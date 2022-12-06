import sys, os

from handlers.weatherhandler import WeatherHandler
sys.path.append(os.path.abspath(os.path.join('.')))

import time
import random
import traceback
import configparser
from datetime import datetime

from lib.mongoclient import MongoClient
from lib.response import parse_response

from flask_restful import Resource
from flask import request

# Global Variables
start_time = datetime.now()
config = configparser.ConfigParser()
config.read(os.getcwd()+'/config.ini')
default_config = config['DEFAULT']
weather_config = config['WEATHER']

# Creating a DB client
db = MongoClient(default_config['ConnectionString'], default_config['DBName'])

class WeatherContext(Resource):

    # current_session = db.insert_one('simulator-sessions', {
    #   'entity-type': 'weather',
    #    'time': datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    #    }
    # )
    
    handler = WeatherHandler()
    handler.setProperties(db)

    # GET Endpoint 
    def get(self):
        try:  
            args = request.args
            chance = 0.0
            chance_2 = 0.0
            # Retriving the measurement
            data = self.handler.getWeather()

            csType = args['type']

            if(csType == "1"):
                # Simulating variation of response latencies
                time.sleep(random.uniform(float(weather_config['MinLatency']), float(weather_config['MaxLatency'])))
            elif(csType == "2"):
                time.sleep(random.uniform(0.5,1.1))
                chance_2 = random.uniform(0,1.0)
            elif(csType == "3"):
                time.sleep(random.uniform(0.8,1.4))
                chance = random.uniform(0,1.0)
            else:
                time.sleep(random.uniform(0.2, 0.7))

            if(chance >= 0.96 or chance_2 > 98):
                return parse_response({'message':'The provider faced an unexpected error.'}), 500 
            # Return data and 200 OK code
            return parse_response(data[0]), data[1]

        except(Exception):
            print('An error occured : ' + traceback.format_exc())

            # Return message and 400 Error code
            return parse_response({'message':'An error occured'}), 400  