# 🏋️ Workout Tracker Backend

A comprehensive Spring Boot REST API for tracking workouts, exercises, and fitness progress. Built with modern Java technologies and JWT authentication.

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Security](#-security)
- [Configuration](#-configuration)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Contributing](#-contributing)

## ✨ Features

### 🔐 Authentication & User Management

- JWT-based authentication
- User registration and login
- Password encryption with BCrypt
- User profile management
- Role-based access control

### 💪 Exercise Management

- Comprehensive exercise catalog
- Exercise categorization (Cardio, Strength Training, Flexibility, etc.)
- Muscle group targeting
- Difficulty level classification
- Equipment requirements tracking

### 🏃‍♂️ Workout Tracking

- Create and schedule workouts
- Track workout progress and completion
- Record sets, reps, weights, and duration
- Calorie burn tracking
- Workout status management (Scheduled, In Progress, Completed, Cancelled)

### 📊 Progress Analytics

- Weekly and monthly progress reports
- Performance analytics and statistics
- Goal tracking and achievement monitoring
- Personal record tracking
- Export functionality (PDF/CSV)

### 🔍 Advanced Features

- RESTful API with comprehensive documentation
- H2 database for development, PostgreSQL for production
- OpenAPI/Swagger documentation
- CORS configuration for frontend integration
- Comprehensive logging and monitoring

## 🛠️ Technology Stack

- **Java 17** - Modern Java features and performance
- **Spring Boot 3.2.5** - Rapid application development framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data access layer
- **Hibernate** - Object-relational mapping
- **JWT (JSON Web Tokens)** - Stateless authentication
- **H2 Database** - In-memory database for development
- **PostgreSQL** - Production database
- **Maven** - Dependency management and build tool
- **OpenAPI/Swagger** - API documentation
- **BCrypt** - Password hashing

## 📁 Project Structure

```
workout-tracker/
├── src/
│   ├── main/
│   │   ├── java/com/workouttracker/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST API controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── mapper/      # Object mappers
│   │   │   │   ├── request/     # Request DTOs
│   │   │   │   └── response/    # Response DTOs
│   │   │   ├── model/           # JPA entities
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── security/        # Security configuration
│   │   │   └── service/         # Business logic layer
│   │   └── resources/
│   │       └── application.yml  # Application configuration
│   └── test/                    # Test classes
├── pom.xml                      # Maven configuration
└── README.md                    # This file
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL (for production)

### Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd workout-tracker
   ```

2. **Build the project**

   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080/api/v1`

### Development Setup

For development, the application uses H2 in-memory database. You can access the H2 console at:

- URL: `http://localhost:8080/api/v1/h2-console`
- JDBC URL: `jdbc:h2:mem:workout_tracker`
- Username: `sa`
- Password: `password`

## 📚 API Documentation

### Swagger UI

Access the interactive API documentation at:
`http://localhost:8080/api/v1/swagger-ui.html`

#### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123!"
}
```

Response includes JWT token:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

### Exercise Endpoints

#### Get All Exercises (Public)

```http
GET /api/v1/exercises/public
```

#### Get Exercises by Category

```http
GET /api/v1/exercises/category/STRENGTH_TRAINING
```

#### Get Exercises by Muscle Group

```http
GET /api/v1/exercises/muscle-group/CHEST
```

### Workout Endpoints

#### Create Workout

```http
POST /api/v1/workouts
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "name": "Upper Body Strength",
  "description": "Focus on chest, back, and shoulders",
  "scheduledDateTime": "2024-01-15T10:00:00",
  "exercises": [
    {
      "exerciseId": 1,
      "sets": 3,
      "repetitions": 12,
      "weightKg": 50.0,
      "restSeconds": 90
    }
  ]
}
```

#### Get User Workouts

```http
GET /api/v1/workouts
Authorization: Bearer <jwt-token>
```

### Report Endpoints

#### Generate Progress Report

```http
GET /api/v1/reports/progress?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <jwt-token>
```

#### Export Workout Data (PDF)

```http
GET /api/v1/reports/export/pdf?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <jwt-token>
```

## 🗄️ Database Schema

### Core Entities

#### User

- Personal information (name, email, date of birth)
- Physical attributes (height, weight, gender)
- Activity level and fitness goals
- Authentication credentials

#### Exercise

- Exercise details (name, description, instructions)
- Categorization (category, muscle groups, difficulty)
- Equipment requirements
- Media resources (images, videos)

#### Workout

- Workout planning and scheduling
- Status tracking (scheduled, in progress, completed)
- Duration and calorie tracking
- User association

#### WorkoutExercise

- Junction table linking workouts and exercises
- Performance data (sets, reps, weights)
- Completion tracking
- Notes and observations

## 🔒 Security

### JWT Authentication

- Stateless authentication using JSON Web Tokens
- Configurable token expiration (default: 24 hours)
- Secure token storage and validation

### Password Security

- BCrypt password hashing
- Configurable password strength requirements
- Secure password reset functionality

### CORS Configuration

- Configurable cross-origin resource sharing
- Support for frontend integration
- Secure header configuration

## ⚙️ Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
# Server Configuration
server:
  port: 8080
  servlet:
    context-path: /api/v1

# Database Configuration
spring:
  datasource:
    url: jdbc:h2:mem:workout_tracker # Development
    # url: jdbc:postgresql://localhost:5432/workout_tracker  # Production

# JWT Configuration
jwt:
  secret: your-secret-key
  expiration: 86400000 # 24 hours

# OpenAPI Documentation
springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

### Environment Variables

For production deployment, set these environment variables:

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/workout_tracker
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password
JWT_SECRET=your_secure_jwt_secret
JWT_EXPIRATION=86400000
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Structure

- Unit tests for services and repositories
- Integration tests for controllers
- Security component tests
- Database interaction tests

## 🚀 Deployment

### Production Deployment

1. **Build the application**

   ```bash
   mvn clean package -Pproduction
   ```

2. **Set environment variables**

   ```bash
   export DATABASE_URL=jdbc:postgresql://your-db-host:5432/workout_tracker
   export JWT_SECRET=your-secure-secret-key
   ```

3. **Run the application**
   ```bash
   java -jar target/workout-tracker-backend-1.0.0.jar
   ```

### Docker Deployment

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/workout-tracker-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java coding conventions
- Write comprehensive tests
- Update documentation for new features
- Use meaningful commit messages
- Ensure all tests pass before submitting PR

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Ishan Shrestha**

- Email: sthaishan2004@gmail.com
