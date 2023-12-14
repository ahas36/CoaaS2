import sys, os
import datetime
import traceback 
from geographiclib.geodesic import Geodesic

sys.path.append(os.path.abspath(os.path.join('.')))

from SingletonMeta import SingletonMeta

class CarHandler(metaclass=SingletonMeta):
    requester = None
    lookedup = dict()
    start_time = datetime.datetime.now()
    
    def setProperties(self, db, set):
        self.__db = db
        self.__set = set

    def getCar(self, vin):
        cursor = self.__db.cursor(as_dict=True)
        try:
            if(self.__set > 1) :
                index = 0
                cursor.execute('SELECT * FROM Car WHERE VIN=%s', vin)
                rows = cursor.fetchall()
                row_count = int(cursor.rowcount)
                curr_time = datetime.datetime.now()
                
                # Mapping time dataset time with current time.
                time_diff = curr_time - self.start_time
                delta_mins = time_diff.total_seconds()/60
                index = int(delta_mins) % row_count
                
                # Commented becuase it only considered responding with the next record.
                # if (vin in self.lookedup):
                #     index = self.lookedup[vin] % row_count
                #     self.lookedup[vin] = self.lookedup[vin] + 1
                # else:
                #     self.lookedup[vin] = 1

                row = rows[index]
                geo = row['Geo'].split('; ')

                prev_row_index = index - 1
                if(prev_row_index < 0): 
                    prev_row_index = row_count - 1
                # Heading calculation
                prev_row = rows[prev_row_index]
                prev_geo = prev_row['Geo'].split('; ')
                heading = self.get_bearing(prev_geo[0], geo[0], prev_geo[1], geo[1])

                response = { 
                    'vin': vin,
                    'mobile': True,
                    'heading': heading,
                    'speed': row['Speed'],
                    'acceleration': row['Acceleration'],
                    'personInside': True if row['PersonInside'] > 1 else False,
                    'doorStatus': row['DoorStatus'],
                    'location': {
                        'longitude': float(geo[1]),
                        'latitude': float(geo[0])
                    },
                    "age": {
                        "value": curr_time.second,
                        "unitText": "s"
                    }
                }
                return response , 200
            else:
                cursor.execute('SELECT * FROM CarSensor WHERE vin=%d', vin)
                row = cursor.fetchall()
                curr_time = datetime.datetime.now()
                row_count = int(cursor.rowcount)
                if(row_count == 0) : 
                    raise Exception('No records found about the given car.')
                car = row[0]

                response = { 
                    'vin': vin,
                    'mobile': True,
                    'speed': car['speed'],
                    'sensorId': car['sensor_id'],
                    'acceleration': car['accelaration'],
                    'personInside': True if car['person_inside'] == 1 else False,
                    'location': {
                        'longitude': car['latitude'],
                        'latitude': car['longitude']
                    },
                    'engineStatus': True if car['engine_start'] == 1 else False,
                    'doorsLocked': True if car['doors_locked'] == 1 else False,
                    'zoneNumber': car['zone_number'],
                    'kerbsideID': car['kerbside_id'],
                    "age": {
                        "value": curr_time.second,
                        "unitText": "s"
                    }
                }
                return response , 200
        except(Exception):
            traceback.print_exc() 
            print('No car found by the given VIN.')
            return { 'error': 'No car by VIN=' + vin }, 500
        
    def getCars(self):
        cursor = self.__db.cursor(as_dict=True)
        try:
            response = []
            cursor.execute('SELECT DISTINCT VIN FROM Car')
            rows = cursor.fetchall()
            for result in rows:
                car, status = self.getCar(result['VIN'])
                if status == 200:
                    response.append(car)
            return response , 200
        except Exception as e:
            print('No cars found.')
            return { 'error': e }, 500

    def get_bearing(self, lat1, lat2, long1, long2):
        brng = Geodesic.WGS84.Inverse(lat1, long1, lat2, long2)['azi1']
        return brng