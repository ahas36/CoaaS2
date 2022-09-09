import pymongo

class MongoClient:
  __db = None

  def __init__(self, connection: str, database: str):
    myclient = pymongo.MongoClient(connection)
    self.__db = myclient[database]
    
  # Insert a single new item to the collection
  # Returns: _id of the inserted item
  def insert_one(self, collection, data):
    col = self.__db[collection]
    try:
      x = col.insert_one(data)
      return x.inserted_id
    except Exception as e:
      print('Insertion into mongo db failed!')
      return -1

  # Insert multiple items to the collection
  # Returns: list of _ids of the inserted
  def insert_many(self, collection, data):
    col = self.__db[collection]
    try:
      x = col.insert_many(data)
      return x.inserted_ids
    except(Exception):
      print('Insertion into mongo db failed!')
      return -1