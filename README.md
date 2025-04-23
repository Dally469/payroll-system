# Modern Payroll System

This application is a modern, scalable payroll system built with Spring Boot that manages employee attendance, activities, and payroll processing.

## Table of Contents
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Features](#features)
- [Security](#security)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Development](#development)
- [Scalability Considerations](#scalability-considerations)
- [Future Enhancements](#future-enhancements)
- [API Usage Guide (Postman)](#api-usage-guide-postman)
  - [Authentication](#authentication)
  - [Employee Management](#employee-management)
  - [Department Management](#department-management)
  - [Advance Management](#advance-management)
  - [Activity Tracking](#activity-tracking)
  - [Payroll Management](#payroll-management)
  - [WebSocket Endpoints](#websocket-endpoints)
  - [Postman Tips](#postman-tips)

## Technology Stack

- **Spring Boot**: For rapid application development
- **Spring Security**: For authentication and authorization
- **WebSocket**: For real-time updates
- **GraphQL**: For flexible API queries
- **PostgreSQL**: For relational database storage
- **Docker**: For containerization and easy deployment

## Architecture

The application follows a clean, layered architecture:

- **Domain Layer**: Contains entity classes that represent business objects
- **Repository Layer**: Data access interfaces that interact with the database
- **Service Layer**: Business logic implementation
- **Controller Layer**: REST API endpoints
- **DTO Layer**: Data Transfer Objects for API communication
- **GraphQL Resolvers**: GraphQL query and mutation handlers
- **WebSocket Controllers**: Real-time messaging handlers

## Features

1. **Employee Management**
    - CRUD operations for employees and departments
    - Employee details, including salary information

2. **Attendance Tracking**
    - Check-in and check-out functionality
    - Working hours calculation

3. **Activity Tracking**
    - Record different types of employee activities
    - Activity duration calculation

4. **Payroll Processing**
    - Generate payroll based on attendance and activities
    - Support for overtime, bonuses, and deductions
    - Payroll approval workflow

5. **Real-time Updates**
    - WebSocket for immediate notifications
    - GraphQL subscriptions for live data

## Security

- JWT-based authentication
- Role-based access control
- Secure API endpoints

## Getting Started

### Prerequisites

- JDK 17+
- Docker and Docker Compose
- PostgreSQL (or use the Docker container)

### Running the Application

1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/payroll-system.git
   cd payroll-system
   ```

2. Run with Docker Compose
   ```bash
   docker-compose up
   ```

3. Access the application
    - REST API: http://localhost:8080/api/
    - GraphQL: http://localhost:8080/graphql
    - WebSocket: ws://localhost:8080/ws

## API Documentation

- REST API documentation is available at http://localhost:8080/swagger-ui.html
- GraphQL playground is available at http://localhost:8080/graphiql

## Development

### Build

```bash
./mvnw clean package
```

### Run locally

```bash
./mvnw spring-boot:run
```

## Scalability Considerations

- Stateless application design for horizontal scaling
- Connection pooling for database efficiency
- Caching layer for frequently accessed data
- Containerized deployment for easy scaling

## Future Enhancements

- Implement event-driven architecture using Kafka
- Add reporting and analytics features
- Integrate with third-party payment providers
- Implement a front-end application using React or Angular

## API Usage Guide (Postman)

This section provides a comprehensive guide for using the Payroll System API with Postman, including all available endpoints, required request bodies, and authentication details.

### Authentication

#### 1. Login

```
POST /auth/login
```

**Request Body:**
```json
{
    "username": "john.doe",
    "password": "yourpassword"
}
```

**Response:**
```json
{
    "status": "success",
    "message": "Login successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "userId": "123e4567-e89b-12d3-a456-426614174000",
        "username": "john.doe",
        "email": "john.doe@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "organizationId": "123e4567-e89b-12d3-a456-426614174001",
        "organizationName": "ACME Inc",
        "roles": ["ADMIN"],
        "lastLoginAt": "2025-04-23T10:15:30"
    }
}
```

#### 2. Register New User

```
POST /auth/register
```

**Request Body:**
```json
{
    "username": "jane.smith",
    "password": "securepassword",
    "email": "jane.smith@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "organizationId": "123e4567-e89b-12d3-a456-426614174001",
    "roles": ["USER", "MANAGER"]
}
```

#### 3. Request Password Reset

```
POST /auth/reset-password/request
```

**Request Body:**
```json
{
    "email": "john.doe@example.com"
}
```

#### 4. Reset Password

```
POST /auth/reset-password/confirm
```

**Request Body:**
```json
{
    "token": "reset-token-from-email",
    "newPassword": "newSecurePassword"
}
```

#### 5. Change Password

```
POST /auth/change-password
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "currentPassword": "oldPassword",
    "newPassword": "newSecurePassword"
}
```

### Employee Management

#### 1. Get All Employees in Organization

```
GET /api/employees
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

#### 2. Get Employee by ID

```
GET /api/employees/{id}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

#### 3. Create New Employee

```
POST /api/employees
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "firstName": "Robert",
    "lastName": "Johnson",
    "email": "robert.johnson@example.com",
    "baseSalary": 5000.00,
    "dateOfJoining": "2025-01-01",
    "departmentId": "123e4567-e89b-12d3-a456-426614174002"
}
```

#### 4. Update Employee

```
PUT /api/employees/{id}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "firstName": "Robert",
    "lastName": "Johnson",
    "email": "robert.johnson@example.com",
    "baseSalary": 5500.00,
    "departmentId": "123e4567-e89b-12d3-a456-426614174002"
}
```

#### 5. Delete Employee

```
DELETE /api/employees/{id}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

### Department Management

#### 1. Get All Departments in Organization

```
GET /api/departments
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

#### 2. Get Department by ID

```
GET /api/departments/{id}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

#### 3. Create New Department

```
POST /api/departments
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "name": "Finance",
    "code": "FIN"
}
```

#### 4. Update Department

```
PUT /api/departments/{id}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "name": "Finance & Accounting",
    "code": "FINACC"
}
```

#### 5. Delete Department

```
DELETE /api/departments/{id}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

### Advance Management

#### 1. Get All Advances in Organization

```
GET /api/advances
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Query Parameters (optional):**
```
status=PENDING (options: PENDING, APPROVED, REJECTED)
```

#### 2. Get Employee Advances

```
GET /api/advances/employee/{employeeId}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Query Parameters (optional):**
```
status=APPROVED
```

#### 3. Get Advance by ID

```
GET /api/advances/{advanceId}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

#### 4. Request Advance

```
POST /api/advances/request
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "employeeId": "123e4567-e89b-12d3-a456-426614174003",
    "amount": 1000.00,
    "reason": "Medical expenses",
    "requestDate": "2025-04-24T10:00:00",
    "repaymentDate": "2025-05-24T10:00:00"
}
```

#### 5. Approve Advance

```
PUT /api/advances/{advanceId}/approve
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

#### 6. Reject Advance

```
PUT /api/advances/{advanceId}/reject
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "reason": "Insufficient documentation provided"
}
```

#### 7. Record Repayment

```
PUT /api/advances/{advanceId}/repay?amount=500.00
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

### Activity Tracking

#### 1. Get All Activities in Organization

```
GET /api/activities
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

#### 2. Get Employee Activities

```
GET /api/activities/employee/{employeeId}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

#### 3. Start Activity

```
POST /api/activities/start/{employeeId}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "description": "Working on project X",
    "type": "PROJECT"
}
```

#### 4. End Activity

```
PUT /api/activities/end/{activityId}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

### Payroll Management

#### 1. Get All Payrolls in Organization

```
GET /api/payrolls
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

#### 2. Get Employee Payrolls

```
GET /api/payrolls/employee/{employeeId}
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

#### 3. Generate Payroll

```
POST /api/payrolls/generate
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "employeeId": "123e4567-e89b-12d3-a456-426614174003",
    "startDate": "2025-04-01",
    "endDate": "2025-04-30"
}
```

#### 4. Update Payroll Status

```
PUT /api/payrolls/{payrollId}/status
```

**Headers:**
```
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "status": "APPROVED"
}
```

### WebSocket Endpoints

For real-time updates, the system supports WebSocket connections. You'll need to use a WebSocket client rather than standard Postman requests.

WebSocket Connection URL: `ws://localhost:8080/ws`

#### Available Topics:

1. `/topic/attendance` - Real-time attendance updates
2. `/topic/activity` - Real-time activity updates  
3. `/topic/payroll` - Real-time payroll updates

#### WebSocket Message Examples:

**Check-in Request:**
```json
{
    "destination": "/app/attendance/check-in",
    "body": "123e4567-e89b-12d3-a456-426614174003"
}
```

**Start Activity Request:**
```json
{
    "destination": "/app/activity/start",
    "body": {
        "employeeId": "123e4567-e89b-12d3-a456-426614174003",
        "activity": {
            "description": "Client meeting",
            "type": "MEETING"
        }
    }
}
```

### Postman Tips

1. **Create an environment** to store variables like:
   - `base_url` (e.g., http://localhost:8080)
   - `token` (to store the JWT after login)

2. **Add authentication header automatically** by using the following in the "Pre-request Script" tab:
   ```javascript
   pm.request.headers.add({
       key: 'Authorization',
       value: 'Bearer ' + pm.environment.get('token')
   });
   ```

3. **Save the token from login response** by using the following in the "Tests" tab of your login request:
   ```javascript
   var jsonData = pm.response.json();
   pm.environment.set("token", jsonData.data.token);
   ```

4. **Create a collection** for each logical group of endpoints (Auth, Employees, Departments, etc.)
