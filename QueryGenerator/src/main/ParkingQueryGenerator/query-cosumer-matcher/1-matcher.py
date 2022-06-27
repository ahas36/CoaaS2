from concurrent.futures import thread
from queue import Empty
import pymongo
import random

#This matches for daily commuters who arrive at the same location at relativey the same time

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]

vehcile_col = mydb["vehicles"]
query_col = mydb["query-params"]

category = "1"

condition = {
    "category.name": category,
    "assigned" : { "$exists" : False } 
}

days = ['Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday']

cars = vehcile_col.find(condition)

print('Number of cars to assign: '+ str(cars.count()))
count = 0

for car in cars:
    # One of the cars here
    # Retrieve a random context query executable on Monday
    # The condition selects a query exectable duing the peak traffic hours.
    monday_query = list(query_col.aggregate([
        { "$match": { 
            "vin": { "$exists": False },
            "day": "Monday",
            "address": car['location'],
            "hour": {"$lt": 16},
            "hour": {"$gte": 10}
        } },
        { "$sample": { "size": 1 } }
    ]))[0]

    hour = monday_query['hour']
    minute = monday_query['minute']

    mon = {"_id": monday_query["_id"]}
    assignedCar = { "$set": { "vin": car['vin'] } }
    query_col.update_one(mon, assignedCar)

    # For each of the other days in the week, given the vehcile is assigned to query execting on Monday
    # the queries selected for each other day is picked based on similarity to the assigned query on Monday.
    for day in days:
        new_hour = hour
        new_minute = minute + random.randrange(-15,15)
        
        if new_minute >= 60:
            new_hour = new_hour + 1
            if new_hour >=24:
                new_hour = new_hour - 24
            new_minute = new_minute - 60
        elif new_minute < 0:
            new_hour = new_hour - 1
            if new_hour < 0:
                new_hour = 24 + new_hour
            new_minute = 60 + new_minute 

        day_query = query_col.aggregate([
            { "$match": { 
                "vin": { "$exists": False },
                "day": day,
                "address": car['location'],
                "hour": new_hour,
                "minute": {"$lte": new_minute}
            } },
            { "$sample": { "size": 1 } }
        ])

        doc_list = list(day_query)

        if len(doc_list) > 0:
            day_query = doc_list[0]
        else:
            day_query = query_col.aggregate([
                { "$match": { 
                    "vin": { "$exists": False },
                    "day": day,
                    "address": car['location'],
                    "$or":[ {"hour":hour}, {"hour":new_hour}]
                } },
                { "$sample": { "size": 1 } }
            ])
            try_one = list(day_query)
            if len(try_one) > 0:
                day_query = try_one[0]
            else:
                day_query = query_col.aggregate([
                    { "$match": { 
                        "vin": { "$exists": False },
                        "day": day,
                        "address": car['location']
                    } },
                    { "$sample": { "size": 1 } }
                ])

                try_two = list(day_query)
                if len(try_two)>0:
                    day_query = try_two[0]
                else:
                    continue

        mon = {"_id": day_query["_id"]}
        assignedCar = { "$set": { "vin": car['vin'] } }
        query_col.update_one(mon, assignedCar)

    origCar = {"_id": car["_id"]}
    newvalues = { "$set": { "assigned": True } }
    vehcile_col.update_one(origCar, newvalues)

    count = count + 1
    if(count % 1000 == 0):
        print(str(count) + 'cars done!')



