
import json

"""For each main categories of commuters, this script creates the distribution of 
commuters to each location"""

with open('/datasets/place_busy.json') as f:
    data = json.load(f)

places = data['places']

main_categories = {
    "daily": ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
    "weekday": ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
    "weekend": ['Saturday', 'Sunday']
}

for main_cat in main_categories.keys():
    selected_places = []
    days = main_categories[main_cat]

    for candidate in places.keys():
        failed = False
        for day in days:
            if candidate[day] == 0:
                failed = True
                break

        if failed != True:
            selected_places.append(candidate)

    pl = dict()
    total = 0

    for p in selected_places:
        all = places[p]
        val_list = list(all.values())
        tot = sum(val_list)
        total = total + tot
        pl[p] = { "count": tot }

    for p in selected_places:
        prop = pl[p]['count']/total
        pl[p]['prop'] = prop

    file_name = '/datasets/summary-'+main_cat+'.json'
    with open(file_name, 'w') as fp:
        json.dump(pl, fp)

print('Completed deriving the query propotions by commuter categories!')