import sys, os
sys.path.append(os.path.abspath(os.path.join('.')))

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
default_config = config['VEHICLE']

# Creating a DB client
db = MongoClient(default_config['ConnectionString'], default_config['DBName'])

class PlaceContext(Resource):

    current_session = db.insert_one('simulator-sessions', {'time': datetime.now().strftime("%Y-%m-%d %H:%M:%S")})