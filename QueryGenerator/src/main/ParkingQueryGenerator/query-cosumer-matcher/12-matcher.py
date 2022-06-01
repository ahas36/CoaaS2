import pymongo
import random

"""This matches for weekend only commuters who arrive at different locations at different times"""

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]

vehcile_col = mydb["vehicles"]
query_col = mydb["query-params"]

category = "12"

condition = {
    "category.name": category,
    "assigned" : { "$exists" : False } 
}

days = ['Saturday','Sunday']

cars = vehcile_col.find(condition)

print('Number of cars to assign: '+ str(cars.count()))
count = 0

for car in cars:
    """One of the cars here"""
    assigned=False
    for day in days:
        monday_query = query_col.aggregate([
            { "$match": { 
                "vin": { "$exists": False },
                "day": day,
            } },
            { "$sample": { "size": 1 } }
        ])

        doc_list = list(monday_query)

        if len(doc_list)>0:
            mon = {"_id": doc_list[0]["_id"]}
            assignedCar = { "$set": { "vin": car['vin'] } }
            query_col.update_one(mon, assignedCar)
            assigned = True

    if assigned == True:
        origCar = {"_id": car["_id"]}
        newvalues = { "$set": { "assigned": True } }
        vehcile_col.update_one(origCar, newvalues)

    count = count + 1
    if(count % 1000 == 0):
        print(str(count) + 'cars done!')



