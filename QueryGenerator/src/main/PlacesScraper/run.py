import populartimes
import json
import requests
import pymongo

with open('config.json', 'r') as f:
    config = json.load(f)

filename = 'places-list.txt'
with open(filename) as file:
    places = file.readlines()
    places = [place.rstrip().replace(' ','%20') for place in places]

place_collection = []

for pl in places:
    response = requests.get(config['url'].format(pl,config['api_key']))
    if response.status_code == 200:
        response.json()

    first_item = response['results'][0]
    place = populartimes.get_id(config['api_key'], first_item['place_id'])
    place_collection.append(place)

with open('../ParkingQueryGenerator/places/datasets/places.json', 'w') as f2:
    json.dump(place_collection, f2)

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]
mycol = mydb["places"]

x = mycol.insert_many(place_collection)

print('Completed collecting all the places!')

