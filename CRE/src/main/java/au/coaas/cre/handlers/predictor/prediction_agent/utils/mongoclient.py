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
    
  # Retrieve all the items (upro 10,000 records) in the collection
  def read_all(self, collection, condition, projection = None, sorting_col = '_id', asd = 1, limit = 10000):
    col = self.__db[collection]
    if projection != None:
      return list(col.find(condition, projection).sort(sorting_col,asd).limit(limit))
    else:
      return list(col.find(condition).sort(sorting_col,asd).limit(limit))
    
  # Read only the distinct values of a property from a collection
  def read_distinct(self, collection, field):
    col = self.__db[collection]
    return list(col.distinct(field))
  
  def read_distinct(self, collection, conditions):
    col = self.__db[collection]
    condition_obj = {}
    for cond in conditions:
      condition_obj[cond] = '$'+cond
    
    return list(col.aggregate([
      {'$group': {'_id' : condition_obj}}
    ]))