from datetime import datetime
import sys, os

sys.path.append(os.path.abspath(os.path.join('.')))

from SingletonMeta import SingletonMeta

class CarparkHandler(metaclass=SingletonMeta):
    def setProperties(self, db):
        self.__db = db

    def getAvailability(self, id):
        now = datetime.now()
        date = getDate(now.weekday())

        try:
            park = self.__db.read_single('carparks', {'reference': id, 'open_hours.day':date.capitalize()})
            
            sampling = park['sampling_interval']['value']
            num = 0
            if sampling == 60000:
                num = 1440
            elif sampling == 600000:
                num = 144
            elif sampling == 1800000:
                num = 48
            else:
                num = 24
                     
            hour = now.hour
            min = now.minute

            total_mins = hour*60 + min
            index = round(total_mins/num)
            capacity = park['capacity']
            variation = park['occupancy'][date]
            current_availability = capacity - variation[index]
            park['ownedBy'] = {
                'name': park['owner']
            }
            park['primary_location'] = {
                'address': park['address'],
                'latitude': park['location']['lat'],
                'longitude': park['location']['long']
            }
            park['currenciesAccepted'] = 'AUD'
            park['maxHeight'] = {
                "value": park['gates'][0]['height']['value'],
                "unitText": park['gates'][0]['height']['unit']
            }

            if 'hourly' in park['price']:
                for off in park['price']['hourly']:
                    if off['max'] >= 1:
                        park['price_offer'] = {
                            'price': off['amount'],
                            'priceCurrency': 'AUD'
                        }
                        break
            elif 'flat' in park['price']:
                park['price_offer'] = {
                    'price': park['price']['flat'],
                    'priceCurrency': 'AUD'
                }

            del park['_id']
            del park['occupancy']
            del park['sampling_interval']
            del park['owner']
            del park['address']
            del park['location']
            del park['price']
            
            schedule = park['open_hours'][now.weekday()]
            open = int(schedule['open'].split(':')[0])
            close = int(schedule['close'].split(':')[0])

            isOpen = hour>=open and hour<=close
            if close < open:
                if (hour <=23 and hour >= open) or (hour<open and hour<close):
                    isOpen = True
                else:
                    isOpen = False
                    
            if isOpen:
                park['free_slots'] = current_availability
                park['is_open'] = True
            else:
                park['free_slots'] = 0
                park['is_open'] = False

            return park, 200

        except:
            park = self.__db.read_single('carparks', {'reference': id})
            park['ownedBy'] = {
                'name': park['owner']
            }
            park['primary_location'] = {
                'address': park['address'],
                'latitude': park['location']['lat'],
                'longitude': park['location']['long']
            }
            park['currenciesAccepted'] = 'AUD'

            if 'hourly' in park['price']:
                for off in park['price']['hourly']:
                    if off['max'] >= 1:
                        park['price_offer'] = {
                            'price': off['amount'],
                            'priceCurrency': 'AUD'
                        }
                        break
            elif 'flat' in park['price']:
                park['price_offer'] = {
                    'price': park['price']['flat'],
                    'priceCurrency': 'AUD'
                }
            
            del park['_id']
            del park['occupancy']
            del park['sampling_interval']
            del park['owner']
            del park['address']
            del park['location']
            del park['price']

            park['free_slots'] = 0
            park['is_open'] = False

            return park, 200      

    def getCarparks(self, args):
        now = datetime.now()
        date = getDate(now.weekday())

        rating = int(args['rating']) if 'rating' in args else 0

        filters = {
            'open_hours.day':date.capitalize(),
            'rating': {'$gte':rating}
        }

        parks = self.__db.read_all('carparks', filters)

        selected_parks = []

        for park in parks:
            sampling = park['sampling_interval']['value']
            num = 0
            if sampling == 60000:
                num = 1440
            elif sampling == 600000:
                num = 144
            elif sampling == 1800000:
                num = 48
            else:
                num = 24
                    
            hour = now.hour
            min = now.minute

            total_mins = hour*60 + min
            index = round(total_mins/num)
            capacity = park['capacity']
            variation = park['occupancy'][date]
            current_availability = capacity - variation[index]

            if current_availability < int(args['availableSlots']):
                continue

            park['ownedBy'] = {
                'name': park['owner']
            }
            park['primary_location'] = {
                'address': park['address'],
                'latitude': park['location']['lat'],
                'longitude': park['location']['long']
            }
            park['currenciesAccepted'] = 'AUD'
            
            if 'height' in park['gates'][0] and park['gates'][0]['height']['value'] >= float(args['maxHeight']):
                park['maxHeight'] = {
                    "value": park['gates'][0]['height']['value'],
                    "unitText": park['gates'][0]['height']['unit']
                }
            else:
                continue

            if 'hourly' in park['price']:
                for off in park['price']['hourly']:
                    if off['max'] >= 1:
                        park['price_offer'] = {
                            'price': off['amount'],
                            'priceCurrency': 'AUD'
                        }
                        break
            elif 'flat' in park['price']:
                park['price_offer'] = {
                    'price': park['price']['flat'],
                    'priceCurrency': 'AUD'
                }

            if 'price' in args and park['price_offer']['price'] > float(args['price']):
                continue

            del park['_id']
            del park['occupancy']
            del park['sampling_interval']
            del park['owner']
            del park['address']
            del park['location']
            del park['price']
            
            schedule = park['open_hours'][now.weekday()]
            open = int(schedule['open'].split(':')[0])
            close = int(schedule['close'].split(':')[0])

            isOpen = hour>=open and hour<=close
            if close < open:
                if (hour <=23 and hour >= open) or (hour<open and hour<close):
                    isOpen = True
                else:
                    isOpen = False
                    
            if isOpen and bool(args['isOpen']):
                park['free_slots'] = current_availability
                park['is_open'] = True
            else:
                continue
            
            selected_parks.append(park)
        
        return { 'results': selected_parks }, 200

def getDate(day):
    if day == 0:
        return 'monday'
    if day == 1:
        return 'tuesday'
    if day == 2:
        return 'wednesday'
    if day == 3:
        return 'thursday'
    if day == 4:
        return 'friday'
    if day == 5:
        return 'saturday'
    if day == 6:
        return 'sunday'
        


           
