FROM openjdk:8

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8014,server=y,suspend=n

COPY target/lib /app/lib
COPY target/CPREE-2.0.1.jar /app/CPREE-2.0.1.jar

EXPOSE 9292
EXPOSE 8014
ENTRYPOINT ["java", "-jar", "/app/CPREE-2.0.1.jar"]