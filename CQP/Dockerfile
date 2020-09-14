FROM openjdk:8

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8007,server=y,suspend=n

COPY target/lib /app/lib
COPY target/CQP-2.0.1.jar /app/CQP-2.0.1.jar

EXPOSE 8585
EXPOSE 8007
ENTRYPOINT ["java", "-jar", "/app/CQP-2.0.1.jar"]