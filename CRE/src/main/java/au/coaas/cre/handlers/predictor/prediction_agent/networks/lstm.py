import traceback
import numpy as np
import pandas as pd

from utils.observers import Data
from utils.fifoqueue import FIFOQueue 

import torch
import torch.nn as nn
import torch.optim as optim
import torch.utils.data as data

import matplotlib.pyplot  as plt

class LSTMModel(nn.Module):
    def __init__(self):
        super().__init__()
        self.lstm = nn.LSTM(input_size=1, hidden_size=64, num_layers=1, batch_first=True)
        self.linear = nn.Linear(64,1)

    def forward(self, input):
        x_out,_ = self.lstm(input)
        output = self.linear(x_out)
        return output
    
class LSTMExecutor():
    __ready = False
    __pred_count = 0

    def __init__(self, db, lookback, epochs):
        print('Creating a new LSTM model.')
        self.__db = db
        self.__rsmes = FIFOQueue()

        self.__lookback = lookback
        self.__epochs = epochs

        self.__retrain_sub = Data('retrainer')
        self.__retrain_sub.attach(self)

        try:
            self.init_training()
            self.__ready = True
        except Exception:
            print('Exception occured when training the LSTM model.')
            traceback.print_exc()
    
    # Training dataset retrieval
    def create_dataset(self, dataset):
        x,y = [],[]
        for i in range(len(dataset) - self.__lookback):
            feature = dataset[i:i + self.__lookback]
            target = dataset[i+1:i + self.__lookback + 1]

            # If all the dates are in order, then go ahead, else continue
            prev = None
            skip = False
            feature = feature.reset_index()
            for index, row in feature.iterrows():
                if(prev == None):
                    prev = row['time']
                else:
                    if(skip != True and prev > row['time']):
                        skip = True
            if(not skip):
                x.append(feature[['probability']].values.astype('float32'))
                y.append(target[['probability']].values.astype('float32'))

        return torch.tensor(x), torch.tensor(y)

    def get_data_set_csv(self):
        # From a CSVs
        df = pd.read_csv('infered_situations.csv')
        return df[["probability"]].values.astype('float32')
    
    def get_dataset_mongo(self, subscriber):
        _filter = {
            'consumerId': subscriber
        }
        _project = {
            'time' : 1,
            'probability' : 1
        }
        situations = self.__db.read_all('infered_situations', _filter, _project)
        df = pd.DataFrame(situations, columns =['time', 'probability'], dtype = float) 
        
        return df
        # return df[['probability']].values.astype('float32')
    
    def get_consumer_ids(self):
        return self.__db.read_distinct(self, 'infered_situations', 'consumerId')

    # Initial Training
    def init_training(self):
        # If the data is store in the CSV
        # timeseries = self.get_data_set_csv()
        
        # If the data is stored in the Mongo DB
        subscribers = self.get_consumer_ids()
        tsdfs = [] # time-series data frames
        for sub_id in subscribers:
            tsdfs.append(self.get_dataset_mongo(sub_id))
        
        timeseries = pd.concat(tsdfs)

        # Initial train-test split
        self.train_size = int(len(timeseries) * 0.9) 
        self.train, self.test = timeseries[:self.train_size], timeseries[self.train_size:]

        x_train, y_train = self.create_dataset(self.train)
        x_test, y_test = self.create_dataset(self.test)

        self.__model = LSTMModel()
        self.__optimizer = optim.Adam(self.__model.parameters())
        self.__loss_fn = nn.MSELoss()
        self.__loader = data.DataLoader(data.TensorDataset(x_train, y_train), shuffle=False, batch_size=self.__lookback) 

        test_rmse = 0 # Final RMSE for prediction
        for epoch in range(self.__epochs):
            self.__model.train()
            for x_batch,y_batch in self.__loader:
                y_pred = self.__model(x_batch)
                loss = self.__loss_fn(y_pred, y_batch)
                self.__optimizer.zero_grad()
                loss.backward()
                self.__optimizer.step()

            #Validation
            if epoch%100 != 0:
                continue
            self.__model.eval()
            with torch.no_grad():
                y_pred = self.__model(x_train)
                train_rmse = np.sqrt(self.__loss_fn(y_pred, y_train))
                
                y_pred = self.__model(x_test)
                test_rmse = np.sqrt(self.__loss_fn(y_pred, y_test))
            
            print("Epoch %d: train RMSE %.4f, test RMSE %.4f" % (epoch, train_rmse, test_rmse))
        
        self.__rsmes.push(test_rmse)
        # Testing the model
        # visualize(timeseries, x_train, x_test)
        
    def predict(self, horizon):
        if(self.__ready):
            self.__pred_count += 1
            self.__retrain_sub.data = self.__pred_count
            return self.__model.forward(horizon)
        else:
            return None
    
    def visualize(self, timeseries, x_train, x_test):
        with torch.no_grad():
            # Shift train predictions for plotting
            train_plot = np.ones_like(timeseries) * np.nan
            y_pred = self.__model(x_train)
            y_pred = y_pred[:, -1, :]
            train_plot[self.__lookback:self.__train_size] = self.__model(x_train)[:,-1,:]
            
            # Shift train predictions for plotting
            test_plot = np.ones_like(timeseries) * np.nan
            test_plot[self.__train_size + self.__lookback:len(timeseries)] = self.__model(x_test)[:,-1,:]

        plt.plot(timeseries)
        plt.plot(train_plot, c='r')
        plt.plot(test_plot, c='g')
        plt.show()

    # Retraining the model after a set of predictions
    def retrain(self):
        subscribers = self.get_consumer_ids()
        tsdfs = [] # time-series data frames
        for sub_id in subscribers:
            tsdfs.append(self.get_dataset_mongo(sub_id))
        
        timeseries = pd.concat(tsdfs)

        # Initial train-test split
        self.train_size = int(len(timeseries) * 0.9) 
        self.train, self.test = timeseries[:self.train_size], timeseries[self.train_size:]

        x_train, y_train = self.create_dataset(self.train)
        x_test, y_test = self.create_dataset(self.test)

        temp_model = LSTMModel()
        self.__loader = data.DataLoader(data.TensorDataset(x_train, y_train), shuffle=False, batch_size=self.__lookback) 

        test_rmse = 0 
        for epoch in range(self.__epochs):
            temp_model.train()
            for x_batch,y_batch in self.__loader:
                y_pred = temp_model(x_batch)
                loss = self.__loss_fn(y_pred, y_batch)
                self.__optimizer.zero_grad()
                loss.backward()
                self.__optimizer.step()

            #Validation
            if epoch%100 != 0:
                continue
            temp_model.eval()
            with torch.no_grad():
                y_pred = temp_model(x_test)
                test_rmse = np.sqrt(self.__loss_fn(y_pred, y_test))
        
        # This to make sure the system continues to make predictions when the model is being re-trained.
        self.__model = temp_model
        self.__rsmes.push(test_rmse)