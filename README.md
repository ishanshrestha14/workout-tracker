# 🏋️ Workout Tracker Backend

[![CI](https://github.com/ishanshrestha14/workout-tracker/actions/workflows/ci.yml/badge.svg)](https://github.com/ishanshrestha14/workout-tracker/actions/workflows/ci.yml)

A Spring Boot REST API for tracking workouts, exercises, and fitness progress, secured with JWT authentication.

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
- User registration and login (by username or email)
- Password hashing with BCrypt
- Profile updates and password change
- Role-based access control (`USER` and `ADMIN`) with `@PreAuthorize`
- Admin-only user management endpoints

### 💪 Exercise Catalog

- Seeded catalog of exercises
- Categories (Cardio, Strength Training, Flexibility, etc.)
- Primary and secondary muscle groups
- Difficulty levels
- Equipment requirements
- Search and filtering by category, muscle group and difficulty

### 🏃‍♂️ Workout Tracking

- Create and schedule workouts
- Record sets, reps, weight, distance, duration and calories per exercise
- Workout lifecycle: Scheduled → In Progress → Completed (or Cancelled)
- Personal records per exercise (heaviest completed sets)
- Exercise history and progress over time

### 📊 Reports & Analytics

- Progress reports for any date range, plus weekly and monthly reports
- Workout, exercise and muscle-group statistics
- PDF export of progress reports
- CSV export of workout data

### 🔍 Platform

- OpenAPI/Swagger documentation
- Database schema managed by Flyway migrations
- H2 for development, PostgreSQL for production
- Docker and docker-compose setup
- CI with GitHub Actions
- CORS configuration for frontend integration

## 🛠️ Technology Stack

- **Java 17** - Language level (builds on JDK 17 and 21)
- **Spring Boot 3.2.5** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA / Hibernate** - Data access and ORM
- **Flyway** - Database migrations
- **Apache PDFBox** - PDF report generation
- **JWT (jjwt)** - Stateless authentication
- **H2 Database** - In-memory database for development and tests
- **PostgreSQL** - Production database
- **JUnit 5, Mockito, Spring MockMvc** - Testing
- **Maven** - Build tool
- **Docker / docker-compose** - Containerized deployment
- **GitHub Actions** - Continuous integration
- **OpenAPI/Swagger** - API documentation

## 📁 Project Structure

```
workout-tracker/
├── .github/workflows/ci.yml      # CI: mvn verify + Docker build
├── src/
│   ├── main/
│   │   ├── java/com/workouttracker/
│   │   │   ├── config/           # Security and OpenAPI configuration
│   │   │   ├── controller/       # REST API controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── mapper/       # Object mappers
│   │   │   │   ├── request/      # Request DTOs
│   │   │   │   └── response/     # Response DTOs
│   │   │   ├── model/            # JPA entities
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── security/         # JWT filter and utilities
│   │   │   └── service/          # Business logic layer
│   │   └── resources/
│   │       ├── application.yml   # Application configuration
│   │       └── db/migration/     # Flyway SQL migrations
│   └── test/java/com/workouttracker/
│       ├── controller/           # @WebMvcTest controller tests
│       ├── security/             # JWT tests
│       ├── service/              # Service unit tests
│       └── AuthFlowIntegrationTest.java
├── Dockerfile
├── docker-compose.yml
├── .env.example                  # Template for required environment variables
├── endpoints.md                  # Full endpoint list
└── pom.xml
```

## 🚀 Getting Started

### Prerequisites

- JDK 17 or 21
- Maven 3.6 or higher
- Docker (optional, for running with PostgreSQL)

### Run locally (H2 in-memory database)

1. **Clone the repository**

   ```bash
   git clone https://github.com/ishanshrestha14/workout-tracker.git
   cd workout-tracker
   ```

2. **Set a JWT secret** (required; there is no default)

   ```bash
   export JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
   ```

3. **Build and run**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The application starts on `http://localhost:8080/api/v1`. Flyway creates the schema on startup and the exercise catalog is seeded automatically.

### Run with Docker (PostgreSQL)

```bash
cp .env.example .env   # then set JWT_SECRET and DATABASE_PASSWORD
docker compose up --build
```

### H2 Console

In development you can inspect the in-memory database at:

- URL: `http://localhost:8080/api/v1/h2-console`
- JDBC URL: `jdbc:h2:mem:workout_tracker`
- Username: `sa`
- Password: _(empty)_

## 📚 API Documentation

### Swagger UI

Interactive API documentation is available at:
`http://localhost:8080/api/v1/swagger-ui.html`

See [endpoints.md](endpoints.md) for the full list of endpoints.

### Authentication

#### Register

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

#### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "john_doe",
  "password": "SecurePass123!"
}
```

The response includes the JWT:

```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "expiresAt": "2026-01-16T10:00:00",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

Send it on every other request as `Authorization: Bearer <accessToken>`.

### Exercise Endpoints

All exercise endpoints require authentication.

```http
GET /api/v1/exercises
GET /api/v1/exercises/category/STRENGTH_TRAINING
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
  "scheduledDateTime": "2026-01-15T10:00:00",
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

Report dates are ISO date-times (`yyyy-MM-ddTHH:mm:ss`).

#### Generate Progress Report

```http
GET /api/v1/reports/progress?startDate=2026-01-01T00:00:00&endDate=2026-01-31T23:59:59
Authorization: Bearer <jwt-token>
```

#### Export Progress Report (PDF)

```http
GET /api/v1/reports/export/pdf?startDate=2026-01-01T00:00:00&endDate=2026-01-31T23:59:59
Authorization: Bearer <jwt-token>
```

Returns an `application/pdf` download with a summary of the period, a table of workouts and the best performance per exercise.

#### Export Workout Data (CSV)

```http
GET /api/v1/reports/export/csv?startDate=2026-01-01T00:00:00&endDate=2026-01-31T23:59:59
Authorization: Bearer <jwt-token>
```

Returns a `text/csv` download with one row per exercise for workouts scheduled in the range:

```csv
Date,Workout,Status,Exercise,Category,Sets,Reps,Weight (kg),Distance (km),Duration (s),Calories,Completed
2026-01-15T10:00:00,Upper Body Strength,COMPLETED,Barbell Bench Press,STRENGTH_TRAINING,3,12,50.0,,,,true
```

## 🗄️ Database Schema

The schema is defined in Flyway migrations under `src/main/resources/db/migration`. Hibernate runs with `ddl-auto: validate`, so the application refuses to start if the entities and the schema drift apart. To change the schema, add a new `V<n>__description.sql` migration rather than editing an existing one.

### Core Entities

#### User

- Personal information (name, email, date of birth)
- Physical attributes (height, weight, gender)
- Activity level
- Role (`USER` or `ADMIN`)
- Authentication credentials

#### Exercise

- Exercise details (name, description, instructions)
- Categorization (category, muscle groups, difficulty)
- Equipment requirements
- Image and video URLs

#### Workout

- Workout planning and scheduling
- Status tracking (scheduled, in progress, completed, cancelled)
- Duration and calorie tracking
- User association

#### WorkoutExercise

- Links workouts and exercises
- Performance data (sets, reps, weight, distance, duration)
- Completion tracking
- Notes

## 🔒 Security

### JWT Authentication

- Stateless authentication using JSON Web Tokens (HMAC-SHA)
- Signing key supplied through the `JWT_SECRET` environment variable; nothing is hardcoded
- Configurable token expiration (default: 24 hours)

### Roles

- Everyone who registers is a `USER`; admins also have every `USER` permission
- `/users/**` and `GET /reports/user/{userId}` are admin-only
- The first admin is created on startup from `ADMIN_USERNAME`, `ADMIN_EMAIL` and `ADMIN_PASSWORD` (at least 12 characters). An existing account with that username is never promoted, so registering the name first does not grant admin rights
- To promote another user: `UPDATE users SET role = 'ADMIN' WHERE username = '...';`

### Password Security

- BCrypt password hashing
- Minimum password length enforced on registration

### CORS Configuration

- Allowed origins configured for common local frontend ports and Vercel/Netlify deployments

## ⚙️ Configuration

All secrets come from environment variables. Copy `.env.example` to `.env` for docker-compose, or export them in your shell.

| Variable                 | Required                | Description                                                         |
| ------------------------ | ----------------------- | ------------------------------------------------------------------- |
| `JWT_SECRET`             | Always                  | Base64-encoded key of at least 256 bits (`openssl rand -base64 64`) |
| `JWT_EXPIRATION`         | No                      | Token lifetime in ms (default `86400000`)                           |
| `SPRING_PROFILES_ACTIVE` | For production          | Set to `production` to use PostgreSQL                               |
| `DATABASE_URL`           | With `production`       | e.g. `jdbc:postgresql://localhost:5432/workout_tracker`             |
| `DATABASE_USERNAME`      | With `production`       | Database user                                                       |
| `DATABASE_PASSWORD`      | With `production`       | Database password                                                   |
| `ADMIN_USERNAME`         | No                      | Creates this admin account on startup if it does not exist          |
| `ADMIN_EMAIL`            | With `ADMIN_USERNAME`   | Email for the bootstrap admin                                       |
| `ADMIN_PASSWORD`         | With `ADMIN_USERNAME`   | Password for the bootstrap admin (min. 12 characters)               |

## 🧪 Testing

The project has 73 automated tests:

- **Service unit tests** (JUnit 5 + Mockito): authentication, workouts, users, admin bootstrap, CSV export
- **PDF tests**: generated documents are parsed back to check content, page breaks, truncation and character fallback
- **JWT tests**: valid tokens, forged signatures, tampered payloads, expired and malformed tokens
- **Controller tests** (`@WebMvcTest` with the real security configuration): request validation, status codes, response bodies
- **Authorization tests**: a `USER` is forbidden from `ADMIN` endpoints, anonymous callers get 401, admins are allowed
- **Integration tests** (`@SpringBootTest`, including a real HTTP server): Flyway migrations, register/login with a real JWT, error status codes, and the admin bootstrap

### Running Tests

```bash
# Run all tests
mvn test

# Run the full build as CI does
mvn verify

# Run a specific test class
mvn test -Dtest=AuthServiceTest
```

## 🚀 Deployment

### Docker Compose

```bash
cp .env.example .env   # set JWT_SECRET and DATABASE_PASSWORD
docker compose up --build -d
```

This starts PostgreSQL 16 and the API on port 8080. The API waits for the database to be healthy, and Flyway applies migrations on startup.

### Standalone JAR

```bash
mvn clean package
export SPRING_PROFILES_ACTIVE=production
export DATABASE_URL=jdbc:postgresql://your-db-host:5432/workout_tracker
export DATABASE_USERNAME=workout_tracker
export DATABASE_PASSWORD=your-db-password
export JWT_SECRET=your-base64-secret
java -jar target/workout-tracker-backend-1.0.0.jar
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java coding conventions
- Add tests for new behavior; CI must pass before merging
- Add a new Flyway migration for any schema change
- Update documentation for new features

## 👨‍💻 Author

**Ishan Shrestha**

- Email: sthaishan2004@gmail.com
