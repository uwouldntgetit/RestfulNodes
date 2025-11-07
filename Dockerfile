FROM eclipse-temurin:21-jdk-jammy
COPY build/libs/*.jar app.jar
LABEL authors="tiberius"
ENTRYPOINT ["java", "-jar", "/app.jar"]