<<<<<<< HEAD
FROM ubuntu:latest
LABEL authors="Admin"
# Sử dụng JDK để build app
FROM maven:3.9.6-eclipse-temurin-17 AS dev
WORKDIR /app

# Copy pom.xml trước để cache dependencies
COPY pom.xml .

# Tải offline dependencies
RUN mvn dependency:go-offline

# Copy toàn bộ source code
COPY src ./src

# Mở port cho Spring Boot
EXPOSE 8080

# Chạy ứng dụng với DevTools
CMD ["mvn", "spring-boot:run"]
=======
# =========================
# Stage 1: Build ứng dụng
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml
COPY pom.xml .
COPY src ./src

# Build WAR file
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Runtime (chạy app)
# =========================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy file WAR đã build (đúng tên file)
COPY --from=build /app/target/campuslearning-*.war /app/app.war

EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app/app.war"]
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
