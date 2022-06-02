#!/bin/bash

# Step 1: Collect data of intereted places
# Places ensure the places-list.txt is filled 
python3 ../PlacesScraper/run.py

# Step 1: Location popularity distribution creation
python3 ./places/1-places.py
python3 ./places/2-summary.py

# Step 2: Query template creation and origin assignment
#query location assignment
python3 ./query-temp/locations.py

# Step 3: Context consumer generation
# Vehicle creation
python3 ./consumers/consumerGenerator.py

# Step 4: Location matching
python3 ./places/3-assign-place.py
python3 ./places/4-assign-hour.py
python3 ./places/5-matching.py

# Step 5 (Final): Matching the Context Consumers with the Query Templates
# Consumer-query matching for each category of commuters
python3 ./query-consumer-matcher/1-matcher.py
python3 ./query-consumer-matcher/5-matcher.py
python3 ./query-consumer-matcher/9-matcher.py
python3 ./query-consumer-matcher/2-matcher.py
python3 ./query-consumer-matcher/6-matcher.py
python3 ./query-consumer-matcher/10-matcher.py
python3 ./query-consumer-matcher/3-matcher.py
python3 ./query-consumer-matcher/7-matcher.py
python3 ./query-consumer-matcher/11-matcher.py
python3 ./query-consumer-matcher/4-matcher.py
python3 ./query-consumer-matcher/8-matcher.py
python3 ./query-consumer-matcher/12-matcher.py
python3 ./query-consumer-matcher/13-matcher.py
