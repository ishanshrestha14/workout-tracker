package com.workouttracker.controller;

import com.workouttracker.dto.request.CreateWorkoutRequest;
import com.workouttracker.dto.response.WorkoutResponse;
import com.workouttracker.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workouts")
@Tag(name = "Workouts", description = "Workout management APIs")
public class WorkoutController {

    private static final Logger logger = LoggerFactory.getLogger(WorkoutController.class);

    @Autowired
    private WorkoutService workoutService;

    @Operation(summary = "Create a new workout", description = "Create a new workout with exercises")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Workout created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> createWorkout(@Valid @RequestBody CreateWorkoutRequest request) {
        try {
            logger.info("Creating workout: {}", request.getName());
            
            WorkoutResponse workoutResponse = workoutService.createWorkout(request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(workoutResponse);
            
        } catch (RuntimeException e) {
            logger.error("Failed to create workout: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Workout creation failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error creating workout: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get workout by ID", description = "Get a specific workout by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Workout not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{workoutId}")
    public ResponseEntity<?> getWorkoutById(@PathVariable Long workoutId) {
        try {
            WorkoutResponse workoutResponse = workoutService.getWorkoutById(workoutId);
            return ResponseEntity.ok(workoutResponse);
            
        } catch (RuntimeException e) {
            logger.error("Workout not found: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Workout not found");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving workout: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get all user workouts", description = "Get all workouts for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workouts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<?> getAllUserWorkouts() {
        try {
            List<WorkoutResponse> workouts = workoutService.getAllUserWorkouts();
            return ResponseEntity.ok(workouts);
            
        } catch (Exception e) {
            logger.error("Error retrieving user workouts: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get paginated workouts", description = "Get paginated workouts for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workouts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/paginated")
    public ResponseEntity<?> getUserWorkouts(Pageable pageable) {
        try {
            Page<WorkoutResponse> workouts = workoutService.getUserWorkouts(pageable);
            return ResponseEntity.ok(workouts);
            
        } catch (Exception e) {
            logger.error("Error retrieving paginated workouts: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get upcoming workouts", description = "Get upcoming scheduled workouts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upcoming workouts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingWorkouts() {
        try {
            List<WorkoutResponse> workouts = workoutService.getUpcomingWorkouts();
            return ResponseEntity.ok(workouts);
            
        } catch (Exception e) {
            logger.error("Error retrieving upcoming workouts: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get today's workouts", description = "Get workouts scheduled for today")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Today's workouts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/today")
    public ResponseEntity<?> getTodayWorkouts() {
        try {
            List<WorkoutResponse> workouts = workoutService.getTodayWorkouts();
            return ResponseEntity.ok(workouts);
            
        } catch (Exception e) {
            logger.error("Error retrieving today's workouts: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get workouts by date range", description = "Get workouts within a specific date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workouts retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/date-range")
    public ResponseEntity<?> getWorkoutsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<WorkoutResponse> workouts = workoutService.getWorkoutsInDateRange(startDate, endDate);
            return ResponseEntity.ok(workouts);
            
        } catch (Exception e) {
            logger.error("Error retrieving workouts in date range: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Search workouts", description = "Search workouts by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchWorkouts(@RequestParam String name) {
        try {
            List<WorkoutResponse> workouts = workoutService.searchWorkoutsByName(name);
            return ResponseEntity.ok(workouts);
            
        } catch (Exception e) {
            logger.error("Error searching workouts: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Update workout", description = "Update an existing workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Workout not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{workoutId}")
    public ResponseEntity<?> updateWorkout(@PathVariable Long workoutId, 
                                          @Valid @RequestBody CreateWorkoutRequest request) {
        try {
            WorkoutResponse workoutResponse = workoutService.updateWorkout(workoutId, request);
            return ResponseEntity.ok(workoutResponse);
            
        } catch (RuntimeException e) {
            logger.error("Failed to update workout: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Workout update failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error updating workout: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Start workout", description = "Start a scheduled workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout started successfully"),
            @ApiResponse(responseCode = "400", description = "Workout cannot be started"),
            @ApiResponse(responseCode = "404", description = "Workout not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{workoutId}/start")
    public ResponseEntity<?> startWorkout(@PathVariable Long workoutId) {
        try {
            WorkoutResponse workoutResponse = workoutService.startWorkout(workoutId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Workout started successfully");
            response.put("workout", workoutResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Failed to start workout: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Cannot start workout");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error starting workout: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Complete workout", description = "Mark a workout as completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout completed successfully"),
            @ApiResponse(responseCode = "400", description = "Workout cannot be completed"),
            @ApiResponse(responseCode = "404", description = "Workout not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{workoutId}/complete")
    public ResponseEntity<?> completeWorkout(@PathVariable Long workoutId,
                                            @RequestBody(required = false) Map<String, Object> completionData) {
        try {
            String notes = null;
            Integer totalCaloriesBurned = null;
            
            if (completionData != null) {
                notes = (String) completionData.get("notes");
                Object calories = completionData.get("totalCaloriesBurned");
                if (calories instanceof Number) {
                    totalCaloriesBurned = ((Number) calories).intValue();
                }
            }
            
            WorkoutResponse workoutResponse = workoutService.completeWorkout(workoutId, notes, totalCaloriesBurned);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Workout completed successfully");
            response.put("workout", workoutResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Failed to complete workout: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Cannot complete workout");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error completing workout: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Cancel workout", description = "Cancel a scheduled workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Workout not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{workoutId}/cancel")
    public ResponseEntity<?> cancelWorkout(@PathVariable Long workoutId) {
        try {
            WorkoutResponse workoutResponse = workoutService.cancelWorkout(workoutId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Workout cancelled successfully");
            response.put("workout", workoutResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Failed to cancel workout: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Cannot cancel workout");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error cancelling workout: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Delete workout", description = "Delete a workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Workout not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{workoutId}")
    public ResponseEntity<?> deleteWorkout(@PathVariable Long workoutId) {
        try {
            workoutService.deleteWorkout(workoutId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Workout deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Failed to delete workout: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Cannot delete workout");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error deleting workout: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get workout statistics", description = "Get workout statistics for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/stats")
    public ResponseEntity<?> getWorkoutStats() {
        try {
            WorkoutService.WorkoutStats stats = workoutService.getWorkoutStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error retrieving workout statistics: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get overdue workouts", description = "Get workouts that are overdue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdue workouts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueWorkouts() {
        try {
            List<WorkoutResponse> workouts = workoutService.getOverdueWorkouts();
            return ResponseEntity.ok(workouts);
            
        } catch (Exception e) {
            logger.error("Error retrieving overdue workouts: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
