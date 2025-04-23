# Payroll System

A comprehensive payroll management system built with Spring Boot, designed to handle payroll processing, employee management, attendance tracking, advance payments, and batch operations for organizations.

## Features

### User & Authentication
- Secure user authentication with JWT tokens
- Role-based access control (Admin, Manager, Employee)
- Organization-based data isolation
- Password reset and account management

### Organization Management
- Multi-organization support
- Department management within organizations
- Employee assignment to departments

### Employee Management
- Complete employee record management
- Salary and compensation details
- Employment history tracking

### Payroll Processing
- Automated payroll generation
- Support for various payment periods (weekly, bi-weekly, monthly)
- Tax and deduction calculations
- Payroll history and reporting
- Batch payroll processing for multiple employees

### Attendance Management
- Employee check-in/check-out tracking
- Leave management
- Activity logging
- Work hour calculations

### Advance Payment System
- Advance payment requests
- Approval workflows
- Repayment tracking
- Batch advance processing

### Batch Processing
- Process multiple payroll requests in a single operation
- Process multiple advance requests in batch
- Track batch job status and progress
- Asynchronous processing for improved performance

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Database**: PostgreSQL
- **Security**: Spring Security, JWT
- **API Documentation**: Swagger/OpenAPI
- **Build Tool**: Maven
- **Async Processing**: Spring @Async, CompletableFuture
- **Data Validation**: Jakarta Bean Validation
- **Email**: Spring Mail, Thymeleaf templates

## API Endpoints

### Authentication
- `POST /auth/login` - Authenticate user and get token
  ```json
  {
    "username": "john.doe",
    "password": "password123"
  }
  ```

- `POST /auth/register` - Register a new user
  ```json
  {
    "username": "jane.smith",
    "password": "securePass123",
    "email": "jane.smith@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "roles": ["MANAGER", "USER"]
  }
  ```

- `POST /auth/reset-password/request` - Request password reset
  ```json
  {
    "email": "user@example.com"
  }
  ```

- `POST /auth/reset-password/confirm` - Complete password reset
  ```json
  {
    "token": "reset-token-from-email",
    "newPassword": "newSecurePassword123"
  }
  ```

- `POST /auth/change-password` - Change user password
  ```json
  {
    "currentPassword": "oldPassword123",
    "newPassword": "newPassword456"
  }
  ```

### Employees
- `GET /api/employees` - Get all employees for organization
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create new employee
  ```json
  {
    "firstName": "Robert",
    "lastName": "Johnson",
    "email": "robert.johnson@example.com",
    "phone": "123-456-7890",
    "documentId": "EMP-001",
    "dateOfJoining": "2023-01-15",
    "baseSalary": 5000.00,
    "departmentId": "550e8400-e29b-41d4-a716-446655440001"
  }
  ```

- `PUT /api/employees/{id}` - Update employee
  ```json
  {
    "firstName": "Robert",
    "lastName": "Johnson",
    "email": "robert.johnson@example.com",
    "phone": "123-456-7890",
    "baseSalary": 5500.00,
    "departmentId": "550e8400-e29b-41d4-a716-446655440001"
  }
  ```

- `DELETE /api/employees/{id}` - Delete employee

### Departments
- `GET /api/departments` - Get all departments for organization
- `GET /api/departments/{id}` - Get department by ID
- `POST /api/departments` - Create new department
  ```json
  {
    "name": "Finance",
    "code": "FIN"
  }
  ```

- `PUT /api/departments/{id}` - Update department
  ```json
  {
    "name": "Finance & Accounting",
    "code": "FINACC"
  }
  ```

- `DELETE /api/departments/{id}` - Delete department

### Payroll
- `GET /api/payrolls` - Get all payrolls for organization
- `GET /api/payrolls/employee/{employeeId}` - Get payrolls for employee
- `POST /api/payrolls/generate` - Generate payroll for employee
  ```json
  {
    "employeeId": "550e8400-e29b-41d4-a716-446655440002",
    "startDate": "2023-01-01",
    "endDate": "2023-01-31"
  }
  ```

- `PUT /api/payrolls/{payrollId}/status` - Update payroll status
  ```json
  {
    "status": "APPROVED"
  }
  ```

