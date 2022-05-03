#stage 1
FROM node:latest as node
RUN mkdir -p /app
WORKDIR /app
COPY . .

FROM nginx:alpine
COPY --from=node /app/dist/CoaaSDashboard /usr/share/nginx/html

EXPOSE 4200


