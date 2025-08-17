package com.workouttracker.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Workout Tracker API",
        version = "1.0.0",
        description = """
            # Workout Tracker API Documentation
            
            A comprehensive fitness tracking application that allows users to plan, track, and analyze their workout routines.
            
            ## Features
            
            - **User Management**: Registration, authentication, and profile management
            - **Exercise Library**: Comprehensive database of exercises with categories and difficulty levels
            - **Workout Planning**: Create and schedule custom workout routines
            - **Progress Tracking**: Track sets, reps, weights, and performance metrics
            - **Analytics & Reports**: Generate detailed progress reports and analytics
            - **Data Export**: Export workout data in various formats
            
            ## Authentication
            
            This API uses JWT (JSON Web Token) for authentication. Include the JWT token in the Authorization header:
            ```
            Authorization: Bearer <your-jwt-token>
            ```
            
            ## Getting Started
            
            1. Register a new account using `/auth/register`
            2. Login to get your JWT token using `/auth/login`
            3. Use the token to access protected endpoints
            4. Explore the exercise library with `/exercises`
            5. Create your first workout with `/workouts`
            
            ## Support
            
            For support and questions, please contact our development team.
            """,
        contact = @Contact(
            name = "Workout Tracker Development Team",
            email = "support@workouttracker.com",
            url = "https://github.com/workouttracker/api"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080/api/v1",
            description = "Development Server"
        ),
        @Server(
            url = "https://api.workouttracker.com/api/v1",
            description = "Production Server"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT authentication",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new io.swagger.v3.oas.models.Components()
                        .addResponses("BadRequest", createErrorResponse(
                                "Bad Request",
                                "The request was invalid or cannot be served",
                                """
                                {
                                  "error": "Validation failed",
                                  "message": "Username is required",
                                  "timestamp": 1640995200000
                                }
                                """
                        ))
                        .addResponses("Unauthorized", createErrorResponse(
                                "Unauthorized",
                                "Authentication is required and has failed or has not been provided",
                                """
                                {
                                  "error": "Unauthorized",
                                  "message": "Full authentication is required to access this resource",
                                  "timestamp": 1640995200000
                                }
                                """
                        ))
                        .addResponses("Forbidden", createErrorResponse(
                                "Forbidden",
                                "The request was valid but the server is refusing action",
                                """
                                {
                                  "error": "Access denied",
                                  "message": "You don't have permission to access this resource",
                                  "timestamp": 1640995200000
                                }
                                """
                        ))
                        .addResponses("NotFound", createErrorResponse(
                                "Not Found",
                                "The requested resource could not be found",
                                """
                                {
                                  "error": "Resource not found",
                                  "message": "User not found with id: 123",
                                  "timestamp": 1640995200000
                                }
                                """
                        ))
                        .addResponses("InternalServerError", createErrorResponse(
                                "Internal Server Error",
                                "An unexpected error occurred on the server",
                                """
                                {
                                  "error": "Internal server error",
                                  "message": "An unexpected error occurred",
                                  "timestamp": 1640995200000
                                }
                                """
                        ))
                        
                        // Add common request examples
                        .addExamples("UserRegistrationRequest", createExample(
                                "User Registration Request",
                                "Complete user registration with all optional fields",
                                """
                                {
                                  "username": "john_doe",
                                  "email": "john.doe@example.com",
                                  "password": "SecurePass123!",
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "dateOfBirth": "1990-05-15T00:00:00",
                                  "gender": "MALE",
                                  "heightCm": 180,
                                  "weightKg": 75.5,
                                  "activityLevel": "MODERATELY_ACTIVE"
                                }
                                """
                        ))
                        .addExamples("BasicUserRegistrationRequest", createExample(
                                "Basic User Registration Request",
                                "Minimal user registration with required fields only",
                                """
                                {
                                  "username": "jane_smith",
                                  "email": "jane.smith@example.com",
                                  "password": "MyPassword123"
                                }
                                """
                        ))
                        .addExamples("LoginRequest", createExample(
                                "Login Request",
                                "User login with username or email",
                                """
                                {
                                  "usernameOrEmail": "john_doe",
                                  "password": "SecurePass123!"
                                }
                                """
                        ))
                        .addExamples("WorkoutCreationRequest", createExample(
                                "Workout Creation Request",
                                "Create a new workout with exercises",
                                """
                                {
                                  "name": "Upper Body Strength",
                                  "description": "Focus on chest, shoulders, and arms",
                                  "scheduledDate": "2024-01-15T10:00:00",
                                  "exercises": [
                                    {
                                      "exerciseId": 1,
                                      "sets": 3,
                                      "reps": 12,
                                      "weight": 60.0,
                                      "restSeconds": 90,
                                      "notes": "Focus on controlled movement"
                                    },
                                    {
                                      "exerciseId": 5,
                                      "sets": 3,
                                      "reps": 10,
                                      "weight": 25.0,
                                      "restSeconds": 60
                                    }
                                  ]
                                }
                                """
                        ))
                        
                        // Add common response examples
                        .addExamples("SuccessfulRegistrationResponse", createExample(
                                "Successful Registration Response",
                                "Response after successful user registration",
                                """
                                {
                                  "message": "User registered successfully",
                                  "user": {
                                    "id": 1,
                                    "username": "john_doe",
                                    "email": "john.doe@example.com",
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "dateOfBirth": "1990-05-15T00:00:00",
                                    "gender": "MALE",
                                    "heightCm": 180,
                                    "weightKg": 75.5,
                                    "activityLevel": "MODERATELY_ACTIVE",
                                    "createdAt": "2024-01-15T10:30:00",
                                    "updatedAt": "2024-01-15T10:30:00"
                                  }
                                }
                                """
                        ))
                        .addExamples("SuccessfulLoginResponse", createExample(
                                "Successful Login Response",
                                "Response after successful authentication",
                                """
                                {
                                  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                  "tokenType": "Bearer",
                                  "expiresIn": 86400000,
                                  "user": {
                                    "id": 1,
                                    "username": "john_doe",
                                    "email": "john.doe@example.com",
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "createdAt": "2024-01-15T10:30:00"
                                  }
                                }
                                """
                        ))
                        .addExamples("ExerciseListResponse", createExample(
                                "Exercise List Response",
                                "List of exercises with pagination",
                                """
                                {
                                  "content": [
                                    {
                                      "id": 1,
                                      "name": "Barbell Bench Press",
                                      "description": "Lie on bench, lower barbell to chest, press up explosively",
                                      "category": "STRENGTH_TRAINING",
                                      "primaryMuscleGroup": "CHEST",
                                      "difficultyLevel": "INTERMEDIATE",
                                      "active": true
                                    },
                                    {
                                      "id": 2,
                                      "name": "Push-ups",
                                      "description": "Basic bodyweight chest exercise",
                                      "category": "STRENGTH_TRAINING",
                                      "primaryMuscleGroup": "CHEST",
                                      "difficultyLevel": "BEGINNER",
                                      "active": true
                                    }
                                  ],
                                  "pageable": {
                                    "pageNumber": 0,
                                    "pageSize": 20,
                                    "sort": { "sorted": false }
                                  },
                                  "totalElements": 150,
                                  "totalPages": 8,
                                  "first": true,
                                  "last": false
                                }
                                """
                        ))
                        .addExamples("WorkoutProgressReport", createExample(
                                "Workout Progress Report",
                                "Comprehensive progress report with analytics",
                                """
                                {
                                  "reportId": "RPT-1640995200000",
                                  "reportType": "PROGRESS_REPORT",
                                  "title": "Workout Progress Report",
                                  "description": "Comprehensive workout progress analysis",
                                  "generatedAt": "2024-01-15T10:30:00",
                                  "periodStart": "2024-01-01T00:00:00",
                                  "periodEnd": "2024-01-15T23:59:59",
                                  "userSummary": {
                                    "userId": 1,
                                    "username": "john_doe",
                                    "email": "john.doe@example.com",
                                    "fullName": "John Doe",
                                    "age": 33,
                                    "fitnessLevel": "Intermediate",
                                    "primaryGoal": "General Fitness"
                                  },
                                  "workoutSummary": {
                                    "totalWorkouts": 12,
                                    "completedWorkouts": 10,
                                    "cancelledWorkouts": 1,
                                    "completionRate": 83.33,
                                    "totalExercises": 45,
                                    "uniqueExercises": 15,
                                    "averageWorkoutDuration": 65.5,
                                    "totalCaloriesBurned": 3250.0
                                  },
                                  "progressMetrics": {
                                    "strengthGainPercentage": 12.5,
                                    "enduranceImprovement": 8.3,
                                    "consistencyScore": 83.33,
                                    "personalRecords": 3,
                                    "averageRating": 4.2,
                                    "trendDirection": "IMPROVING",
                                    "achievements": ["Completed 10+ workouts"],
                                    "recommendations": ["Try to maintain at least 3 workouts per week"]
                                  }
                                }
                                """
                        ))
                );
    }

    private ApiResponse createErrorResponse(String description, String summary, String exampleJson) {
        return new ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .example(exampleJson)
                        )
                );
    }

    private Example createExample(String summary, String description, String value) {
        return new Example()
                .summary(summary)
                .description(description)
                .value(value);
    }
}
