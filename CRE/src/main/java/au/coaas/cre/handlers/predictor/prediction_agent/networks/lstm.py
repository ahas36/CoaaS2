import traceback
import numpy as np
import pandas as pd

import os.path
import threading

from utils.observers import Data

import torch
import torch.nn as nn
import torch.optim as optim
import torch.utils.data as data

import matplotlib.pyplot  as plt

from utils.trainedmodel import TrainedModel

from statsmodels.tsa.api import SimpleExpSmoothing

LSTM_EXTN = "-lstm.pth"
LINE_EXTN = "-linear.pth"

class LSTMModel(nn.Module):
    def __init__(self, path):
        super().__init__()
        self.__path = path
        self.lstm = nn.LSTM(input_size=1, hidden_size=64, num_layers=1, batch_first=True)
        self.linear = nn.Linear(64,1)

        if(os.path.isfile('models/' + path + LSTM_EXTN)):
            self.load_model()

    # Predict linearly
    def forward(self, input):
        x_out,_ = self.lstm(input)
        output = self.linear(x_out)
        return output
    
    # Save the model
    def save_model(self):
        torch.save(self.lstm.state_dict(), 'models/' + self.__path + LSTM_EXTN)
        torch.save(self.linear.state_dict(), 'models/' + self.__path + LINE_EXTN)

    # Load saved models
    def load_model(self):
        self.lstm.load_state_dict(torch.load('models/' + self.__path + LSTM_EXTN))
        self.linear.load_state_dict(torch.load('models/' + self.__path + LINE_EXTN))
    
