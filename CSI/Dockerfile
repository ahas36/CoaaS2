FROM openjdk:8

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8002,server=y,suspend=n

COPY target/lib /app/lib
COPY target/CSI-2.0.1.jar /app/CSI-2.0.1.jar

EXPOSE 8582
EXPOSE 8002

ENTRYPOINT ["java", "-jar", "/app/CSI-2.0.1.jar"]
