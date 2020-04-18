FROM openjdk:8

COPY target/lib /app/lib
COPY target/SQEM-2.0.1.jar /app/SQEM-2.0.1.jar

EXPOSE 8686
ENTRYPOINT ["java", "-jar", "/app/SQEM-2.0.1.jar"]