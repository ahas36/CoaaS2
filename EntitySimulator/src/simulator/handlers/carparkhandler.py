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

            del park['_id']
            del park['occupancy']
            del park['sampling_interval']
            
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

            del park['_id']
            del park['occupancy']
            del park['sampling_interval']

            park['free_slots'] = 0
            park['is_open'] = False

            return park, 200
            

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
        
       
