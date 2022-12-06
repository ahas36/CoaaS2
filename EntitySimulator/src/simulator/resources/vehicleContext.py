import sys, os

from handlers.vehiclehandler import VehicleHandler
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
vehicle_config = config['VEHICLE']

# Creating a DB client
db = MongoClient(default_config['ConnectionString'], default_config['DBName'])

class VehicleContext(Resource):

    # current_session = db.insert_one('simulator-sessions', {
    #    'entity-type': 'vehicles',
    #    'time': datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    #    }
    # )
    
    handler = VehicleHandler()
    handler.setDBClient(db)

    # GET Endpoint 
    def get(self):
        try:   
            args = request.args
            chance = 0.0
            chance_2 = 0.0

            csType = args['type']
            # Retriving the measurement
            data = self.handler.getVehicle(args['vin'], csType)

            if(csType == "1"):
                # Simulating variation of response latencies
                time.sleep(random.uniform(float(vehicle_config['MinLatency']), float(vehicle_config['MaxLatency'])))
            elif(csType == "2"):
                time.sleep(random.uniform(0.5,0.75))
            elif(csType == "3"):
                time.sleep(random.uniform(0.5,1.0))
                chance_2 = random.uniform(0,1.0)
            elif(csType == "4"):
                time.sleep(random.uniform(0.6,1.1))
                chance = random.uniform(0,1.0)
            elif(csType == "5"):
                time.sleep(random.uniform(0.3,0.6))
            elif(csType == "6"):
                time.sleep(random.uniform(0.75,1.2))
                chance_2 = random.uniform(0,1.0)
            elif(csType == "7"):
                time.sleep(random.uniform(0.8,1.7))
                chance = random.uniform(0,1.0)
            elif(csType == "8"):
                time.sleep(random.uniform(0.8,1.0))
            elif(csType == "9"):
                time.sleep(random.uniform(0.3,0.5))
            else:
                time.sleep(random.uniform(1.2,2.2))
                chance = random.uniform(0,1.0)
        
            if(chance >= 0.97 or chance >= 0.99):
                return parse_response({'message':'The provider faced an unexpected error.'}), 500 
            # Return data and 200 OK code
            return parse_response(data[0]), data[1]

        except(Exception):
            print('An error occured : ' + traceback.format_exc())

            # Return message and 400 Error code
            return parse_response({'message':'An error occured'}), 400  