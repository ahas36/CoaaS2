FROM openjdk:8

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8003,server=y,suspend=n

COPY target/lib /app/lib
COPY target/SQEM-2.0.1.jar /app/SQEM-2.0.1.jar

EXPOSE 8686
EXPOSE 8003
ENTRYPOINT ["java", "-jar", "/app/SQEM-2.0.1.jar"]