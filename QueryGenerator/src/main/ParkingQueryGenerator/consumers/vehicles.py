import json
import urllib
import requests
import pymongo

where = urllib.parse.quote_plus(
    {
        "Year": 1998
    }
)

url = 'https://parseapi.back4app.com/classes/Car_Model_List?order=Year&where=%s' % where
headers = {
    'X-Parse-Application-Id': 'hlhoNKjOvEhqzcVAJ1lxjicJLZNVv36GdbboZj3Z', # This is the fake app's application id
    'X-Parse-Master-Key': 'SNMJJF0CZZhTPhLDIqGhTlUNV9r60M2Z5spyWfXW' # This is the fake app's readonly master key
}
data = json.loads(requests.get(url, headers=headers).content.decode('utf-8')) # Here you have the data that you need

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]
mycol = mydb["vehicles"]

for ve in data['results']:
    mydict = { 
        "location": {
            "year": ve['Year'],
            "make": ve['Make'],
            "model": ve['Model'],
            "type": ve['Category']
        }
    }
    x = mycol.insert_one(ve)
