import json

f = open('test-queries.json')
data = json.load(f)

with open('output', 'a') as file:
    file.write("token\tquery\n")
    for query in data:
        file.write(query['token'] + "\t" + query['query'] + "\n")
        
f.close()