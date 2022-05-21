import sys, os
import datetime

from lib.restapiwrapper import Requester
sys.path.append(os.path.abspath(os.path.join('.')))

from SingletonMeta import SingletonMeta

class WeatherHandler(metaclass=SingletonMeta):
    requester = None
    
    def setProperties(self, db):
        self.__db = db

    def getWeather(self):
        time = datetime.datetime.now()
        month = time.month
        if month<10:
            month = '0'+str(month)
        else:
            month = str(month)

        date = time.day
        if date<10:
            date = '0'+str(date)
        else:
            date = str(date)

        hour = time.hour
        if hour<10:
            hour = '0'+str(hour)
        else:
            hour = str(hour)

        curr_time = "2019{0}{1}T{2}00".format(month,date,hour)
        weather = self.__db.read_single('weather', {'timestamp':curr_time})

        return {
            'temperature': weather['Temperature ']['2 m elevation corrected'],
            'precipitation': weather['Precipitation Total'],
            'wind_gust': weather['Wind Gust'],
            'wind_speed': weather['Wind Speed ']['10 m'],
            'wind_direction': weather['Wind Direction ']['10 m'],     
            'cloud_cover': {
                'total': weather['Cloud Cover Total'],
                'high': weather['Cloud Cover High ']['high cld lay'],
                'mid': weather['Cloud Cover Medium ']['mid cld lay'],
                'low': weather['Cloud Cover Low ']['low cld lay']
            },
            'cloud_columns':{
                'ice': weather['Total Column Cloud Ice ']['atmos col'],
                'water': weather['Total Column Cloud Water ']['atmos col']
            },
            'cape': weather['CAPE ']['180-0 mb above gnd'],
            'radition': {
                'shortwave': weather['Shortwave Radiation'],
                'longwave': weather['Longwave Radiation'],
                'uv': weather['UV Radiation'],
            },
            'mean_sea_level_presssure': weather['Mean Sea Level Pressure ']['MSL']
        }, 200