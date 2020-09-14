FROM openjdk:8

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8001,server=y,suspend=n

COPY target/lib /app/lib
COPY target/CQC-2.0.1.jar /app/CQC-2.0.1.jar

EXPOSE 8484
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "/app/CQC-2.0.1.jar"]