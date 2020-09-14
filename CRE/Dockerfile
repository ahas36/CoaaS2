FROM openjdk:8

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8008,server=y,suspend=n

COPY target/lib /app/lib
COPY target/CRE-2.0.1.jar /app/CRE-2.0.1.jar

EXPOSE 8583
EXPOSE 8008
ENTRYPOINT ["java", "-jar", "/app/CRE-2.0.1.jar"]