import sys, os
import datetime
from geographiclib.geodesic import Geodesic

from lib.restapiwrapper import Requester
sys.path.append(os.path.abspath(os.path.join('.')))

from SingletonMeta import SingletonMeta

class BicycleHandler(metaclass=SingletonMeta):
    requester = None
    lookedup = dict()
    speedIndex = 0
    
    def setProperties(self, db):
        self.__db = db

    def getBicycle(self, vin):
        cursor = self.__db.cursor(as_dict=True)
        try:
            index = 0
            cursor.execute('SELECT * FROM Bicycle WHERE VIN=%s', vin)
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
                'brakes': row['Brakes'],
                'roadNumber': row['RoadNumber'],
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
        except(Exception):
            print('No bicylce found by the given VIN.')
            return { 'error': 'No bicycle by VIN=' + vin }, 500
    
    def getBicycles(self, speed):
        return {} , 200
    
    def getAllBicycles(self):
        cursor = self.__db.cursor(as_dict=True)
        try:
            response = []
            cursor.execute('SELECT DISTINCT VIN FROM Bicycle')
            rows = cursor.fetchall()
            for result in rows:
                car, status = self.getBicycle(result['VIN'])
                if status == 200:
                    response.append(car)
            return response , 200
        except Exception as e:
            print('No cars found.')
            return { 'error': e }, 500

    def get_bearing(self, lat1, lat2, long1, long2):
        brng = Geodesic.WGS84.Inverse(lat1, long1, lat2, long2)['azi1']
        return brng