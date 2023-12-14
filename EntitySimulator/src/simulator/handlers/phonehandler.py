import sys, os
import datetime

from lib.restapiwrapper import Requester
sys.path.append(os.path.abspath(os.path.join('.')))

from SingletonMeta import SingletonMeta

class PhoneHandler(metaclass=SingletonMeta):
    requester = None
    lookedup = dict()
    start_time = datetime.datetime.now()
    
    def setProperties(self, db):
        self.__db = db

    def getPerson(self, id):
        cursor = self.__db.cursor(as_dict=True)
        try:
            index = 0
            cursor.execute('SELECT * FROM phoneSensor WHERE person_id=%d', id)
            rows = cursor.fetchall()
            row_count = int(cursor.rowcount)
            curr_time = datetime.datetime.now()

            if(row_count == 0):
                    raise Exception('Could not find the given bike.')
            
            time_diff = curr_time - self.start_time
            delta_mins = time_diff.total_seconds()/4
            index = int(delta_mins) % row_count

            row = rows[index]
            
            response = { 
                'id': row['person_id'],
                'sensorId': row['sensor_id'],
                'physicalAge': row['age'],
                'respiratoryRate': row['respiratory_rate'],
                'heartRate': row['heart_rate'],
                "age": {
                    "value": curr_time.second % 4,
                    "unitText": "s"
                }
            }
            return response , 200
        except(Exception):
            print('No peron found by the given ID.')
            return { 'error': 'No person by ID=' + id }, 500
    