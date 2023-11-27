FROM openjdk:8

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8020,server=y,suspend=n
ENV MASTER_IP 0.0.0.0

COPY target/lib /app/lib
COPY target/RWC-1.0.0.jar /app/RWC-1.0.0.jar

EXPOSE 6868
EXPOSE 8020
ENTRYPOINT ["java", "-jar", "/app/RWC-1.0.0.jar"]