**Product Management REST API**
Java Developer Technical Assessment Submission
    This project is a production-ready, secure, and scalable RESTful API built using Spring Boot 3 and Java 21.
    It implements full CRUD operations for Products and their associated Items, secured using JWT Authentication with Refresh Token Rotation, and follows clean architecture principles.
    The application is fully containerized using Docker & Docker Compose, documented with Swagger/OpenAPI, and covered with unit and integration tests.

**Tech Stack**
 Java 21
 Spring Boot 3
 Spring Data JPA (Hibernate)
 MySQL 8
 Spring Security with JWT & Refresh Token
 JUnit 5 & Mockito
 Swagger / OpenAPI
 Docker & Docker Compose
 H2 Database (for testing)

**Features**
 Full CRUD operations for Products
 Product → Items relationship
 JWT Authentication
 Refresh Token Rotation
 Role-based Authorization
 Pagination Support
 Global Exception Handling
 Standardized API Response Structure
 Input Validation (Jakarta Validation)
 Swagger API Documentation
 Dockerized Application
 Unit & Integration Testing


**Architecture Overview**

                ┌─────────────────────┐
                │     Controller      │
                │  (REST Endpoints)   │
                └─────────┬───────────┘
                          │
                ┌─────────▼───────────┐
                │       Service       │
                │  (Business Logic)   │
                └─────────┬───────────┘
                          │
                ┌─────────▼───────────┐
                │     Repository      │
                │   (Data Access)     │
                └─────────┬───────────┘
                          │
                ┌─────────▼───────────┐
                │       MySQL DB      │
                └─────────────────────┘


**Package Structure**
com.pms
 ├── config
 ├── controllers
 ├── dto
 ├── entities
 ├── exceptions
 ├── helper
 ├── repositories
 ├── services
 └── services.implementation


**Design Principles Used**
Separation of Concerns
DTO Pattern
Dependency Injection
Global Exception Handling
REST Resource-Oriented Design
API Versioning (/api/v1/)
Standardized Error Responses

**Base Path**
/api/v1

| Method | Endpoint             | Description                             |
| ------ | -------------------- | --------------------------------------- |
| GET    | /products            | Get all products (pagination supported) |
| GET    | /products/{id}       | Get product by ID                       |
| POST   | /products            | Create product                          |
| PUT    | /products/{id}       | Update product                          |
| DELETE | /products/{id}       | Delete product                          |
| GET    | /products/{id}/items | Get items of product                    |


**Security Implementation**
JWT Access Token
Refresh Token with rotation
Role-based Authorization
Stateless session management
Password encryption using BCrypt
CORS configuration enabled

**Database Schema**
**Product Table**
CREATE TABLE product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(255) NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_on TIMESTAMP NOT NULL,
    modified_by VARCHAR(100),
    modified_on TIMESTAMP
);

**Item Table**
CREATE TABLE item (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id)
);


**Running with Docker**

**Step 1: Build and Run**
docker compose up --build

**Application will run on:**
http://localhost:8080

**MySQL runs on:**
localhost:3308

**Swagger Documentation**
Access Swagger UI at:
http://localhost:8080/swagger-ui/index.html



**Testing**
**Unit Testing**
JUnit 5
Mockito
Service Layer Coverage

**Integration Testing**
Spring Boot Test
H2 In-Memory Database

**Run tests:**
mvn test


**Performance & Best Practices**
Pagination for large data sets
Database indexing on foreign keys
DTO mapping for optimized responses
Stateless JWT authentication
Proper exception handling
Clean code and naming conventions


**Setup Without Docker**
**Configure MySQL database**
CREATE DATABASE zest;

**Update application.properties**
spring.datasource.url=jdbc:mysql://localhost:3307/zest
spring.datasource.username=root
spring.datasource.password=your_password

**Run:**
mvn clean install
mvn spring-boot:run


**Evaluation Coverage**
This implementation fulfills:
Clean Architecture
REST API Best Practices
Security Implementation
Unit & Integration Testing
Docker Deployment
Documentation
Versioned API Design


**Postman API Testing**
**Base Path**
api/v1/auth/

**STEP 1 — Register User**
POST http://localhost:8080/api/v1/auth/register
**Input:**
{
  "username": "vaibhav",
  "password": "12345"
}
**Output:**
status 200 OK
User registered successfully


**STEP 2 — Login (Get Access + Refresh Token)**
POST http://localhost:8080/api/v1/auth/login
**Input:**
{
  "username": "admin",
  "password": "admin123"
}
**Output:**
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2YWliaGF2IiwiaWF0IjoxNzcxNTY4MDgzLCJleHAiOjE3NzE1NzE2ODN9.7h0Pyxk7r93G4Rei9s54bvoj49U3qrhhHEkhnILpbwo",
    "refreshToken": "70be2684-11bd-4e97-bbec-594204059016"
}


**STEP 3 — Use Access Token in Postman**
Only admin can add product
Authorization tab → Select **Bearer Token**

**STEP 4 — Create Product**
POST http://localhost:8080/api/v1/products
**Input:**
{
  "productName": "Laptop"
}
**Output:**
{
    "id": 1,
    "productName": "Laptop",
    "createdBy": "ADMIN",
    "createdOn": "2026-02-20T12:09:23.685601"
}


**STEP 5 — Get All Products (Pagination)**
GET http://localhost:8080/api/v1/products?page=0&size=5 OR http://localhost:8080/api/v1/products
**Output:**
{
    "content": [
        {
            "id": 1,
            "productName": "Laptop",
            "createdBy": "ADMIN",
            "createdOn": "2026-02-20T12:09:23.685601"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 5,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 0,
        "unpaged": false,
        "paged": true
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "first": true,
    "numberOfElements": 1,
    "size": 5,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "empty": false
}


**STEP 6 — Get Product By ID**
GET http://localhost:8080/api/v1/products/1
**Output:**
{
    "id": 1,
    "productName": "Laptop",
    "createdBy": "ADMIN",
    "createdOn": "2026-02-20T12:09:23.685601"
}

**STEP 7 — Update Product**
PUT http://localhost:8080/api/v1/products/1
**Intput:**
{
  "productName": "Gaming Laptop"
}
**Output:**
{
    "id": 1,
    "productName": "Gaming Laptop",
    "createdBy": "ADMIN",
    "createdOn": "2026-02-20T12:09:23.685601"
}


**STEP 8 — Delete Product**
DELETE http://localhost:8080/api/v1/products/1
**Output:**
Status 200 OK
Product deleted successfully


**STEP 9 — Add Item to Product**
POST http://localhost:8080/api/v1/products/2/items
**Input:**
{
  "name": "KeyBoard",
  "quantity": 2
}
**Output:**
{
    "id": 5,
    "name": "KeyBoard",
    "quantity": 2
}




**Author**
Vaibhav Vhankade
Java Backend Developer
