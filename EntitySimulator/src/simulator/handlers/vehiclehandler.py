import sys, os
sys.path.append(os.path.abspath(os.path.join('.')))

from SingletonMeta import SingletonMeta

class VehicleHandler(metaclass=SingletonMeta):
    def setDBClient(self, db):
        self.__db = db

    def getVehicle(self, vin, csType):
        vehicle = self.__db.read_single('vehicles', {'vin':vin})
    
        if vehicle == None:
            return {}, 404
        else:
            location = self.__db.read_single('query-params', {'vin':vin})

            if(csType == "1" or csType == "5" or csType == "8" or csType == "9"):
                return {
                    'vin': vin,
                    'plateNumber': vehicle['plate-no'],
                    'specifications': {
                        'make': vehicle['make'],
                        'type': vehicle['body-style']
                    },
                    'height': {
                        'value': vehicle['dimensions']['height'],
                        'unitText': 'm'
                    },
                    'width': {
                        'value': vehicle['dimensions']['width'],
                        'unitText': 'm'
                    },
                    'length': {
                        'value': vehicle['dimensions']['length'],
                        'unitText': 'm'
                    },
                    'unit': {
                        'value': vehicle['dimensions']['unit'],
                        'unitText': 'm'
                    },
                    'wheel-base': {
                        'value': vehicle['wheel-base'],
                        'unitText': 'm'
                    },
                    'performance': {
                        'value': vehicle['milage']['city'],
                        'unitText': 'liters per kilometer'
                    },
                    'location': {
                        'latitude': location['location']['lat'],
                        'longitude': location['location']['lng']
                    },
                    'speed': {
                        'value': 60,
                        'unitText': 'kmph'
                    } 
                }, 200
            elif(csType == "2"):
                return {
                    'vin': vin,
                    'plateNumber': vehicle['plate-no'],
                    'height': {
                        'value': vehicle['dimensions']['height'],
                        'unitText': 'm'
                    },
                    'width': {
                        'value': vehicle['dimensions']['width'],
                        'unitText': 'm'
                    },
                    'length': {
                        'value': vehicle['dimensions']['length'],
                        'unitText': 'm'
                    },
                    'location': {
                        'latitude': location['location']['lat'],
                        'longitude': location['location']['lng']
                    },
                    'speed': {
                        'value': 60,
                        'unitText': 'kmph'
                    } 
                }, 200
            elif(csType == "3"):
                return {
                    'vin': vin,
                    'plateNumber': vehicle['plate-no'],
                    'specifications': {
                        'make': vehicle['make'],
                        'type': vehicle['body-style']
                    },
                    'height': {
                        'value': vehicle['dimensions']['height'],
                        'unitText': 'm'
                    },
                    'width': {
                        'value': vehicle['dimensions']['width'],
                        'unitText': 'm'
                    },
                    'location': {
                        'latitude': location['location']['lat'],
                        'longitude': location['location']['lng']
                    },
                    'speed': {
                        'value': 60,
                        'unitText': 'kmph'
                    } 
                }, 200
            elif(csType == "4"):
                return {
                    'vin': vin,
                    'plateNumber': vehicle['plate-no'],
                    'specifications': {
                        'make': vehicle['make'],
                        'type': vehicle['body-style']
                    },
                    'height': {
                        'value': vehicle['dimensions']['height'],
                        'unitText': 'm'
                    },
                    'width': {
                        'value': vehicle['dimensions']['width'],
                        'unitText': 'm'
                    },
                    'length': {
                        'value': vehicle['dimensions']['length'],
                        'unitText': 'm'
                    },
                    'performance': {
                        'value': vehicle['milage']['city'],
                        'unitText': 'liters per kilometer'
                    },
                    'location': {
                        'latitude': location['location']['lat'],
                        'longitude': location['location']['lng']
                    },
                    'speed': {
                        'value': 60,
                        'unitText': 'kmph'
                    } 
                }, 200
            elif(csType == "6"):
                return {
                    'vin': vin,
                    'height': {
                        'value': vehicle['dimensions']['height'],
                        'unitText': 'm'
                    },
                    'width': {
                        'value': vehicle['dimensions']['width'],
                        'unitText': 'm'
                    },
                    'length': {
                        'value': vehicle['dimensions']['length'],
                        'unitText': 'm'
                    },
                    'performance': {
                        'value': vehicle['milage']['city'],
                        'unitText': 'liters per kilometer'
                    },
                    'location': {
                        'latitude': location['location']['lat'],
                        'longitude': location['location']['lng']
                    },
                    'speed': {
                        'value': 60,
                        'unitText': 'kmph'
                    } 
                }, 200
            elif(csType == "7"):
                return {
                    'vin': vin,
                    'specifications': {
                        'make': vehicle['make'],
                        'type': vehicle['body-style']
                    },
                    'height': {
                        'value': vehicle['dimensions']['height'],
                        'unitText': 'm'
                    },
                    'location': {
                        'latitude': location['location']['lat'],
                        'longitude': location['location']['lng']
                    },
                    'speed': {
                        'value': 60,
                        'unitText': 'kmph'
                    } 
                }, 200
            else:
                return {
                    'vin': vin,
                    'plateNumber': vehicle['plate-no'],
                    'specifications': {
                        'make': vehicle['make'],
                        'type': vehicle['body-style']
                    },
                    'height': {
                        'value': vehicle['dimensions']['height'],
                        'unitText': 'm'
                    },
                    'width': {
                        'value': vehicle['dimensions']['width'],
                        'unitText': 'm'
                    },
                    'length': {
                        'value': vehicle['dimensions']['length'],
                        'unitText': 'm'
                    },
                    'location': {
                        'latitude': location['location']['lat'],
                        'longitude': location['location']['lng']
                    },
                    'speed': {
                        'value': 60,
                        'unitText': 'kmph'
                    } 
                }, 200

            