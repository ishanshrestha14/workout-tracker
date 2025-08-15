package com.workouttracker.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkoutRequest {

    @NotBlank(message = "Workout name is required")
    @Size(max = 100, message = "Workout name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private LocalDateTime scheduledDateTime;

    @NotEmpty(message = "At least one exercise is required")
    @Valid
    private List<WorkoutExerciseRequest> exercises;
}
