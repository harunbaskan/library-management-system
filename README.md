Library Management System
Backend REST API for a library system. Built with Spring Boot and MySQL.
About
I built this to learn Spring Boot and practice building a proper backend project. It's a library system where you can add books, register members, and handle borrowing/returning.
Main features:

Book management (add, update, delete, search)
Member registration with 3 membership types
Borrow/return system with due dates
Overdue tracking and fine calculation
Different borrow limits per membership (Standard: 3, Student: 5, Premium: 7)

Tech

Java 17
Spring Boot 3.2
Spring Data JPA
MySQL
JUnit 5 + Mockito
Maven

Project Structure
src/main/java/com/libraryapp/
├── model/        # Database entities
├── repository/   # Data access layer
├── service/      # Business logic
├── controller/   # API endpoints
├── dto/          # Request/Response objects
└── exception/    # Error handling
API
Books - /api/books

POST /api/books - add book
GET /api/books - get all
GET /api/books/{id} - get by id
PUT /api/books/{id} - update
DELETE /api/books/{id} - delete
GET /api/books/search?keyword=java - search
GET /api/books/genre/TECHNOLOGY - filter by genre
GET /api/books/available - available only

Members - /api/members

POST /api/members - register
GET /api/members - get all
GET /api/members/{id} - get by id
PUT /api/members/{id} - update
PATCH /api/members/{id}/deactivate - deactivate
PATCH /api/members/{id}/activate - activate
GET /api/members/search?name=alice - search

Borrowing - /api/borrows

POST /api/borrows - borrow a book
PATCH /api/borrows/{id}/return - return
GET /api/borrows/member/{id} - member history
GET /api/borrows/overdue - overdue list
GET /api/borrows/active - active borrows

Setup

Create database:

sqlCREATE DATABASE library_db;

Edit application.properties with your MySQL password
Run:

bashmvn spring-boot:run

Open http://localhost:8080/api/books

Tests
bashmvn test