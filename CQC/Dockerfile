FROM openjdk:8

COPY target/lib /app/lib
COPY target/CQC-2.0.1.jar /app/CQC-2.0.1.jar

EXPOSE 8484
ENTRYPOINT ["java", "-jar", "/app/CQC-2.0.1.jar"]