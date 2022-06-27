import json
import pymongo

# Day and Location Matching to the context query

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]
mycol = mydb["query-params"]

all = mycol.find()
index = -1

with open('place_busy.json') as f:
    data = json.load(f)

for day in data['days'].keys():
    for p in data['places'].keys():
        count = data['places'][p][day]
        if count > 0:
            for c in range(count):
                index += 1
                myquery = { "_id": all[index]['_id'] }
                newvalues = { "$set": { 
                    "address": p, 
                    "day": day
                } }

                mycol.update_one(myquery, newvalues)



