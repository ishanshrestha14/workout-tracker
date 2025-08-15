package com.workouttracker.controller;

import com.workouttracker.dto.response.ExerciseResponse;
import com.workouttracker.model.Exercise;
import com.workouttracker.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exercises")
@Tag(name = "Exercises", description = "Exercise management APIs")
public class ExerciseController {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    @Autowired
    private ExerciseService exerciseService;

    @Operation(summary = "Get all active exercises", description = "Get all active exercises in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<?> getAllActiveExercises() {
        try {
            List<ExerciseResponse> exercises = exerciseService.getAllActiveExercises();
            return ResponseEntity.ok(exercises);
            
        } catch (Exception e) {
            logger.error("Error retrieving exercises: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get exercise by ID", description = "Get a specific exercise by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Exercise not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{exerciseId}")
    public ResponseEntity<?> getExerciseById(@PathVariable Long exerciseId) {
        try {
            ExerciseResponse exerciseResponse = exerciseService.getExerciseById(exerciseId);
            return ResponseEntity.ok(exerciseResponse);
            
        } catch (RuntimeException e) {
            logger.error("Exercise not found: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Exercise not found");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving exercise: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get exercises by category", description = "Get exercises filtered by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getExercisesByCategory(@PathVariable String category) {
        try {
            Exercise.Category exerciseCategory = Exercise.Category.valueOf(category.toUpperCase());
            List<ExerciseResponse> exercises = exerciseService.getExercisesByCategory(exerciseCategory);
            return ResponseEntity.ok(exercises);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid category: {}", category);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid category");
            errorResponse.put("message", "Category '" + category + "' is not valid");
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error retrieving exercises by category: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get exercises by muscle group", description = "Get exercises filtered by muscle group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid muscle group"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/muscle-group/{muscleGroup}")
    public ResponseEntity<?> getExercisesByMuscleGroup(@PathVariable String muscleGroup) {
        try {
            Exercise.MuscleGroup exerciseMuscleGroup = Exercise.MuscleGroup.valueOf(muscleGroup.toUpperCase());
            List<ExerciseResponse> exercises = exerciseService.getExercisesByMuscleGroup(exerciseMuscleGroup);
            return ResponseEntity.ok(exercises);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid muscle group: {}", muscleGroup);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid muscle group");
            errorResponse.put("message", "Muscle group '" + muscleGroup + "' is not valid");
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error retrieving exercises by muscle group: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get exercises by difficulty level", description = "Get exercises filtered by difficulty level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid difficulty level"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/difficulty/{difficultyLevel}")
    public ResponseEntity<?> getExercisesByDifficultyLevel(@PathVariable String difficultyLevel) {
        try {
            Exercise.DifficultyLevel exerciseDifficulty = Exercise.DifficultyLevel.valueOf(difficultyLevel.toUpperCase());
            List<ExerciseResponse> exercises = exerciseService.getExercisesByDifficultyLevel(exerciseDifficulty);
            return ResponseEntity.ok(exercises);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid difficulty level: {}", difficultyLevel);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid difficulty level");
            errorResponse.put("message", "Difficulty level '" + difficultyLevel + "' is not valid");
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error retrieving exercises by difficulty: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Search exercises by name", description = "Search exercises by name (partial match)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchExercisesByName(@RequestParam String name) {
        try {
            List<ExerciseResponse> exercises = exerciseService.searchExercisesByName(name);
            return ResponseEntity.ok(exercises);
            
        } catch (Exception e) {
            logger.error("Error searching exercises: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get exercises with filters and pagination", description = "Get exercises with optional filters and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtered exercises retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/filtered")
    public ResponseEntity<?> getExercisesWithFilters(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String muscleGroup,
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {
        try {
            Exercise.Category exerciseCategory = null;
            Exercise.MuscleGroup exerciseMuscleGroup = null;
            
            if (category != null && !category.trim().isEmpty()) {
                exerciseCategory = Exercise.Category.valueOf(category.toUpperCase());
            }
            
            if (muscleGroup != null && !muscleGroup.trim().isEmpty()) {
                exerciseMuscleGroup = Exercise.MuscleGroup.valueOf(muscleGroup.toUpperCase());
            }
            
            Page<ExerciseResponse> exercises = exerciseService.getExercisesWithFilters(
                    exerciseCategory, exerciseMuscleGroup, searchTerm, pageable);
            
            return ResponseEntity.ok(exercises);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid filter parameter: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid filter parameter");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error retrieving filtered exercises: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get random exercises", description = "Get random exercises for workout suggestions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Random exercises retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid limit parameter"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/random")
    public ResponseEntity<?> getRandomExercises(@RequestParam(defaultValue = "10") int limit) {
        try {
            if (limit <= 0 || limit > 50) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid limit");
                errorResponse.put("message", "Limit must be between 1 and 50");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            List<ExerciseResponse> exercises = exerciseService.getRandomExercises(limit);
            return ResponseEntity.ok(exercises);
            
        } catch (Exception e) {
            logger.error("Error retrieving random exercises: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get all exercise categories", description = "Get list of all available exercise categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            Exercise.Category[] categories = exerciseService.getAllCategories();
            return ResponseEntity.ok(categories);
            
        } catch (Exception e) {
            logger.error("Error retrieving categories: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get all muscle groups", description = "Get list of all available muscle groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Muscle groups retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/muscle-groups")
    public ResponseEntity<?> getAllMuscleGroups() {
        try {
            Exercise.MuscleGroup[] muscleGroups = exerciseService.getAllMuscleGroups();
            return ResponseEntity.ok(muscleGroups);
            
        } catch (Exception e) {
            logger.error("Error retrieving muscle groups: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get all difficulty levels", description = "Get list of all available difficulty levels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Difficulty levels retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/difficulty-levels")
    public ResponseEntity<?> getAllDifficultyLevels() {
        try {
            Exercise.DifficultyLevel[] difficultyLevels = exerciseService.getAllDifficultyLevels();
            return ResponseEntity.ok(difficultyLevels);
            
        } catch (Exception e) {
            logger.error("Error retrieving difficulty levels: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get basic exercise list", description = "Get basic exercise information for dropdowns")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Basic exercise list retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/basic")
    public ResponseEntity<?> getBasicExerciseList() {
        try {
            List<ExerciseResponse> exercises = exerciseService.getBasicExerciseList();
            return ResponseEntity.ok(exercises);
            
        } catch (Exception e) {
            logger.error("Error retrieving basic exercise list: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get exercise statistics", description = "Get exercise statistics and counts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise statistics retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/stats")
    public ResponseEntity<?> getExerciseStats() {
        try {
            ExerciseService.ExerciseStats stats = exerciseService.getExerciseStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error retrieving exercise statistics: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get exercises targeting muscle group", description = "Get exercises that target a specific muscle group (primary or secondary)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid muscle group"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/targeting/{muscleGroup}")
    public ResponseEntity<?> getExercisesByTargetMuscleGroup(@PathVariable String muscleGroup) {
        try {
            Exercise.MuscleGroup targetMuscleGroup = Exercise.MuscleGroup.valueOf(muscleGroup.toUpperCase());
            List<ExerciseResponse> exercises = exerciseService.getExercisesByTargetMuscleGroup(targetMuscleGroup);
            return ResponseEntity.ok(exercises);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid muscle group: {}", muscleGroup);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid muscle group");
            errorResponse.put("message", "Muscle group '" + muscleGroup + "' is not valid");
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error retrieving exercises by target muscle group: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get exercise count by category", description = "Get count of exercises in a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise count retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count/category/{category}")
    public ResponseEntity<?> getExerciseCountByCategory(@PathVariable String category) {
        try {
            Exercise.Category exerciseCategory = Exercise.Category.valueOf(category.toUpperCase());
            long count = exerciseService.getExerciseCountByCategory(exerciseCategory);
            
            Map<String, Object> response = new HashMap<>();
            response.put("category", exerciseCategory);
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid category: {}", category);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid category");
            errorResponse.put("message", "Category '" + category + "' is not valid");
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error retrieving exercise count: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
