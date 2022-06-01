import sys, os

from handlers.carparkhandler import CarparkHandler 
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
vehicle_config = config['CARPARKS']

# Creating a DB client
db = MongoClient(default_config['ConnectionString'], default_config['DBName'])

class CarParkContext(Resource):

    # current_session = db.insert_one('simulator-sessions', {
    #    'entity-type': 'carparks',
    #    'time': datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    #    }
    #)
    
    handler = CarparkHandler()
    handler.setProperties(db)

    # GET Endpoint 
    def get(self):
        try:            
            # Retriving the measurement
            args = request.args
            data = self.handler.getAvailability(args['id'])

            # Simulating variation of response latencies
            time.sleep(random.uniform(float(vehicle_config['MinLatency']), float(vehicle_config['MaxLatency'])))
        
            # Return data and 200 OK code
            return parse_response(data[0]), data[1]

        except(Exception):
            print('An error occured : ' + traceback.format_exc())

            # Return message and 400 Error code
            return parse_response({'message':'An error occured'}), 400  