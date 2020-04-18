FROM openjdk:8

COPY target/lib /app/lib
COPY target/SemanticVocabManager-2.0.1.jar /app/SVM-2.0.1.jar

EXPOSE 8686
ENTRYPOINT ["java", "-jar", "/app/SVM-2.0.1.jar"]