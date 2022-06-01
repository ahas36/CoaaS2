import json

"""Takes all the places and calculate the following distributions:
1) Total Number of crowd (or context queries)
1) Total crowd (or context queries) by day
2) Total crowd (or context queries) by day per location"""

with open('/datasets/places.json', 'r') as f:
    data = json.load(f)

with open('config.json', 'r') as f2:
    config = json.load(f2)

total = 0
place_sums = dict()
days_sum = dict(
    Monday = 0,
    Tuesday = 0,
    Wednesday = 0,
    Thursday = 0,
    Friday = 0,
    Saturday = 0,
    Sunday = 0
)

for file in data:
    if 'populartimes' in file:
        subsum = dict()
        for day in file['populartimes']:
            daysum = sum(day['data']) * config['multiplier'] * config['probability']
            total+=daysum
            days_sum[day['name']] += daysum
            subsum[day['name']] = daysum

        place_sums[file['name']] = subsum

final = {
    "total": total,
    "days": days_sum,
    "places": place_sums
}

with open("/datasets/place_busy.json", "w") as out_file:
    json.dump(final, out_file)