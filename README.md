# INTEGRATOR — User Registration & Purchase Orders

## Subject: Programación Avanzada I
### Ing. y Lic. en Sistemas

---

# Domain Summary

## Goal
Implement a REST API following *Clean Architecture*, managing two main entities:

- **User**
- **Order**

Expected:

- Use Cases as application services 
- DTOs to decouple API <-> Domain 
- Relational DB persistence 
- Asynchronous flows using Scheduled Jobs or Pending Tasks 

---

# Global Evaluation Rules

- Java 21+, Spring Boot recommended 
- Must compile on any environment 
- Mandatory unit tests 
- Feature branches from `develop` (only for Team Challenge) 
- Lowercase package naming 
- Thin controllers 
- Custom exceptions + ControllerAdvice 
- Domain objects created via factory methods 

---

# Domain Model

## User
```
id: Long
email: String
password: String
status: UserStatus (PENDING, ACTIVE, EXPIRED)
activationCode: String?
activationExpiresAt: LocalDateTime?
createdAt: LocalDateTime
```

## Order
```
id: Long
user: User
status: OrderStatus (PENDING, PROCESSING, APPROVED, REJECTED, CANCELLED)
amount: Decimal
createdAt: LocalDateTime
updatedAt: LocalDateTime
```

## PendingTask
```
id: Long
type: PendingTaskType
status: PENDING | DONE | ERROR
createdAt: LocalDateTime
processedAt: LocalDateTime?
```

---

# Core Business Rules

- `User.email` must be unique 
- New users start in **PENDING** 
- Only ACTIVE users may create orders 
- Valid order transitions: 
 - PENDING -> PROCESSING -> APPROVED/REJECTED 
 - PENDING -> CANCELLED 
- Scheduled jobs must be idempotent 
- Export endpoints must generate valid downloadable files 

---

# Testing Guidance

- JUnit5 + Mockito 
- Clock/TimeProvider should be injected for time-dependent tests 
- Cover happy paths, expired tokens, invalid state transitions 
- Validate exported file content 

---

# Git Workflow (Only for Team Challenge)

- `develop` holds integrated code 
- Each use case must be implemented in a feature branch 
- Pull Requests require tests passing 

---

# Deliverable Checklist

- All assigned endpoints implemented 
- Use case logic tested 
- Domain factory methods 
- Validation + exceptions 
- Correct HTTP responses 
- One PR per use case (only for Team Challenge) 

---

# JSON Examples

## Create User
```json
{
 "email": "john@example.com",
 "password": "secret123"
}
```

## Create Order
```json
{
 "amount": 199.90
}
```

---

# Feature — Asynchronous Order Export
## Use Cases
- RegisterUser 
- ActivateUser (Scheduled job)
- CreateOrder 
- QueueExportOrdersTask (creates EXPORT_ORDERS type PendingTask)
- DownloadGeneratedFile 

## Endpoints
- POST /users 
- POST /users/{userId}/orders 
- POST /orders/export/request (create pending task)
- GET /orders/export/{taskId} (download when TaskStatus is PENDING, then update to DONE or ERROR properly)

---

# End of Document
