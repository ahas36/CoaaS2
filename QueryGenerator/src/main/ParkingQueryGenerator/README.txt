This query generator is a developed speciifically for the the Car Park recommendation use case in Melbourne.

Please change the values in config.json for changes in teh result in each directory where nessecary.

Some of the python files contains values that are hard coded into the python script for simplicity. However, they are values originating from previous steps in the process.
For example, the values in 5-matching.py assigned to the variable "main_categories" are resultants from 2-summary.py
Therefore, you are either free to refactor the code to read from summary-<category>.json, e.g., "summary-daily.json" file OR change the values accordingly.

STEPS TO GENERATE QUERIES
-------------------------
1) Execute queryGenerator.bash file in the terminal.

NOTE
----
The query generator may take a number of hours to complete depending on the total number of query templates created in Step 1.
For instance, it took 36+ hours complete 898,050 queries.
