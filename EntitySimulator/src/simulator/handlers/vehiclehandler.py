import sys, os
sys.path.append(os.path.abspath(os.path.join('.')))

from SingletonMeta import SingletonMeta

class VehicleHandler(metaclass=SingletonMeta):
    def setDBClient(self, db):
        self.__db = db

    def getVehicle(self, vin):
        vehicle = self.__db.read_single('vehicles', {'vin':vin})
    
        if vehicle == None:
            return {}, 404
        else:
            location = self.__db.read_single('query-params', {'vin':vin})

            return {
                'vin': vin,
                'plateNumber': vehicle['plate-no'],
                'specifications': {
                    'make': vehicle['make'],
                    'type': vehicle['body-style'],
                    'dimensions': {
                        'height': vehicle['dimensions']['height'],
                        'width': vehicle['dimensions']['width'],
                        'length': vehicle['dimensions']['length'],
                        'unit': vehicle['dimensions']['unit'],
                        'wheelBase': vehicle['wheel-base']
                    }
                },
                'performance': vehicle['milage'],
                'telemetry': {
                    'location': {
                        'latitude': location['location']['lat'],
                        'longitude': location['location']['lng'],
                    },
                    'speed': {
                        'value': 60,
                        'unit': 'kmph'
                    }
                }   
            }, 200