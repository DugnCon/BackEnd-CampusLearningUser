FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/campuslearning-*.war /app/app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.war"]
