FROM openjdk:8

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8004,server=y,suspend=n
COPY target/lib /app/lib
COPY src/main/resources/schmeaorg.ttl  /app/lib/ont/schmeaorg.ttl
COPY target/SemanticVocabManager-2.0.1.jar /app/SVM-2.0.1.jar

EXPOSE 8686
EXPOSE 8004
ENTRYPOINT ["java", "-jar", "/app/SVM-2.0.1.jar"]