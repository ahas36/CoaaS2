import sys, os
import datetime

sys.path.append(os.path.abspath(os.path.join('.')))

from SingletonMeta import SingletonMeta

class CarHandler(metaclass=SingletonMeta):
    requester = None
    lookedup = dict()
    
    def setProperties(self, db):
        self.__db = db

    def getCar(self, vin):
        cursor = self.__db.cursor(as_dict=True)
        try:
            index = 0
            cursor.execute('SELECT * FROM Car WHERE VIN=%s', vin)
            rows = cursor.fetchall()
            row_count = int(cursor.rowcount)
            if (vin in self.lookedup):
                index = self.lookedup[vin] % row_count
                self.lookedup[vin] = self.lookedup[vin] + 1
            else:
                self.lookedup[vin] = 1

            row = rows[index]
            geo = row['Geo'].split('; ')
            time = datetime.datetime.now()

            response = { 
                'vin': vin,
                'speed': row['Speed'],
                'acceleration': row['Acceleration'],
                'personInside': True if row['PersonInside'] > 1 else False,
                'doorStatus': row['DoorStatus'],
                'location': {
                    'longitude': float(geo[1]),
                    'latitude': float(geo[0])
                },
                "age": {
                    "value": time.second,
                    "unitText": "s"
                }
            }
            return response , 200
        except(Exception):
            print('No car found by the given VIN.')
            return { 'error': 'No car by VIN=' + vin }, 500