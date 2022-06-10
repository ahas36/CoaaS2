FROM openjdk:8

COPY target/lib /app/lib
COPY target/CQG-2.0.1.jar /app/CQG-2.0.1.jar

ENTRYPOINT ["java", "-jar", "/app/CQG-2.0.1.jar"]


