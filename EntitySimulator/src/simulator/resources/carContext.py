import sys, os

from handlers.carhandler import CarHandler
sys.path.append(os.path.abspath(os.path.join('.')))

import time
import random
import pymssql
import traceback
import configparser
from datetime import datetime
from lib.response import parse_response

from flask_restful import Resource
from flask import request

# Global Variables
start_time = datetime.now()
config = configparser.ConfigParser()
config.read(os.getcwd()+'/config.ini')
default_config = config['DEFAULT']
cars_config = config['CARS']

# Creating a DB client
db = pymssql.connect(default_config['SQLServer'], 'sa', default_config['SQLPassword'], default_config['SQLDBName'])

class CarContext(Resource):   
    handler = CarHandler()
    handler.setProperties(db)

    # GET Endpoint 
    def get(self):
        try:  
            args = request.args

            # Retriving the current state of the car
            data = self.handler.getCar(args['vin'])

            # Simulating variation of response latencies
            time.sleep(random.uniform(float(cars_config['MinLatency']), float(cars_config['MaxLatency'])))
        
            # Return data and 200 OK code
            return parse_response(data[0]), data[1]

        except(Exception):
            print('An error occured : ' + traceback.format_exc())

            # Return message and 400 Error code
            return parse_response({'message':'An error occured'}), 400  