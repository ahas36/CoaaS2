import sys, os
sys.path.append(os.path.abspath(os.path.join('.')))

import time
import random
import traceback
import configparser
from datetime import datetime
from configuration import CarParkConfiguration
from carpark.carParkFactory import CarParkFactory

from lib.mongoclient import MongoClient
from lib.response import parse_response

from flask_restful import Resource

# Global Variables
start_time = datetime.now()
config = configparser.ConfigParser()
config.read(os.getcwd()+'/config.ini')

top_config = config['DEFAULT']
default_config = config['SINGLECARPARK']

# Creating a DB client
db = MongoClient(top_config['ConnectionString'], top_config['DBName'])

class SimCarParkContext(Resource):
    # Saving the current session 
    # current_session = db.insert_one('simulator-sessions', {'time': datetime.now().strftime("%Y-%m-%d %H:%M:%S")})
    current_session = 'default_session'

    # Setting up configuration
    configuration = CarParkConfiguration(current_session, sample_size = int(default_config['SampleSize']), 
        standard_deviation = default_config['StandardDeviation'], total_time = float(default_config['TotalTime']), 
        skew = default_config['Skew'], sampling_rate = float(default_config['SamplingRate']), 
        variation=default_config['ValueChange'].split(','), planning_period = default_config['PlanningPeriod'], 
        selected_periods = default_config['SelectedPeriods'], random_noise = True if default_config['RandomNoise'] == 'True' else False,
        noise_percentage = float(default_config['NoisePercentage']), min_occupancy = float(default_config['MinOccupancy']))
    
    # Requesting a car park instance from factory
    carpark_factory = CarParkFactory(configuration)
    carpark = carpark_factory.get_carpark()

    meta = {
        'start_time': str(start_time),
        'sampling_rate': float(default_config['SamplingRate'])
    }

    # GET Endpoint 
    def get(self):
        try:   
            # Calculating the current time step from start
            curr_time = datetime.now()
            time_diff = (curr_time - start_time).total_seconds()*1000
            
            # Retriving the measurement
            data = self.carpark.get_current_status(time_diff)
            
            # Simulating variation of response latencies
            time.sleep(random.uniform(float(default_config['MinLatency']), float(default_config['MaxLatency'])))
            
            # Return data and 200 OK code
            return parse_response(data, meta=self.meta), 200  

        except(Exception):
            print('An error occured : ' + traceback.format_exc())

            # Return message and 400 Error code
            return parse_response({'message':'An error occured'}), 400  