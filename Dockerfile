FROM maven:3.9.4-eclipse-temurin-21 AS build

COPY pom.xml .
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests


FROM eclipse-temurin:21-jre

RUN adduser --system auth-user && addgroup --system auth-group && adduser auth-user auth-group
USER auth-user

WORKDIR /app

COPY --from=build /target/AuthenticationService-0.0.1-SNAPSHOT.jar ./application.jar

ENTRYPOINT ["java", "-jar", "/app/application.jar"]