from math import trunc
from distribution import RandomDistribution

class CarPark(object):    
    def getDist(self, configuration):
        rd = RandomDistribution()
        jx = rd.create_dist(configuration)
        return jx

    