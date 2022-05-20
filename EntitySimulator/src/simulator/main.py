import sys, os

from placeContext import PlaceContext
from carParkContext import CarParkContext
from vehicleContext import VehicleContext
from weatherContext import WeatherContext
sys.path.append(os.path.abspath(os.path.join('.')))

from flask import Flask
from flask_restful import Resource, Api

app = Flask(__name__)
api = Api(app)

api.add_resource(PlaceContext, '/places')
api.add_resource(WeatherContext, '/weather')
api.add_resource(VehicleContext, '/vehicles/<vin>')
api.add_resource(CarParkContext, '/carparks')

if __name__ == '__main__':
    app.run(debug=False, port=5000)