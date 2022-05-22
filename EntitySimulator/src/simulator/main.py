import sys, os

from resources.placeContext import PlaceContext
from resources.weatherContext import WeatherContext
from resources.vehicleContext import VehicleContext
from resources.carparksContext import CarParkContext
from resources.carParkContext import SimCarParkContext

sys.path.append(os.path.abspath(os.path.join('.')))

from flask import Flask
from flask_restful import Api

app = Flask(__name__)
api = Api(app)

api.add_resource(PlaceContext, '/places')
api.add_resource(WeatherContext, '/weather')
api.add_resource(VehicleContext, '/vehicles')
api.add_resource(SimCarParkContext, '/sim-carpark')
api.add_resource(CarParkContext, '/carparks')

if __name__ == '__main__':
    app.run(debug=False, port=5000)