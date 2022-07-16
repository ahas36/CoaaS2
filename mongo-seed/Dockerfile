FROM mongo

COPY consumerToken.json /consumerToken.json
COPY contextConsumer.json /contextConsumer.json
COPY contextService.json /contextService.json
COPY query-params.json /query-params.json
COPY vehicles.json /vehicles.json
COPY places.json /places.json
COPY carparks.json /carparks.json
COPY weather.json /weather.json
COPY empty.json /empty.json

CMD   mongoimport --host mongodb --db coaas --collection consumerToken --type json --file /consumerToken.json --jsonArray --legacy && \
      mongoimport --host mongodb --db coaas --collection contextConsumer --type json --file /contextConsumer.json --jsonArray --legacy && \
      mongoimport --host mongodb --db coaas --collection contextService --type json --file /contextService.json --jsonArray --legacy && \
      mongoimport --host mongodb --db coaas_log --collection performanceSummary --type json --file /empty.json --jsonArray --legacy && \
      mongoimport --host mongodb --db acoca-experiments --collection weather --type json --file /weather.json --jsonArray --legacy && \
      mongoimport --host mongodb --db acoca-experiments --collection places --type json --file /places.json --jsonArray --legacy && \
      mongoimport --host mongodb --db acoca-experiments --collection carparks --type json --file /carparks.json --jsonArray --legacy && \
      mongoimport --host mongodb --db acoca-experiments --collection vehicles --type json --file /vehicles.json --jsonArray --legacy && \
      mongoimport --host mongodb --db acoca-experiments --collection query-params --type json --file /query-params.json --jsonArray --legacy
