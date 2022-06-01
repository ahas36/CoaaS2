import pymongo

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]
mycol = mydb["vehicles"]

print('data gathered')

mycol.delete_many({"category.name":"9"})

print('Done')