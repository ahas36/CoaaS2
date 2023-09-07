import sys, os
import traceback
import configparser

from networks.lstm import LSTMExecutor
sys.path.append(os.path.abspath(os.path.join('.')))

from flask_restful import Resource

from utils.mongoclient import MongoClient

config = configparser.ConfigParser()
config.read(os.getcwd()+'/config.ini')

top_config = config['DEFAULT']

# Creating a DB client
db = MongoClient(top_config['ConnectionString'], top_config['DBName'])

class AgentContext(Resource):
    __executor = None

    def get(self):
        try:         
            if self.__executor == None:
                self.__executor = LSTMExecutor(db, top_config['Lookback'], top_config['Epochs'])
            
            self.__executor.init_training()
            result = self.__executor.predict(top_config['Horizon'])

            # Return data and 200 OK code
            return result, 200

        except(Exception):
            print('An error occured : ' + traceback.format_exc())
            return {'message':'An error occured'}, 500  
