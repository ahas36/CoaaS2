import pymongo
import random

"""This matches for all the other random commuters"""

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]

vehcile_col = mydb["vehicles"]
query_col = mydb["query-params"]

query_condition = {
    "vin": { "$exists": False }
}
query_filter = { "_id": 1}

queries = query_col.find(query_condition, query_filter)

print('Number of queries to assign: '+ str(queries.count()))
count = 0

for q in queries:
    res = list(vehcile_col.aggregate([
            { "$match": { 
                "assigned": { "$exists": False },
            } },
            { "$sample": { "size": 1 } }
        ]))[0]

    mon = {"_id": q["_id"]}
    assignedCar = { "$set": { "vin": res['vin'] } }        
    query_col.update_one(mon, assignedCar)

    c = {"_id": res["_id"]}
    vehcile_col.update_one(c, { "$set": { "assigned": True } }  )

    count = count + 1
    if(count % 1000 == 0):
        print(str(count) + 'queries done!')