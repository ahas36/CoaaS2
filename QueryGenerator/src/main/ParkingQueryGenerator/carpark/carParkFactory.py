from carpark.carPark import CarPark
from configuration import CarParkConfiguration

# Produces a car park instance according to configuration
class CarParkFactory:
    # Retruns the singleton instance of the car park
    def get_carpark(self, configuration) -> CarPark:
        if(configuration != None and isinstance(configuration,CarParkConfiguration)):
            return CarPark(configuration)
        else:
            return CarPark()







        