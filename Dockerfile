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