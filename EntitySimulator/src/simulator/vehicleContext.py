import sys, os

from vehcile.vehiclehandler import VehicleHandler
sys.path.append(os.path.abspath(os.path.join('.')))

import time
import random
import traceback
import configparser
from datetime import datetime

from lib.mongoclient import MongoClient
from lib.response import parse_response

from flask_restful import Resource

# Global Variables
start_time = datetime.now()
config = configparser.ConfigParser()
config.read(os.getcwd()+'/config.ini')
default_config = config['DEFAULT']
vehicle_config = config['VEHICLE']

# Creating a DB client
db = MongoClient(default_config['ConnectionString'], default_config['DBName'])

class VehicleContext(Resource):

    current_session = db.insert_one('simulator-sessions', {
        'entity-type': 'vehicles',
        'time': datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        }
    )
    handler = VehicleHandler()
    handler.setDBClient(db)

    # GET Endpoint 
    def get(self):
        try:            
            # Retriving the measurement
            data = self.handler.getVehicle()

            # Simulating variation of response latencies
            time.sleep(random.uniform(float(vehicle_config['MinLatency']), float(vehicle_config['MaxLatency'])))
        
            # Return data and 200 OK code
            return parse_response(data[0]), data[1]

        except(Exception):
            print('An error occured : ' + traceback.format_exc())

            # Return message and 400 Error code
            return parse_response({'message':'An error occured'}), 400  