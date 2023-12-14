import sys, os

from handlers.phonehandler import PhoneHandler
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
phone_config = config['PHONES']

# Creating a DB client
db = pymssql.connect(default_config['SQLServer'], 'sa', default_config['SQLPassword'], default_config['SQLDBName'])

class PhoneContext(Resource):
    
    handler = PhoneHandler()
    handler.setProperties(db)

    # GET Endpoint 
    def get(self):
        try:  
            args = request.args
            if(len(args)>0):
                # Retriving the current data from the phone
                data = self.handler.getPerson(args['id'])
                # Simulating variation of response latencies
                time.sleep(random.uniform(float(phone_config['MinLatency']), float(phone_config['MaxLatency'])))
                # Return data and 200 OK code
                return parse_response(data[0]), data[1]
            else:
                # Details of all the bicycles on the roads.
                data = self.handler.getPeople()
                # Return data and 200 OK code.
                return data, 200

        except(Exception):
            print('An error occured : ' + traceback.format_exc())

            # Return message and 400 Error code
            return parse_response({'message':'An error occured'}), 400  