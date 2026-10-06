## 🔐 Authentication Controller (/auth)

### User Registration & Login
- `POST /auth/register` - Register a new user
- `POST /auth/login` - User login (returns JWT token)
- `POST /auth/logout` - User logout

### Profile Management
- `GET /auth/me` -  Get current user profile
- `PUT /auth/profile` - Update user profile
- `POST /auth/change-password` - Change user password

### Validation
- `GET /auth/check-username?username={username}` - Check username availability
- `GET /auth/check-email?email={email}` - Check email availability



## 🏋️ Exercise Controller (/exercises)

### Basic Exercise Operations
- `GET /exercises` - Get all active exercises
- `GET /exercises/{exerciseId}` - Get exercise by ID
- `GET /exercises/basic` - Get basic exercise list (for dropdowns)

### Filtering & Search
- `GET /exercises/category/{category}` - Get exercises by category
- `GET /exercises/muscle-group/{muscleGroup}` - Get exercises by muscle group
- `GET /exercises/difficulty/{difficultyLevel}` - Get exercises by difficulty
- `GET /exercises/search?name={name}` - Search exercises by name
- `GET /exercises/filtered?category={}&muscleGroup={}&searchTerm={}` - Get exercises with filters (paginated)
- `GET /exercises/targeting/{muscleGroup}` - Get exercises targeting muscle group

### Utility Endpoints
- `GET /exercises/random?limit={limit}` - Get random exercises (default 10, max 50)
- `GET /exercises/categories` - Get all exercise categories
- `GET /exercises/muscle-groups` - Get all muscle groups
- `GET /exercises/difficulty-levels` - Get all difficulty levels

### Statistics
- `GET /exercises/stats` - Get exercise statistics and counts
- `GET /exercises/count/category/{category}` - Get exercise count by category



## 🏃 Workout Controller (/workouts)

### CRUD Operations
- `POST /workouts` - Create a new workout
- `GET /workouts/{workoutId}` - Get workout by ID
- `PUT /workouts/{workoutId}` - Update workout
- `DELETE /workouts/{workoutId}` - Delete workout
## Workout Listings
- `GET /workouts` - Get all user workouts
- `GET /workouts/paginated` - Get paginated workouts
- `GET /workouts/upcoming` - Get upcoming scheduled workouts
- `GET /workouts/today` - Get today's workouts
- `GET /workouts/overdue` - Get overdue workouts

### Filtering & Search
- `GET /workouts/date-range?startDate={}&endDate={}` - Get workouts in date range
- `GET /workouts/search?name={name}` - Search workouts by name

### Workout Management
- `POST /workouts/{workoutId}/start` - Start a workout
- `POST /workouts/{workoutId}/complete` - Complete a workout (with optional notes & calories)
- `POST /workouts/{workoutId}/cancel` - Cancel a workout

### Statistics
- `GET /workouts/stats` - Get workout statistics


## 💪 Workout Exercise Controller (/workout-exercises)

Individual Exercise Management
- `GET /workout-exercises/workout/{workoutId}` - Get all exercises for a specific workout
- `GET /workout-exercises/{workoutExerciseId}` - Get specific workout exercise by ID
- `PUT /workout-exercises/{workoutExerciseId}` - Update exercise details (sets, reps, weight, notes)
- `POST /workout-exercises/{workoutExerciseId}/complete` - Mark exercise as completed

Progress Tracking & History
- `GET /workout-exercises/exercise/{exerciseId}/history`- Get user's exercise history
- `GET /workout-exercises/exercise/{exerciseId}/records` - Get personal records for exercise
- `GET /workout-exercises/exercise/{exerciseId}/progress` - Get exercise progress over time

Analytics & Statistics
- `GET /workout-exercises/stats` - Get exercise statistics for user
- `GET /workout-exercises/volume?startDate={}&endDate={}` - Get total volume lifted in date range
- `GET /workout-exercises/muscle-group-frequency` - Get workout frequency by muscle group
- `GET /workout-exercises/completed-count` - Get count of completed exercises
- `GET /workout-exercises/user/all` - Get all workout exercises for current user



## 📈 Report Controller (/reports)

Dates are ISO date-times (`yyyy-MM-ddTHH:mm:ss`).

- `GET /reports/progress?startDate={}&endDate={}` - Progress report for a date range
- `GET /reports/weekly` - Progress report for the past week
- `GET /reports/monthly` - Progress report for the past month
- `GET /reports/analytics?startDate={}&endDate={}` - Performance analytics
- `GET /reports/export/pdf?startDate={}&endDate={}` - Download progress report as PDF
- `GET /reports/export/csv?startDate={}&endDate={}` - Download workout data as CSV
- `GET /reports/user/{userId}?startDate={}&endDate={}` - Progress report for any user (**ADMIN**)



## 👥 User Controller (/users) — ADMIN only

- `GET /users` - List all users
- `GET /users/{userId}` - Get user by ID
- `GET /users/stats` - User counts (total, active, inactive)



## 📊 Summary
#### Total API Endpoints: 50+

- Authentication: 8 endpoints
- Exercises: 16 endpoints  
- Workouts: 16 endpoints
- Workout Exercises: 12 endpoints

All endpoints include:
- ✅ JWT Authentication (except auth endpoints)
- ✅ Comprehensive error handling
- ✅ OpenAPI/Swagger documentation
- ✅ Input validation
- ✅ Proper HTTP status codes
- ✅ Detailed logging