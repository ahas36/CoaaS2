import sys, os
import traceback
import configparser
from flask import request

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
    __executor = LSTMExecutor(db, top_config['Lookback'], top_config['Epochs'], 
                              top_config['Horizon'], top_config['MinDataThreshold'])

    def get(self):
        try:     
            # Accepts consumer id and the situation name as inputs from the parameters
            args = request.args  
            horizon = top_config['Epochs']
            consumer_id = args['consumer']
            situation_name = args['situation']
            if('horizon' in args):
                horizon =  args['horizon']
            # Predict the results
            result = self.__executor.predict(consumer_id, situation_name, horizon)
            if result == None:
                return 'Failed', 400
            # Return data and 200 OK code
            return result, 200

        except(Exception):
            print('An error occured : ' + traceback.format_exc())
            return {'message':'An error occured'}, 500  
