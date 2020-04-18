FROM openjdk:8

COPY target/lib /app/lib
COPY target/CQP-2.0.1.jar /app/CQP-2.0.1.jar

EXPOSE 8585
ENTRYPOINT ["java", "-jar", "/app/CQP-2.0.1.jar"]