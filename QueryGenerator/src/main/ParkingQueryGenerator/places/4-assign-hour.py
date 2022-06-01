import json
import pymongo
import random

"""Assigns execution time, expected time at place, rating, price, distnace from parking paramters to query template"""

days = ['Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday']
f = open('place_busy.json')
data = json.load(f)

all_places = data['places'].keys()

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["acoca-experiments"]
mycol = mydb["query-params"]

mycol_2 = mydb["places"]

count = 0

week = {
    'Monday':0,
    'Tuesday':1,
    'Wednesday':2,
    'Thursday':3,
    'Friday':4,
    'Saturday':5,
    'Sunday':6
}

def getindex(day):
    return week[day]

def converttoprice(hrs):
    """These are Melbourning parking charges as published in
    https://www.melbourne.vic.gov.au/parking-and-transport/parking/parking-locations-fees/Pages/parking-locations-and-fees.aspx"""

    if hrs < 1:
        return 7
    elif hrs < 2:
        return 14
    elif hrs < 3:
        return 21
    elif hrs < 4:
        return 28
    else:
        return 35

distances = [50,75,100,150,200,250,500,750,1000,1500,2000]

def decidedistance(prob, hour, crowd):
    if(prob<0.25):
        """Own Static Preference"""
        return distances[round(random.uniform(0,10))]
    elif(prob>=0.25 and prob<0.5):
        """Time of the day"""
        if(hour>=5 and hour<10) or (hour>=14 and hour<19):
            if(prob<0.45):
                return distances[random.randint(0,5)]
            else:
                return distances[random.randint(6,10)]
        elif(hour>=10 and hour<14):
            if(prob<0.375):
                return distances[random.randint(0,7)]
            else:
                return distances[random.randint(8,10)]
        elif(hour>=19 and hour<=23) or hour == 0:
            if(prob<0.35):
                return distances[random.randint(0,7)]
            else:
                return distances[random.randint(8,10)]
        else:
            if(prob<0.45):
                return distances[random.randint(0,4)]
            else:
                return distances[random.randint(5,6)]  

    elif(prob>=0.5 and prob<0.75):
        """Business of the Location"""
        if(crowd<25):
            if(prob<0.7):
                return distances[random.randint(0,5)]
            else:
                return distances[random.randint(6,10)]
        elif(crowd>=25 and crowd<50):
            if(prob<0.675):
                return distances[random.randint(0,7)]
            else:
                return distances[random.randint(8,10)]
        elif(crowd>=50 and crowd<75):
            if(prob<0.675):
                return distances[random.randint(0,5)]
            else:
                return distances[random.randint(6,10)]
        else:
            if(prob<0.55):
                return distances[random.randint(0,5)]
            else:
                return distances[random.randint(6,10)]
    else:
        """Random Choice"""
        return distances[random.randint(0,10)]

for loc in all_places:
    print('Filling for ' + loc)

    place_data = mycol_2.find_one({'name':loc})
    if 'populartimes' not in place_data:
        continue
    
    pop_times = place_data['populartimes']

    for day in days:
        condition = {
            'address': loc,
            'day': day
        }
        loc_tags = mycol.find(condition)
        index = -1
        hourly_variation = pop_times[getindex(day)]['data']
        
        hr = 0
        for hour in range(24):
            curr = hourly_variation[hour]
            if(curr > 0):
                for i in range(curr*10):
                    index += 1
                    myquery = { "_id": loc_tags[index]['_id'] }
                    n_vals = { 
                        "hour": hour, 
                        "minute": random.randrange(0, 60),
                        "second": random.randrange(0, 60)
                    }

                    expected_time_to_spend = 0

                    probability = random.uniform(0, 1)

                    if probability > 0.2:
                        if 'time_spent' in loc_tags[index]:
                            time_spent = loc_tags[index]['time_spent']
                            min_time = round(time_spent[0]/60,2)
                            max_time = round(time_spent[1]/60,2)

                            expected_time_to_spend = random.randrange(min_time, max_time)

                            n_vals['price'] = converttoprice(expected_time_to_spend)
                        else:
                            """Minimum and Maximum parking charge in Melbourne"""
                            n_vals['price'] = random.randrange(3,79)
                    
                    """Probability of guessing the time"""
                    if probability > 0.8:
                        if expected_time_to_spend > 0:
                            n_vals['expected_time'] = expected_time_to_spend
                        else:
                            if 'time_spent' in loc_tags[index]:
                                time_spent = loc_tags[index]['time_spent']
                                min_time = round(time_spent[0]/60,2)
                                max_time = round(time_spent[1]/60,2)

                                expected_time_to_spend = random.randrange(min_time, max_time)
                            else:
                                """Expecting to park for between 10mins to 4 hours"""
                                expected_time_to_spend = round(random.uniform(0.167, 4),2)

                            n_vals['expected_time'] = expected_time_to_spend

                    if probability > 0.5:
                        n_vals['rating'] = random.randint(0,4) + 1
                    
                    n_vals['distance'] = decidedistance(probability, hour, curr)

                    newvalues = { "$set": n_vals}

                    mycol.update_one(myquery, newvalues)
                    count = count + 1

                    if(count % 10000 == 0):
                        print('Updated '+ str(count))

print('Completed assigning query execution time, expected time at place, rating, price, distnace from parking paramters to query template!')