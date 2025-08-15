package com.workouttracker.dto.response;

import com.workouttracker.model.Exercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {
    
    private Long id;
    private String name;
    private String description;
    private Exercise.Category category;
    private Exercise.MuscleGroup primaryMuscleGroup;
    private Set<Exercise.MuscleGroup> secondaryMuscleGroups;
    private Exercise.DifficultyLevel difficultyLevel;
    private String equipmentRequired;
    private String instructions;
    private String imageUrl;
    private String videoUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Custom constructor for basic exercise info
    public ExerciseResponse(Long id, String name, Exercise.Category category, Exercise.MuscleGroup primaryMuscleGroup) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.primaryMuscleGroup = primaryMuscleGroup;
        this.isActive = true;
    }
}
