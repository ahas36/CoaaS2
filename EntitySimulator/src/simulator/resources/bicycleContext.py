import sys, os

from handlers.bicyclehandler import BicycleHandler
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
bicycle_config = config['BICYCLES']

# Creating a DB client
db = pymssql.connect(default_config['SQLServer'], 'sa', default_config['SQLPassword'], default_config['SQLDBName'])

class BicycleContext(Resource):
    
    handler = BicycleHandler()
    handler.setProperties(db, int(bicycle_config['Set']))

    # GET Endpoint 
    def get(self):
        try:  
            args = request.args
            if(len(args)>0):
                data = {}
                # Retriving the current data from the bicycle
                if('vin' in args):
                    data = self.handler.getBicycle(args['vin'])
                elif('speed' in args):
                    data = self.handler.getBicycles(args['speed'])
                # Simulating variation of response latencies
                time.sleep(random.uniform(float(bicycle_config['MinLatency']), float(bicycle_config['MaxLatency'])))
                # Return data and 200 OK code
                return parse_response(data[0]), data[1]
            else:
                # Details of all the bicycles on the roads.
                data, status = self.handler.getAllBicycles()
                # Return data and 200 OK code.
                return data, status

        except(Exception):
            print('An error occured : ' + traceback.format_exc())

            # Return message and 400 Error code
            return parse_response({'message':'An error occured'}), 400  