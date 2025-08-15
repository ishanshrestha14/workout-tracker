package com.workouttracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseResponse {
    
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private Integer sets;
    private Integer repetitions;
    private Double weightKg;
    private Double distanceKm;
    private Integer durationSeconds;
    private Integer caloriesBurned;
    private Integer restSeconds;
    private Integer exerciseOrder;
    private String notes;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ExerciseResponse exercise; // Full exercise details

    // Custom constructor for basic workout exercise info
    public WorkoutExerciseResponse(Long id, Long exerciseId, String exerciseName, Integer sets, Integer repetitions) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.repetitions = repetitions;
    }
}
