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
