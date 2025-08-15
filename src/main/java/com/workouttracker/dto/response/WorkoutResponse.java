package com.workouttracker.dto.response;

import com.workouttracker.model.Workout;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutResponse {
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime scheduledDateTime;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Workout.WorkoutStatus status;
    private Integer durationMinutes;
    private Integer totalCaloriesBurned;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String username;
    private List<WorkoutExerciseResponse> exercises;

    // Custom constructor for basic workout info
    public WorkoutResponse(Long id, String name, Workout.WorkoutStatus status, LocalDateTime scheduledDateTime) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.scheduledDateTime = scheduledDateTime;
    }
}
