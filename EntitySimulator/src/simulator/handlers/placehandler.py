import sys, os
import datetime

from lib.restapiwrapper import Requester
sys.path.append(os.path.abspath(os.path.join('.')))

from SingletonMeta import SingletonMeta

class PlaceHandler(metaclass=SingletonMeta):
    requester = None
    
    def setProperties(self, db, config):
        self.__db = db
        self.__config = config

    def getPlace(self, address, type):
        place = self.__db.read_single('places', {'name':address})
        time = datetime.datetime.now()
    
        if place == None:
            """Go to Google Maps and get the result"""
            if self.requester == None:
                self.requester = Requester()
            
            url = self.__config['GoogleAPI']
            query = address+' in Melbourne'

            uri = url.format(query.replace(' ', '%20'), self.__config['GoogleKey'])
            response = self.requester.get_response(uri)

            if(response['status'] == 'OK'):
                return response['results'], 200

            return [],200
        else:
            del place['_id']
            age = time.second
            if(type == "1"):
                age = age + (60*time.minute)
                
            place['primary_location'] = {
                'address': place['formatted_address'],
                'latitude': place['geometry']['location']['lat'],
                'longitude': place['geometry']['location']['lng']
            }
            place['age'] = {
                'value':age,
                'unitText': 's'
            }

            return [place], 200