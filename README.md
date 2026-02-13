# 🎓 Campus Learning User -- Backend System

Backend system for managing campus learning user interactions such as profile, enrollments, and dashboard data.

Built with **Java** and **Spring Boot** following a layered architecture.

------------------------------------------------------------------------

## 🚀 Tech Stack

-   ☕ Java 17+
-   🌱 Spring Boot
-   🗄 Spring Data JPA
-   🐬 MySQL
-   🐳 Docker & Docker Compose
-   🔐 Spring Security (Optional)

------------------------------------------------------------------------

## 📌 Project Overview

Campus Learning User is a RESTful API designed to serve user-side interactions of a campus learning system.

### Main Features:

- 👤 User profile management
- 📦 Enrollment management
- 📚 Course access
- 📊 Dashboard data
- 🧠 Business logic via service layer

This project focuses on user-side backend development using Spring Boot.

------------------------------------------------------------------------

## 🏗 Architecture

The project follows a layered architecture:

Controller → Service → Repository → Database

### Layer Responsibilities:

-   🎯 Controller: Handle HTTP requests
-   ⚙️ Service: Business logic processing
-   💾 Repository: Data access layer (JPA)
-   🧱 Entity: Database mapping objects

This structure improves maintainability, scalability, and code
readability.

------------------------------------------------------------------------

## 📂 Project Structure

    src/
     └── main/
         ├── java/
         │   └── com/...
         │       ├── controller/
         │       ├── service/
         │       ├── repository/
         │       ├── entity/
         │       └── config/
         └── resources/
             ├── application.yml
             └── ...

------------------------------------------------------------------------

## ⚙️ Setup & Run

### 🐳 Run with Docker

``` bash
docker-compose up --build
```

------------------------------------------------------------------------

### 💻 Run Locally

1.  Clone repository

``` bash
git clone https://github.com/DugnCon/BackEnd-CampusLearningAdmin.git
cd BackEnd-CampusLearningAdmin
```

2.  Configure database in `application.yml`

3.  Run application

``` bash
mvn spring-boot:run
```

Server runs at:

    http://localhost:8080

------------------------------------------------------------------------

## 📡 API Example

### Request

    GET /api/courses

### Response

``` json
{
  "id": 1,
  "name": "Backend Development",
  "description": "Spring Boot course"
}
```

------------------------------------------------------------------------

## 🛡 Future Improvements

-   🔐 JWT Authentication
-   🧩 Role-based Authorization
-   📄 Swagger API Documentation
-   🧪 Unit & Integration Testing
-   🚀 CI/CD Pipeline

------------------------------------------------------------------------

## 🎯 Purpose

This project was developed for:

-   Practicing backend development
-   Learning system architecture design
-   Demonstrating Java Spring Boot skills

------------------------------------------------------------------------

## 👨‍💻 Author

**DugnCon**\
GitHub: https://github.com/DugnCon
