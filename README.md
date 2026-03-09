# Library Management System

A RESTful API for managing library operations built with Spring Boot and MySQL.

## About

This system handles core library operations: managing books, member registrations, and the borrowing/returning process. It includes features like overdue tracking, fine calculation, and search functionality.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2**
- **Spring Data JPA** - ORM and database access
- **MySQL** - production database
- **H2** - in-memory database for tests
- **Maven** - dependency management
- **JUnit 5 & Mockito** - unit and integration testing

## Features

- CRUD operations for books and members
- Book borrowing and returning with validation
- Overdue tracking and automatic fine calculation
- Search books by keyword, genre, or availability
- Member borrow limits based on membership type (Standard: 3, Student: 5, Premium: 7)
- Soft delete for members (deactivate/activate)
- Input validation with descriptive error messages
- Global exception handling with consistent error responses

## Project Structure
```
src/main/java/com/libraryapp/
├── model/          # Entity classes (Book, Member, BorrowRecord)
├── repository/     # Spring Data JPA repositories
├── service/        # Business logic layer
├── controller/     # REST API endpoints
├── dto/            # Data Transfer Objects (request/response)
├── exception/      # Custom exceptions and global handler
└── config/         # Configuration classes
```

## API Endpoints

### Books
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/books` | Add a new book |
| GET | `/api/books` | Get all books |
| GET | `/api/books/{id}` | Get book by ID |
| PUT | `/api/books/{id}` | Update a book |
| DELETE | `/api/books/{id}` | Delete a book |
| GET | `/api/books/search?keyword=` | Search by title/author |
| GET | `/api/books/genre/{genre}` | Filter by genre |
| GET | `/api/books/available` | Get available books |

### Members
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/members` | Register a member |
| GET | `/api/members` | Get all members |
| GET | `/api/members/{id}` | Get member by ID |
| PUT | `/api/members/{id}` | Update member info |
| PATCH | `/api/members/{id}/deactivate` | Deactivate member |
| PATCH | `/api/members/{id}/activate` | Reactivate member |
| GET | `/api/members/search?name=` | Search by name |
| GET | `/api/members/overdue` | Members with overdue books |

### Borrowing
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/borrows` | Borrow a book |
| PATCH | `/api/borrows/{id}/return` | Return a book |
| GET | `/api/borrows/{id}` | Get borrow record |
| GET | `/api/borrows/member/{id}` | Member's borrow history |
| GET | `/api/borrows/member/{id}/active` | Member's active borrows |
| GET | `/api/borrows/overdue` | All overdue records |
| GET | `/api/borrows/active` | All active borrows |

## Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Database Setup
```sql
CREATE DATABASE library_db;
```

### Configuration
Update `src/main/resources/application.properties` with your MySQL credentials:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Run Tests
```bash
mvn test
```

## Example Requests

### Create a Book
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "9780132350884",
    "genre": "TECHNOLOGY",
    "totalCopies": 3,
    "description": "A handbook of agile software craftsmanship"
  }'
```

### Borrow a Book
```bash
curl -X POST http://localhost:8080/api/borrows \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": 1,
    "memberId": 1,
    "notes": "Requested for study group"
  }'
```

### Return a Book
```bash
curl -X PATCH http://localhost:8080/api/borrows/1/return
```

## License

This project is for educational purposes.
```
