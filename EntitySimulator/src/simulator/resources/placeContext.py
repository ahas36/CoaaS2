import sys, os

from handlers.placehandler import PlaceHandler
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
place_config = config['PLACE']

# Creating a DB client
db = MongoClient(default_config['ConnectionString'], default_config['DBName'])

class PlaceContext(Resource):
    # current_session = db.insert_one('simulator-sessions', {
    #    'entity-type': 'vehicles',
    #    'time': datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    #    }
    # )

    handler = PlaceHandler()
    handler.setProperties(db, place_config)

    # GET Endpoint 
    def get(self):
        try:            
            # Retriving the measurement
            args = request.args
            csType = args['type']
            data = self.handler.getPlace(args['name'],csType)
            chance = 0.0

            if(csType == "1"):
                # Simulating variation of response latencies for 1st type
                time.sleep(random.uniform(float(place_config['MinLatency']), float(place_config['MaxLatency'])))
                chance = random.uniform(0,1.0)
            else:
                # Simulating variation of response latencies for 2nd type
                time.sleep(random.uniform(0.1,0.5))

            if(chance >= 0.99):
                return parse_response({'message':'The provider faced an unexpected error.'}), 500 

            # Return data and 200 OK code
            # return parse_response(data[0]), data[1]
            return data[0], data[1]

        except(Exception):
            print('An error occured : ' + traceback.format_exc())

            # Return message and 400 Error code
            return parse_response({'message':'An error occured'}), 400  