class LSTMExecutor():
    __ready = False
    __pred_count = 0

    def __init__(self, db, lookback, epochs):
        print('Creating new LSTM models for each consumer-event pairs.')
        self.__db = db
        self.__refs = {}
        self.__wipset = set()

        self.__lookback = lookback
        self.__epochs = epochs

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
    
    def get_dataset_mongo(self, consumer, situation):
        _filter = {
            'consumerId': consumer,
            'situationName': situation
        }
        _project = {
            'time' : 1,
            'probability' : 1
        }
        situations = self.__db.read_all('infered_situations', _filter, _project)
        df = pd.DataFrame(situations, columns =['time', 'probability'], dtype = float) 
        
        return df
    
    # Get distinct consumer and situation pairs
    def get_consumer_ids(self):
        return self.__db.read_distinct(self, 'infered_situations', ['consumerId','situationName'])

    # Initial Training
    def init_training(self):
        # If the data is store in the CSV
        # timeseries = self.get_data_set_csv()
        
        # If the data is stored in the Mongo DB
        # Get all the consumer-event pairs for training specific models
        subscribers = self.get_consumer_ids()

        for sub in subscribers:
            pair = sub['_id']
            self.__learn(pair['consumerId'], pair['situationName'])
            
    def __learn(self, consumer_id, situation_name):   
        path = consumer_id + '-' + situation_name
        
        # Checking if there is a model already being trained.
        if(path in self.__wipset):
            return 
        
        # Setting WPI flag
        self.__wipset.add(path)

        timeseries = self.get_dataset_mongo(consumer_id, situation_name)

        # Initial train-test split
        train_size = int(len(timeseries) * 0.9) 
        train, test = timeseries[:train_size], timeseries[train_size:]

        x_train, y_train = self.create_dataset(train)
        x_test, y_test = self.create_dataset(test)

        model = LSTMModel(path)
        optimizer = optim.Adam(model.parameters())
        loss_fn = nn.MSELoss()
        loader = data.DataLoader(data.TensorDataset(x_train, y_train), shuffle=False, batch_size=self.__lookback) 

        test_rmse = 0 # Final RMSE for prediction
        for epoch in range(self.__epochs):
            model.train()
            for x_batch,y_batch in loader:
                y_pred = model(x_batch)
                loss = loss_fn(y_pred, y_batch)
                optimizer.zero_grad()
                loss.backward()
                optimizer.step()

            #Validation
            if epoch%100 != 0:
                continue
            model.eval()
            with torch.no_grad():
                y_pred = model(x_train)
                train_rmse = np.sqrt(loss_fn(y_pred, y_train))
                
                y_pred = model(x_test)
                test_rmse = np.sqrt(loss_fn(y_pred, y_test))
            
            print("Epoch %d: train RMSE %.4f, test RMSE %.4f" % (epoch, train_rmse, test_rmse))
        
        # Save the model with a path
        model.save_model()

        # Persiting model
        trained_model = TrainedModel(path, optimizer, loss_fn, loader)
        trained_model.__setattr__('train_size', train_size)
        trained_model.rsmes.push(test_rmse)
        self.__refs[consumer_id] = {
            situation_name : trained_model
        }

        # Clearning the WIP flag
        self.__wipset.discard(path)

        # Model retrian subscriptions
        retrain_sub = Data(path)
        retrain_sub.attach(self)
        trained_model.__setattr__('subscription', retrain_sub)

        # Testing the model
        # visualize(timeseries, model, train_size, x_train, x_test)
    
    def predict(self, consumer_id, situation_name, horizon):
        if(self.__ready):
            if((consumer_id in self.__refs) and (situation_name in self.__refs[consumer_id])):
                # There is an already trained model for the consumer and event.
                path = consumer_id + '-' + situation_name
                model = LSTMModel(path)
                
                pred_value = model.forward(horizon)
                
                self.__pred_count += 1
                model_obj = self.__refs[consumer_id][situation_name]
                model_obj.subscription.data(model_obj.subscription.data()+1)

                return pred_value
            else:
                # There is no trained model at the moment. So, triggering a learning.
                self.__background_train()
                self.__moving_average(consumer_id, situation_name, horizon)
                # What to return when there is no model to predict with?
                # 
                return None
        else:
            return None

    def __moving_average(self, consumer_id, situation_name, horizon):
        timeseries = self.get_dataset_mongo(consumer_id, situation_name)
        # Exponential smoothing
        reliance = timeseries['probability'].to_frame()
        reliance['mvavg'] = reliance['probability'].ewm(span=10).mean()
        # Prediction
        predictions = self.__predict_next(reliance['mvavg'].tolist().reverse(), horizon)
        return predictions

    def __predict_next(self, arr, horizon):
        fit_model = SimpleExpSmoothing(arr, initialization_method="estimated").fit()
        # Returns a nd-array of the forecasts
        return fit_model.forecast(horizon)

    def fire_and_forget(f):
        def wrapped():
            threading.Thread(target=f).start()
        return wrapped

    @fire_and_forget
    def __background_train(self, consumer_id, situation_name):
        self.__learn(consumer_id, situation_name)
    
    def visualize(self, timeseries, model, train_size, x_train, x_test):
        with torch.no_grad():
            # Shift train predictions for plotting
            train_plot = np.ones_like(timeseries) * np.nan
            y_pred = model(x_train)
            y_pred = y_pred[:, -1, :]
            train_plot[self.__lookback:train_size] = model(x_train)[:,-1,:]
            
            # Shift train predictions for plotting
            test_plot = np.ones_like(timeseries) * np.nan
            test_plot[train_size + self.__lookback:len(timeseries)] = model(x_test)[:,-1,:]

        plt.plot(timeseries)
        plt.plot(train_plot, c='r')
        plt.plot(test_plot, c='g')
        plt.show()

    # Retraining the model after a set of predictions
    def retrain(self, path):
        sub = path.split('-')
        timeseries = self.get_dataset_mongo(sub[0], sub[1])

        model_obj = self.__refs[sub[0]][sub[1]]
        
        # Initial train-test split
        train_size = int(len(timeseries) * 0.9) 
        model_obj.__setattr__('train_size', train_size)
        train, test = timeseries[:train_size], timeseries[train_size:]

        x_train, y_train = self.create_dataset(train)
        x_test, y_test = self.create_dataset(test)

        temp_model = LSTMModel(path)
        loader = data.DataLoader(data.TensorDataset(x_train, y_train), shuffle=False, batch_size=self.__lookback) 

        test_rmse = 0 
        for epoch in range(self.__epochs):
            temp_model.train()
            for x_batch,y_batch in loader:
                y_pred = temp_model(x_batch)
                loss = model_obj.get_loss_fn(y_pred, y_batch)
                model_obj.get_optimizer.zero_grad()
                loss.backward()
                model_obj.get_optimizer.step()

            #Validation
            if epoch%100 != 0:
                continue
            temp_model.eval()
            with torch.no_grad():
                y_pred = temp_model(x_test)
                test_rmse = np.sqrt(model_obj.get_loss_fn(y_pred, y_test))

        # This to make sure the system continues to make predictions when the model is being re-trained.
        # Persisting updates in the model
        model_obj.set_loader(loader)
        model_obj.rsmes.push(test_rmse)
        self.__refs[sub[0]] = {
                sub[1] : model_obj
            }

        # Updating the saved model
        temp_model.save_model()