# Modern Payroll System

This application is a modern, scalable payroll system built with Spring Boot that manages employee attendance, activities, and payroll processing.

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

### API Documentation

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
- Implement a front-end application using React or Angular# payroll-system
