from concurrent.futures import thread
from queue import Empty
import pymongo
import random

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]

vehcile_col = mydb["vehicles"]
query_col = mydb["query-params"]

category = "5"

origCar = {
    "category.name": category,
    "assigned" : { "$exists" : True } 
}
newvalues = { "$unset": { "assigned": "" } }

assigned_vehciles = vehcile_col.find(origCar)

for vehi in assigned_vehciles:
    origQ = {
        "vin" : { "$exists" : True },
        "vin" : vehi["vin"]
    }
    newQ = { "$unset": { "vin": "" } }
    query_col.update_many(origQ, newQ)

vehcile_col.update_many(origCar, newvalues)

print("Done!")