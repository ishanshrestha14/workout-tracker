package com.workouttracker.controller;

import com.workouttracker.dto.response.WorkoutExerciseResponse;
import com.workouttracker.service.WorkoutExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workout-exercises")
@Tag(name = "Workout Exercises", description = "Individual workout exercise management APIs")
public class WorkoutExerciseController {

    private static final Logger logger = LoggerFactory.getLogger(WorkoutExerciseController.class);

    @Autowired
    private WorkoutExerciseService workoutExerciseService;

    @Operation(summary = "Get workout exercises by workout ID", description = "Get all exercises for a specific workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout exercises retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/workout/{workoutId}")
    public ResponseEntity<?> getWorkoutExercisesByWorkoutId(@PathVariable Long workoutId) {
        try {
            List<WorkoutExerciseResponse> exercises = workoutExerciseService.getWorkoutExercisesByWorkoutId(workoutId);
            return ResponseEntity.ok(exercises);
            
        } catch (RuntimeException e) {
            logger.error("Error retrieving workout exercises: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Access denied or workout not found");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving workout exercises: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get workout exercise by ID", description = "Get a specific workout exercise by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout exercise retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Workout exercise not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{workoutExerciseId}")
    public ResponseEntity<?> getWorkoutExerciseById(@PathVariable Long workoutExerciseId) {
        try {
            WorkoutExerciseResponse exercise = workoutExerciseService.getWorkoutExerciseById(workoutExerciseId);
            return ResponseEntity.ok(exercise);
            
        } catch (RuntimeException e) {
            logger.error("Workout exercise not found or access denied: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Workout exercise not found or access denied");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving workout exercise: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Mark exercise as completed", description = "Mark a workout exercise as completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise marked as completed successfully"),
            @ApiResponse(responseCode = "404", description = "Workout exercise not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{workoutExerciseId}/complete")
    public ResponseEntity<?> markExerciseAsCompleted(@PathVariable Long workoutExerciseId) {
        try {
            WorkoutExerciseResponse exercise = workoutExerciseService.markExerciseAsCompleted(workoutExerciseId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Exercise marked as completed successfully");
            response.put("exercise", exercise);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Failed to mark exercise as completed: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Cannot mark exercise as completed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error marking exercise as completed: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Update workout exercise", description = "Update workout exercise details (sets, reps, weight, notes)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise updated successfully"),
            @ApiResponse(responseCode = "404", description = "Workout exercise not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{workoutExerciseId}")
    public ResponseEntity<?> updateWorkoutExercise(@PathVariable Long workoutExerciseId,
                                                  @RequestBody Map<String, Object> updateData) {
        try {
            Integer sets = null;
            Integer repetitions = null;
            Double weight = null;
            String notes = null;
            
            if (updateData.containsKey("sets")) {
                Object setsObj = updateData.get("sets");
                if (setsObj instanceof Number) {
                    sets = ((Number) setsObj).intValue();
                }
            }
            
            if (updateData.containsKey("repetitions")) {
                Object repsObj = updateData.get("repetitions");
                if (repsObj instanceof Number) {
                    repetitions = ((Number) repsObj).intValue();
                }
            }
            
            if (updateData.containsKey("weight")) {
                Object weightObj = updateData.get("weight");
                if (weightObj instanceof Number) {
                    weight = ((Number) weightObj).doubleValue();
                }
            }
            
            if (updateData.containsKey("notes")) {
                notes = (String) updateData.get("notes");
            }
            
            WorkoutExerciseResponse exercise = workoutExerciseService.updateWorkoutExercise(
                    workoutExerciseId, sets, repetitions, weight, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Exercise updated successfully");
            response.put("exercise", exercise);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Failed to update workout exercise: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Cannot update exercise");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error updating workout exercise: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get user exercise history", description = "Get user's history with a specific exercise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise history retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/exercise/{exerciseId}/history")
    public ResponseEntity<?> getUserExerciseHistory(@PathVariable Long exerciseId) {
        try {
            List<WorkoutExerciseResponse> history = workoutExerciseService.getUserExerciseHistory(exerciseId);
            return ResponseEntity.ok(history);
            
        } catch (Exception e) {
            logger.error("Error retrieving exercise history: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get personal records", description = "Get personal records for a specific exercise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personal records retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/exercise/{exerciseId}/records")
    public ResponseEntity<?> getPersonalRecords(@PathVariable Long exerciseId) {
        try {
            List<WorkoutExerciseResponse> records = workoutExerciseService.getPersonalRecords(exerciseId);
            return ResponseEntity.ok(records);
            
        } catch (Exception e) {
            logger.error("Error retrieving personal records: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get exercise progress over time", description = "Get progress data for a specific exercise over time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progress data retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/exercise/{exerciseId}/progress")
    public ResponseEntity<?> getExerciseProgressOverTime(@PathVariable Long exerciseId) {
        try {
            List<WorkoutExerciseService.ExerciseProgressData> progressData = 
                workoutExerciseService.getExerciseProgressOverTime(exerciseId);
            return ResponseEntity.ok(progressData);
            
        } catch (Exception e) {
            logger.error("Error retrieving exercise progress: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get exercise statistics", description = "Get exercise statistics for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/stats")
    public ResponseEntity<?> getExerciseStatistics() {
        try {
            List<WorkoutExerciseService.ExerciseStatData> stats = workoutExerciseService.getExerciseStatistics();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error retrieving exercise statistics: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get total volume lifted", description = "Get total volume lifted in a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volume data retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/volume")
    public ResponseEntity<?> getTotalVolumeLiftedInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Double totalVolume = workoutExerciseService.getTotalVolumeLiftedInDateRange(startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("totalVolume", totalVolume);
            response.put("unit", "kg");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving volume data: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get muscle group frequency", description = "Get workout frequency by muscle group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Muscle group frequency retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/muscle-group-frequency")
    public ResponseEntity<?> getMuscleGroupFrequency() {
        try {
            List<WorkoutExerciseService.MuscleGroupFrequency> frequency = 
                workoutExerciseService.getMuscleGroupFrequency();
            return ResponseEntity.ok(frequency);
            
        } catch (Exception e) {
            logger.error("Error retrieving muscle group frequency: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get completed exercises count", description = "Get count of completed exercises for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Completed exercises count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/completed-count")
    public ResponseEntity<?> getCompletedExercisesCount() {
        try {
            long count = workoutExerciseService.getCompletedExercisesCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("completedExercisesCount", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving completed exercises count: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get all user workout exercises", description = "Get all workout exercises for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User workout exercises retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/all")
    public ResponseEntity<?> getAllUserWorkoutExercises() {
        try {
            List<WorkoutExerciseResponse> exercises = workoutExerciseService.getAllUserWorkoutExercises();
            return ResponseEntity.ok(exercises);
            
        } catch (Exception e) {
            logger.error("Error retrieving user workout exercises: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
