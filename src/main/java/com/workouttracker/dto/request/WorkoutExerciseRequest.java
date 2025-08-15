package com.workouttracker.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseRequest {

    @NotNull(message = "Exercise ID is required")
    private Long exerciseId;

    @NotNull(message = "Sets are required")
    @Min(value = 1, message = "Sets must be at least 1")
    @Max(value = 50, message = "Sets must not exceed 50")
    private Integer sets;

    @NotNull(message = "Repetitions are required")
    @Min(value = 1, message = "Repetitions must be at least 1")
    @Max(value = 1000, message = "Repetitions must not exceed 1000")
    private Integer repetitions;

    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than 0")
    @DecimalMax(value = "1000.0", message = "Weight must not exceed 1000 kg")
    private Double weightKg;

    @DecimalMin(value = "0.0", inclusive = false, message = "Distance must be greater than 0")
    @DecimalMax(value = "1000.0", message = "Distance must not exceed 1000 km")
    private Double distanceKm;

    @Min(value = 1, message = "Duration must be at least 1 second")
    @Max(value = 86400, message = "Duration must not exceed 24 hours")
    private Integer durationSeconds;

    @Min(value = 0, message = "Calories burned cannot be negative")
    @Max(value = 5000, message = "Calories burned must not exceed 5000")
    private Integer caloriesBurned;

    @Min(value = 0, message = "Rest time cannot be negative")
    @Max(value = 3600, message = "Rest time must not exceed 1 hour")
    private Integer restSeconds;

    @Min(value = 1, message = "Exercise order must start from 1")
    private Integer exerciseOrder;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    // Custom constructor for basic exercise request
    public WorkoutExerciseRequest(Long exerciseId, Integer sets, Integer repetitions) {
        this.exerciseId = exerciseId;
        this.sets = sets;
        this.repetitions = repetitions;
    }
}
