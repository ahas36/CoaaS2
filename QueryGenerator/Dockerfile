FROM openjdk:8

COPY target/lib /app/lib
COPY target/CQG-1.0.0.jar /app/CQG-1.0.0.jar

ENTRYPOINT ["java", "-jar", "/app/CQG-1.0.0.jar"]