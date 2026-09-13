# Employee Management System

Spring Boot REST API for managing employees, with JPA, Flyway migrations, validation, and transaction/isolation learning endpoints.

## Tech stack

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA + Hibernate
- MySQL
- Flyway
- SpringDoc OpenAPI (Swagger UI)

## Prerequisites

- Java 21
- MySQL 8+
- Maven (or use included `./mvnw`)

## Setup

1. Create the database:

```sql
CREATE DATABASE employee_db;
```

2. Copy config and set your MySQL password:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

3. Run the app:

```bash
./mvnw spring-boot:run
```

4. Open Swagger UI: http://localhost:8080/swagger-ui.html

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/employees` | Create employee |
| GET | `/employees` | List employees (filter by department, name, min/max salary; supports pagination) |
| GET | `/employees/{id}` | Get employee by id |
| PUT | `/employees/{id}` | Update employee |
| DELETE | `/employees/{id}` | Delete employee |

### Transaction / isolation demos

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/employees/test-transaction` | Rollback demo (checked exception) |
| POST | `/employees/test-propagation` | Transaction propagation |
| POST | `/employees/test-isolation` | Dirty read / rollback demo |
| GET | `/employees/test-isolation/read` | Read during another transaction |
| POST | `/employees/read-twice` | Non-repeatable read demo (start first) |
| POST | `/employees/update-commit` | Commit update during read-twice sleep |
| POST | `/employees/transaction-test/phantom-read` | Phantom read demo (start first) |
| POST | `/employees/transaction-test/phantom-insert` | Insert during phantom-read sleep |

For phantom read: call **phantom-read** first, then **phantom-insert** while B is sleeping (~15s). Wait until insert returns `Transaction A committed` before B finishes.

## Database migrations

Flyway scripts live in `src/main/resources/db/migration/`.

## Project structure

```
src/main/java/com/dhruv/employee_management/
├── controller/     REST APIs
├── service/        Business logic + transaction demos
├── repository/     JPA repositories
├── entity/         Employee entity
├── dto/            Request/response objects
├── mapper/         Entity ↔ DTO mapping
├── specification/  Dynamic query filters
└── exception/      Global error handling
```
