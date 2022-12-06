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
            csType = args['type']
            chance = 0.0
            chance_2 = 0.0

            if len(args) > 1:
                data = self.handler.getCarparks(args)
                # Simulating variation of response latencies
                if(csType == '81' or csType == '82'):
                    time.sleep(random.uniform(float(vehicle_config['MinLatency']), float(vehicle_config['MaxLatency'])))
                elif(csType == '71' or csType == '64' or csType == '43' or csType == '22'):
                    time.sleep(random.uniform(0.6,1.0))
                    chance = random.uniform(0,1.0)
                elif(csType == '71' or csType == '63' or csType == '41' or csType == '21'):
                    time.sleep(random.uniform(0.4,1.1))
                    chance_2 = random.uniform(0,1.0)
                elif(csType == '61' or csType == '53'):
                    time.sleep(random.uniform(0.3,0.5))
                elif(csType == '51'):
                    time.sleep(random.uniform(0.2,0.45))
                elif(csType == '62'):
                    time.sleep(random.uniform(0.3,0.76))
                elif(csType == '54'):
                    time.sleep(random.uniform(0.4,0.72))
                elif(csType == '65' or csType == '52' or csType == '32' or csType == '33' or csType == '23'):
                    time.sleep(random.uniform(0.3,0.9))
                elif(csType == '55' or csType == '31'):
                    time.sleep(random.uniform(0.6,1.0))
                    chance_2 = random.uniform(0,1.0)
                elif(csType == '42'):
                    time.sleep(random.uniform(1.0,2.5))
                    chance = random.uniform(0,1.0)
                elif(csType == '11'):
                    time.sleep(random.uniform(0.8,3.0))
                    chance = random.uniform(0,1.0)
            else:
                data = self.handler.getAvailability(args['id'])
                time.sleep(random.uniform(0.4,0.6))

            if(chance >= 0.95 or chance_2 >= 0.98):
                return parse_response({'message':'The provider faced an unexpected error.'}), 500 
            # Return data and 200 OK code
            return data[0], data[1]

        except(Exception):
            print('An error occured : ' + traceback.format_exc())

            # Return message and 400 Error code
            return parse_response({'message':'An error occured'}), 400  