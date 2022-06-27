import json
import random
import pymongo

# Matches a context consumer with a place and target location

with open('/datasets/categories.json') as f:
    categories = json.load(f)

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]

vehicle_col = mydb["vehicles"]
query_col = mydb["query-params"]

""" The following values are pre-calculated. They are the outputs from executing 2-summary.py"""

main_categories = {
    "daily": [{'name':'1', 'limit':12000}, {'name':'2', 'limit':4000}, {'name':'3', 'limit':2000}, {'name':'4', 'limit':2000}],
    "weekday": [{'name':'5', 'limit':30000}, {'name':'6', 'limit':10000}, {'name':'7', 'limit':5000}, {'name':'8', 'limit':5000}],
    "weekend": [{'name':'9', 'limit':14000}, {'name':'10', 'limit':14000}, {'name':'11', 'limit':1750}, {'name':'12', 'limit':5250}],
}

""" Random not considered in this matching """
""" "random": ['13','14','15','16','17','18','19'] """

for m_cat in main_categories.keys():
    
    file_name = 'summary-'+m_cat+'.json'
    with open(file_name) as f2:
        data2 = json.load(f2)

    selected_places = data2.keys()

    for cat in main_categories[m_cat]:

        condition = {
            "category.name": cat['name']
        }

        vehicle_data = vehicle_col.find(condition)
        limit = cat['limit']

        index = -1
        total = 0
        for pl in selected_places:
            prop = data2[pl]['prop']
            count = round(limit * prop)
            total = total + count
            print(pl + ":" + str(count))
            
            for i in range(count):
                index = index + 1
                assignment = vehicle_data[index]
                myquery = {
                    "_id": assignment['_id']
                }
                newvalues = { "$set": {
                    "location": pl
                }}

                vehicle_col.update_one(myquery, newvalues)
            
        if(total != limit):
            for i in range(limit-total):
                place = selected_places[random.randrange(0,len(selected_places))]
                index = index + 1
                assignment = vehicle_data[index]
                myquery = {
                    "_id": assignment['_id']
                }
                newvalues = { "$set": {
                    "location": place
                }}

                vehicle_col.update_one(myquery, newvalues)
