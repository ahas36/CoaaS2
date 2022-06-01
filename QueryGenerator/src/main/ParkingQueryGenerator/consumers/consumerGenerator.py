import string
import uuid
import pandas as pd
import pymongo
import random

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]
mycol = mydb["vehicles"]

df = pd.read_csv('/datasets/automobile.csv', usecols= ['make','num-of-doors','body-style','drive-wheels','wheel-base','length','width','height','city-mpg','highway-mpg'])
df = df.reset_index() 

count = 0
TOTAL = 543050 # This is precalculated based on consumer behaviour

def vincreate():
    N = 8
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
    elif(cnt<134470):
        return {
                "name":'13',
                "days":['Monday'],
                "time": 'random',
                "location": 'random'
            }
    elif(cnt<157190):
        return {
                "name":'14',
                "days":['Tuesday'],
                "time": 'random',
                "location": 'random'
            }
    elif(cnt<199800):
        return {
                "name":'15',
                "days":['Wednesday'],
                "time": 'random',
                "location": 'random'
            }
    elif(cnt<249390):
        return {
                "name":'16',
                "days":['Thursday'],
                "time": 'random',
                "location": 'random'
            }
    elif(cnt<303920):
        return {
                "name":'17',
                "days":['Friday'],
                "time": 'random',
                "location": 'random'
            }
    elif(cnt<428770):
        return {
                "name":'18',
                "days":['Saturday'],
                "time": 'random',
                "location": 'random'
            }
    else:
        return {
                "name":'19',
                "days":['Sunday'],
                "time": 'random',
                "location": 'random'
            }

end = False

for i in range(2650):
    for index, row in df.iterrows():
        if(count < TOTAL):
            mydict = { 
                "make": row['make'],
                "doors": row['num-of-doors'],
                "body-style": row['body-style'],
                "drive-wheels": row['drive-wheels'],
                "wheel-base": row['wheel-base'],
                "dimensions": {
                    "length": converttom(row['length'], 0, 1),
                    "width": converttom(row['width'], 0, 0.5),
                    "height": converttom(row['height'], -0.2, 1.2),
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



