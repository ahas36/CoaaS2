
import random 
import math
import pymongo
import json

"""Assigns random location from which the context queries would originate
with the configured radius from a specific a location"""

with open('config.json', 'r') as f:
    config = json.load(f)

R = 0.011197501823889 * config['radius']
popularity = 89805 #Pre-calculated from the output of Step 1

locations = popularity * config['multiplier'] * config['probability']

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]
mycol = mydb["query-params"]

for x in range(locations):
    r = R * math.sqrt(random.random())
    theta = random.random() * 2 * math.pi

    centerX = config['center']['lng']
    centerY = config['center']['lat']

    x = centerX + r * math.cos(theta)
    y = centerY + r * math.sin(theta)

    mydict = { 
        "location": {
            "lat": y,
            "lng": x
        }
    }
    x = mycol.insert_one(mydict)

print('Origins and initial template of Context Queries added!')