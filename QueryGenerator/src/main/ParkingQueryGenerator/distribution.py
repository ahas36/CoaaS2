import math
import random
import numpy as np
from matplotlib import mlab
from math import nan, trunc
import traceback
from scipy.stats import skewnorm
from scipy.ndimage import gaussian_filter1d

from error import ValueConfigurationError
from configuration import CarParkConfiguration

# RandomDistribution class 
# Generates a standard normal distribution that is guranteed no-skew.
# Config Value: random
class RandomDistribution(object):
      
    def create_dist(self, configuration):
        occupancy = []
        final = []

        sample_rate = configuration.sampling_rate/1000

        sample_size = configuration.sample_size
        no_of_refreshes = configuration.no_of_refreshes
        """selected_periods = configuration.selected_periods"""

        """+ random.randrange(0,round(sample_size/4))"""
        mean = sample_size
        ep = 2.71828
        standard_deviation = getStdDev(no_of_refreshes, sample_rate)

        for snap in range(-280,no_of_refreshes-280):
            
            c = pow(snap-mean,2)/standard_deviation
            d = pow(ep,-c)
            occupancy.append(d)

        max_val = max(occupancy)
        multi = 1
        if max_val > 0:
            multi = 1/max_val
        else:
            for snap in range(-280,no_of_refreshes-280):        
                c = pow(snap-round(sample_size/2),2)
                d = pow(ep,-c)
                occupancy.append(d)

            max_val = max(occupancy)
            if max_val > 0:
                multi = 1/max_val
        
        if math.isinf(multi) :
            final = list(map(lambda x: round(random.uniform(0,1)*sample_size), range(0,no_of_refreshes)))
        else:
            for x in occupancy:
                final_val = round(x*multi*sample_size) + configuration.min_occupancy
                if final_val > sample_size:
                    final_val = sample_size

                final.append(final_val)
        
        return final

def getStdDev(no_of_refreshes, sample_rate):
        if(sample_rate >= 1800):
            return no_of_refreshes*random.randrange(1,3)
        elif(sample_rate < 1800 and sample_rate >=600):
            return no_of_refreshes*random.randrange(3,8)
        else:
            return no_of_refreshes*random.randrange(50,100)
    
# Static Method
# Builds the normal distribution from the random data collection
# normalizes that with in the given parameters (fitting).
# parameters: init_dist = random data collection, sample_size = maximum value of representive slots
def normalizing_distribution(init_dist, sample_size):
    constant = sample_size/sum(init_dist)
    dist = map(lambda x: x * constant, init_dist)

    time_step = 1
    cum_occupancy = 0
    last_occupancy = 0

    time_step_list = [0]
    occupancy_list = [last_occupancy]

    for data in list(dist):
        cum_occupancy = cum_occupancy + data
        if(abs(cum_occupancy - last_occupancy) >= 1):
            last_occupancy = trunc(cum_occupancy)
            time_step_list.append(time_step)
            occupancy_list.append(last_occupancy)
        time_step = time_step + 1

    return (time_step_list, occupancy_list)

# Static Method
# Trims the distributions into selected discrete planning periods 
# parameters: selections = indexes of the selected planning periods, dist = full distribution 
def trim_distribution(selections, dist):
    occupancy = []
    for sel in selections:
        occupancy.extend(dist[sel[0]:sel[1]+1])
    time_step = range(1,len(occupancy)+1)
    return (occupancy,time_step)

# Static Method
# Smooths the curve using Gaussian 1-D filtering
def smooth(x,std):
    y = gaussian_filter1d(x, std)
    return list(map(lambda z : round(z), y))

# Static Method
# Adds random noise to the distribution
# parameters: x = distribution, up_bound = max value of the noise, coverage = percentage of noise in the distbution
def randomnoiser(x, up_bound, coverage):
    if(coverage <= 1.0 and coverage > 0.0):  
        noise = np.random.normal(0,2,len(x))   
        indexes = random.sample(range(len(x)), round(len(x)*coverage))
        for i in indexes:
            noise[i] = 0
        return list(map(lambda z : check_value(round(x[z] + noise[z]), up_bound), range(0,len(x)))) 
    else:
        return x

# Static Method
def check_value(x, up_bound):
    if(x<0):
        return 0
    elif(x>up_bound):
        return up_bound
    else:
        return x