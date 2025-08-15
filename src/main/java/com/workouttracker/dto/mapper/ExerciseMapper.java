package com.workouttracker.dto.mapper;

import com.workouttracker.dto.response.ExerciseResponse;
import com.workouttracker.model.Exercise;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExerciseMapper {

    /**
     * Convert Exercise entity to ExerciseResponse DTO
     */
    public ExerciseResponse toExerciseResponse(Exercise exercise) {
        if (exercise == null) {
            return null;
        }

        ExerciseResponse response = new ExerciseResponse();
        response.setId(exercise.getId());
        response.setName(exercise.getName());
        response.setDescription(exercise.getDescription());
        response.setCategory(exercise.getCategory());
        response.setPrimaryMuscleGroup(exercise.getPrimaryMuscleGroup());
        response.setSecondaryMuscleGroups(exercise.getSecondaryMuscleGroups());
        response.setDifficultyLevel(exercise.getDifficultyLevel());
        response.setEquipmentRequired(exercise.getEquipmentRequired());
        response.setInstructions(exercise.getInstructions());
        response.setImageUrl(exercise.getImageUrl());
        response.setVideoUrl(exercise.getVideoUrl());
        response.setActive(exercise.isActive());
        response.setCreatedAt(exercise.getCreatedAt());
        response.setUpdatedAt(exercise.getUpdatedAt());

        return response;
    }

    /**
     * Convert list of Exercise entities to list of ExerciseResponse DTOs
     */
    public List<ExerciseResponse> toExerciseResponseList(List<Exercise> exercises) {
        if (exercises == null) {
            return null;
        }

        return exercises.stream()
                .map(this::toExerciseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a basic ExerciseResponse with essential info
     */
    public ExerciseResponse toBasicExerciseResponse(Exercise exercise) {
        if (exercise == null) {
            return null;
        }

        return new ExerciseResponse(
                exercise.getId(),
                exercise.getName(),
                exercise.getCategory(),
                exercise.getPrimaryMuscleGroup()
        );
    }

    /**
     * Convert list of exercises to basic response list
     */
    public List<ExerciseResponse> toBasicExerciseResponseList(List<Exercise> exercises) {
        if (exercises == null) {
            return null;
        }

        return exercises.stream()
                .map(this::toBasicExerciseResponse)
                .collect(Collectors.toList());
    }
}