### Advances
- `GET /api/advances` - Get all advances for organization
- `GET /api/advances/employee/{employeeId}` - Get advances for employee
- `GET /api/advances/{advanceId}` - Get advance by ID
- `POST /api/advances/request` - Request an advance
  ```json
  {
    "employeeId": "550e8400-e29b-41d4-a716-446655440002",
    "amount": 1000.00,
    "reason": "Medical expenses",
    "requestDate": "2023-02-01T10:00:00",
    "repaymentDate": "2023-03-01T10:00:00"
  }
  ```

- `PUT /api/advances/{advanceId}/approve` - Approve an advance
- `PUT /api/advances/{advanceId}/reject` - Reject an advance
  ```json
  {
    "reason": "Insufficient documentation provided"
  }
  ```

- `PUT /api/advances/{advanceId}/repay` - Record advance repayment (uses a query parameter: `?amount=500.00`)

### Batch Processing
- `GET /api/batch` - Get all batch jobs for organization
- `GET /api/batch/{batchJobId}` - Get batch job details
- `POST /api/batch/payroll` - Submit batch payroll requests
  ```json
  {
    "requestedBy": "550e8400-e29b-41d4-a716-446655440003",
    "payrolls": [
      {
        "employeeId": "550e8400-e29b-41d4-a716-446655440004",
        "startDate": "2023-01-01",
        "endDate": "2023-01-31"
      },
      {
        "employeeId": "550e8400-e29b-41d4-a716-446655440005",
        "startDate": "2023-01-01",
        "endDate": "2023-01-31"
      }
    ],
    "payPeriod": "January 2023",
    "description": "Monthly payroll processing for department X",
    "callbackUrl": "https://example.com/webhook/payroll-complete",
    "skipValidation": false
  }
  ```

- `POST /api/batch/advances` - Submit batch advance requests
  ```json
  {
    "requestedBy": "550e8400-e29b-41d4-a716-446655440003",
    "requests": [
      {
        "employeeId": "550e8400-e29b-41d4-a716-446655440004",
        "amount": 1000.00,
        "reason": "Medical expenses",
        "requestDate": "2023-02-01T10:00:00",
        "repaymentDate": "2023-03-01T10:00:00"
      },
      {
        "employeeId": "550e8400-e29b-41d4-a716-446655440005",
        "amount": 500.00,
        "reason": "Education expenses",
        "requestDate": "2023-02-01T10:00:00",
        "repaymentDate": "2023-03-01T10:00:00"
      }
    ],
    "description": "Batch advances for department X",
    "callbackUrl": "https://example.com/webhook/advances-complete",
    "skipValidation": false
  }
  ```

- `POST /api/batch/advances/action` - Perform batch actions on advances
  ```json
  {
    "actionBy": "550e8400-e29b-41d4-a716-446655440003",
    "advanceRequestIds": [
      "550e8400-e29b-41d4-a716-446655440006",
      "550e8400-e29b-41d4-a716-446655440007"
    ],
    "action": "APPROVE",
    "comment": "Approved after documentation review",
    "notifyEmployees": true,
    "callbackUrl": "https://example.com/webhook/action-complete"
  }
  ```

## Getting Started

### Prerequisites
- Java 17 or higher
- PostgreSQL database
- Maven

### Configuration
1. Clone the repository
2. Configure database connection in `application.properties`:
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/payroll
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
3. Configure mail server (optional) for password reset emails:
   ```
   spring.mail.host=smtp.example.com
   spring.mail.port=587
   spring.mail.username=your_email
   spring.mail.password=your_password
   ```

### Building and Running
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will be available at http://localhost:8080

## API Response Format

All API responses follow a standard format:

```json
{
  "status": "success",
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  }
}
```

For error responses:

```json
{
  "status": "error",
  "message": "Error message describing what went wrong"
}
```

## Batch Processing Overview

The batch processing system allows for efficient handling of multiple payroll or advance operations in a single request. This is especially useful for processing payroll for all employees at once or handling multiple advance requests efficiently.

### Batch Job Entity
The system maintains detailed records of batch operations, including:
- Job type (PAYROLL, ADVANCE)
- Status (SUBMITTED, PROCESSING, COMPLETED, FAILED)
- Progress metrics (total requests, processed, successful, failed)
- Timestamps (submitted, started, completed)
- Organization and user context

### Asynchronous Processing
Batch operations run asynchronously to avoid blocking client connections. The system:
1. Immediately returns a job receipt when a batch is submitted
2. Processes the batch in the background
3. Maintains real-time status updates
4. Provides webhook callbacks (optional) when processing completes

### Integration
Batch operations integrate with the existing approval workflows and maintain all validation rules and business logic defined for individual operations.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
