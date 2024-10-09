# Dockerfile
FROM gradle:8.10.1-jdk17 AS build

WORKDIR /app

ARG FILE_DIRECTORY

COPY $FILE_DIRECTORY /app

RUN gradle clean bootJar

FROM openjdk:17-jdk-slim

COPY --from=build /app/build/libs/*SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "app.jar"]