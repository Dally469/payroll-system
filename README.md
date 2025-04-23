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
- `POST /auth/register` - Register a new user
- `POST /auth/reset-password/request` - Request password reset
- `POST /auth/reset-password/confirm` - Complete password reset
- `POST /auth/change-password` - Change user password

### Employees
- `GET /api/employees` - Get all employees for organization
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create new employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee

### Departments
- `GET /api/departments` - Get all departments for organization
- `GET /api/departments/{id}` - Get department by ID
- `POST /api/departments` - Create new department
- `PUT /api/departments/{id}` - Update department
- `DELETE /api/departments/{id}` - Delete department

### Payroll
- `GET /api/payrolls` - Get all payrolls for organization
- `GET /api/payrolls/employee/{employeeId}` - Get payrolls for employee
- `POST /api/payrolls/generate` - Generate payroll for employee
- `PUT /api/payrolls/{payrollId}/status` - Update payroll status

### Advances
- `GET /api/advances` - Get all advances for organization
- `GET /api/advances/employee/{employeeId}` - Get advances for employee
- `GET /api/advances/{advanceId}` - Get advance by ID
- `POST /api/advances/request` - Request an advance
- `PUT /api/advances/{advanceId}/approve` - Approve an advance
- `PUT /api/advances/{advanceId}/reject` - Reject an advance
- `PUT /api/advances/{advanceId}/repay` - Record advance repayment

### Batch Processing
- `GET /api/batch` - Get all batch jobs for organization
- `GET /api/batch/{batchJobId}` - Get batch job details
- `POST /api/batch/payroll` - Submit batch payroll requests
- `POST /api/batch/advances` - Submit batch advance requests
- `POST /api/batch/advances/action` - Perform batch actions on advances

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
