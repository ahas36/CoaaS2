import string
import uuid
import pandas as pd
import pymongo
import random
import json

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]
mycol = mydb["vehicles"]

with open('config.json', 'r') as f:
    config = json.load(f)

df = pd.read_csv('/datasets/automobile.csv', usecols= ['make','num-of-doors','body-style','drive-wheels','wheel-base','length','width','height','city-mpg','highway-mpg'])
df = df.reset_index() 

count = 0
TOTAL = config['total_vehicles'] # This is precalculated based on consumer behaviour

def vincreate():
    N = config['plate_no_length']
    n = 3
    rand_string = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(N))
    rand_string = rand_string[0:n] + '-' + rand_string[n+1:]
    return rand_string

def converttom(val, min, max):
    actual = round(val*0.0254,2)
    correction = round(random.uniform(min,max), 2)
    chance = random.uniform(0,100)
    if chance > 50:
        return actual + correction
    return actual

def converttolperkm(val):
    return val*2.35215

def tometers(val):
    return val*0.001

def attachcategory(cnt):
    if(cnt<12000):
        return {
                "name":'1',
                "days":['Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'],
                "time": 'peak',
                "location": 'static'
            }
    elif(cnt<16000):
        return {
                "name":'2',
                "days":['Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'],
                "time": 'random',
                "location": 'static'
            }
    elif(cnt<18000):
        return {
                "name":'3',
                "days":['Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'],
                "time": 'static',
                "location": 'random'
            }
    elif(cnt<20000):
        return {
                "name":'4',
                "days":['Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'],
                "time": 'random',
                "location": 'random'
            }
    elif(cnt<50000):
        return {
                "name":'5',
                "days":['Monday','Tuesday','Wednesday','Thursday','Friday'],
                "time": 'peak',
                "location": 'static'
            }
    elif(cnt<60000):
        return {
                "name":'6',
                "days":['Monday','Tuesday','Wednesday','Thursday','Friday'],
                "time": 'random',
                "location": 'static'
            }
    elif(cnt<65000):
        return {
                "name":'7',
                "days":['Monday','Tuesday','Wednesday','Thursday','Friday'],
                "time": 'static',
                "location": 'random'
            }
    elif(cnt<70000):
        return {
                "name":'8',
                "days":['Monday','Tuesday','Wednesday','Thursday','Friday'],
                "time": 'random',
                "location": 'random'
            }
    elif(cnt<84000):
        return {
                "name":'9',
                "days":['Saturday','Sunday'],
                "time": 'static',
                "location": 'static'
            }
    elif(cnt<98000):
        return {
                "name":'10',
                "days":['Saturday','Sunday'],
                "time": 'random',
                "location": 'static'
            }
    elif(cnt<99750):
        return {
                "name":'11',
                "days":['Saturday','Sunday'],
                "time": 'static',
                "location": 'random'
            }
    elif(cnt<105000):
        return {
                "name":'12',
                "days":['Saturday','Sunday'],
                "time": 'random',
                "location": 'random'
            }
    else:
        return {
                "name":'13',
                "time": 'random',
                "location": 'random'
            }

end = False

iterations = round(TOTAL/len(df.index))

for i in range(iterations):
    for index, row in df.iterrows():
        if(count < TOTAL):
            mydict = { 
                "make": row['make'],
                "doors": row['num-of-doors'],
                "body-style": row['body-style'],
                "drive-wheels": row['drive-wheels'],
                "wheel-base": row['wheel-base'],
                "dimensions": {
                    "length": converttom(row['length'], 0, config['modification_limits']['max_length_change']),
                    "width": converttom(row['width'], 0, config['modification_limits']['max_width_change']),
                    "height": converttom(row['height'], -0.2, config['modification_limits']['max_height_change']),
                    "unit": "m"
                },
                "milage": {
                    "city": converttolperkm(row['city-mpg']),
                    "highway": converttolperkm(row['highway-mpg']),
                    "unit": "liters per kilometer"
                },
                "price": random.randint(10000, 50000),
                "plate-no": vincreate(),
                "vin": str(uuid.uuid4().hex).upper(),
                "category": attachcategory(count)
            }

            mycol.insert_one(mydict)
            count = count + 1
        else:
            end = True
            break
    if end == True:
        break

print('All the vehciles created!')



