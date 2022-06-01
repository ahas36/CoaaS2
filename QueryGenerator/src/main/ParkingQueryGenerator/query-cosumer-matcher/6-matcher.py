import pymongo
import random

"""This matches for weekday only commuters who arrive at the same location but at different times of the day"""

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]

vehcile_col = mydb["vehicles"]
query_col = mydb["query-params"]

category = "6"

condition = {
    "category.name": category,
    "assigned" : { "$exists" : False } 
}

days = ['Monday','Tuesday','Wednesday','Thursday','Friday']

cars = vehcile_col.find(condition)

print('Number of cars to assign: '+ str(cars.count()))
count = 0

loc_changed = False

for car in cars:
    """One of the cars here"""
    assigned = False
    for day in days:
        monday_query = query_col.aggregate([
            { "$match": { 
                "vin": { "$exists": False },
                "day": day,
                "address": car['location']
            } },
            { "$sample": { "size": 1 } }
        ])

        doc_list = list(monday_query)

        if len(doc_list)>0:
            mon = {"_id": doc_list[0]["_id"]}
            assignedCar = { "$set": { "vin": car['vin'] } }
            query_col.update_one(mon, assignedCar)
            assigned = True
        else:
            monday_query = query_col.aggregate([
                { "$match": { 
                    "vin": { "$exists": False },
                    "day": day
                } },
                { "$sample": { "size": 1 } }
            ])

            doc_list = list(monday_query)
            car['location'] = doc_list[0]['address']
            loc_changed = True

            mon = {"_id": doc_list[0]["_id"]}
            assignedCar = { "$set": { "vin": car['vin'] } }
            query_col.update_one(mon, assignedCar)
            assigned = True


    if assigned == True:
        origCar = {"_id": car["_id"]}
        if loc_changed == False:
            newvalues = { "$set": { "assigned": True } }
            vehcile_col.update_one(origCar, newvalues)
        else:
            newvalues = { "$set": { "assigned": True, "location": car['location'] } }
            vehcile_col.update_one(origCar, newvalues)

    count = count + 1
    if(count % 1000 == 0):
        print(str(count) + 'cars done!')



