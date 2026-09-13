# Employee Management System

A Spring Boot REST API for managing employees. Built while learning core backend concepts: JPA, validation, Flyway migrations, dynamic filtering with Specifications, global exception handling, and Spring transaction/isolation behavior.

## Features

- **CRUD APIs** for employees
- **Pagination & filtering** by department, name, and salary range
- **Request validation** with `@Valid` and custom error responses
- **Flyway** database migrations
- **Swagger UI** for API exploration
- **Transaction demos** — rollback, propagation, dirty read, non-repeatable read, phantom read

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL |
| Migrations | Flyway |
| API docs | SpringDoc OpenAPI 3 |
| Build | Maven |

## Prerequisites

- Java 21
- MySQL 8+
- Git (optional)

## Getting Started

### 1. Clone or download the project

```bash
git clone https://github.com/Dhruv9916/Employee-Management-System-.git
cd Employee-Management-System-
```

### 2. Create the database

```sql
CREATE DATABASE employee_db;
```

### 3. Configure application properties

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `application.properties` and set your MySQL username and password:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The server starts on **http://localhost:8080**.

### 5. Open Swagger UI

http://localhost:8080/swagger-ui.html

---

## API Endpoints

Base path: `/employees`

### Employee CRUD

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/employees` | Create a new employee |
| `GET` | `/employees` | List employees (paginated, filterable) |
| `GET` | `/employees/{id}` | Get employee by ID |
| `PUT` | `/employees/{id}` | Update employee |
| `DELETE` | `/employees/{id}` | Delete employee |

#### Create employee — sample request

```bash
curl -X POST http://localhost:8080/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dhruv Kumar",
    "email": "dhruv@example.com",
    "department": "Engineering",
    "salary": 100000
  }'
```

#### List with filters

```bash
curl "http://localhost:8080/employees?department=IT&minSalary=80000&maxSalary=120000&page=0&size=10"
```

Query parameters:

| Parameter | Type | Description |
|-----------|------|-------------|
| `department` | string | Exact department match |
| `name` | string | Partial name search (case-insensitive) |
| `minSalary` | number | Minimum salary (>= 0) |
| `maxSalary` | number | Maximum salary (>= 0) |
| `page` | number | Page number (0-based) |
| `size` | number | Page size |

#### Get by ID

```bash
curl http://localhost:8080/employees/1
```

#### Update

```bash
curl -X PUT http://localhost:8080/employees/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dhruv Kumar",
    "email": "dhruv@example.com",
    "department": "Engineering",
    "salary": 110000
  }'
```

#### Delete

```bash
curl -X DELETE http://localhost:8080/employees/1
```

---

## Request / Response Model

**EmployeeRequest** (create & update):

| Field | Validation |
|-------|------------|
| `name` | Required |
| `email` | Required, valid email, unique in DB |
| `department` | Required |
| `salary` | Required, must be > 0 |

**EmployeeResponse**: `id`, `name`, `email`, `department`, `salary`

---

## Error Handling

Global exception handler returns structured JSON:

```json
{
  "timestamp": "2026-09-13T12:00:00",
  "status": 404,
  "message": "Employee not found with id: 99"
}
```

Handles:

- Employee not found → `404`
- Validation errors → `400`
- Invalid query params → `400`
- Illegal arguments (e.g. minSalary > maxSalary) → `400`

---

## Database & Flyway

Migrations: `src/main/resources/db/migration/`

| Version | Script | Purpose |
|---------|--------|---------|
| V2 | `V2__add_email_constraints.sql` | Email `NOT NULL` + unique constraint |

Hibernate is set to `ddl-auto=validate` — schema changes go through Flyway, not Hibernate auto-DDL.

---

## Transaction & Isolation Demos

Learning endpoints to observe Spring `@Transactional` and MySQL isolation levels.

| Method | Endpoint | What it demonstrates |
|--------|----------|----------------------|
| `POST` | `/employees/test-transaction` | Rollback on checked exception (`rollbackFor = Exception.class`) |
| `POST` | `/employees/test-propagation` | `REQUIRES_NEW` propagation — B commits even if A fails |
| `POST` | `/employees/test-isolation` | Update + sleep + rollback (dirty read scenario) |
| `GET` | `/employees/test-isolation/read` | Read salary while another transaction is open |
| `POST` | `/employees/read-twice` | Non-repeatable read — read same row twice with 15s gap |
| `POST` | `/employees/update-commit` | Commit salary change during read-twice sleep |
| `POST` | `/employees/transaction-test/phantom-read` | Range query twice (`salary >= 100000`) with 15s gap |
| `POST` | `/employees/transaction-test/phantom-insert` | Insert high-salary employee during phantom-read sleep |

### Non-repeatable read demo

**Terminal 1** (start first — blocks ~15 seconds):

```bash
curl -X POST http://localhost:8080/employees/read-twice
```

**Terminal 2** (run during the sleep):

```bash
curl -X POST http://localhost:8080/employees/update-commit
```

Check application logs for first vs second salary read.

### Phantom read demo

**Terminal 1** (start first):

```bash
curl -X POST http://localhost:8080/employees/transaction-test/phantom-read
```

**Terminal 2** (run during sleep; wait until it returns `Transaction A committed`):

```bash
curl -X POST http://localhost:8080/employees/transaction-test/phantom-insert
```

Expected with `READ_COMMITTED`: count goes from 10 → 11. With `SERIALIZABLE`: count stays 10.

---

## Project Structure

```
src/main/java/com/dhruv/employee_management/
├── controller/
│   └── EmployeeController.java      REST endpoints
├── service/
│   ├── EmployeeService.java         CRUD + transaction demos
│   ├── IsolationService.java        Isolation level demos
│   └── PropagationService.java      Propagation demos
├── repository/
│   └── EmployeeRepository.java      JPA + Specification support
├── entity/
│   └── Employee.java                JPA entity
├── dto/
│   ├── EmployeeRequest.java         Input DTO + validation
│   └── EmployeeResponse.java        Output DTO
├── mapper/
│   └── EmployeeMapper.java          Entity ↔ DTO mapping
├── specification/
│   └── EmployeeSpecification.java   Dynamic query filters
└── exception/
    ├── GlobalExceptionHandler.java
    ├── EmployeeNotFoundException.java
    └── ErrorResponse.java
```

---

## Build & Test

```bash
# Compile
./mvnw compile

# Run tests
./mvnw test

# Package (creates target/ — do not commit)
./mvnw package
```

---

## Author

**Dhruv Kumar**

- GitHub: [@Dhruv9916](https://github.com/Dhruv9916)
- Repository: [Employee-Management-System-](https://github.com/Dhruv9916/Employee-Management-System-)

---

## License

This project is for learning and portfolio purposes.
