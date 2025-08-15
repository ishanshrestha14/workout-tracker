package com.workouttracker.dto.mapper;

import com.workouttracker.dto.request.CreateWorkoutRequest;
import com.workouttracker.dto.request.WorkoutExerciseRequest;
import com.workouttracker.dto.response.WorkoutResponse;
import com.workouttracker.dto.response.WorkoutExerciseResponse;
import com.workouttracker.model.User;
import com.workouttracker.model.Workout;
import com.workouttracker.model.WorkoutExercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkoutMapper {

    @Autowired
    private ExerciseMapper exerciseMapper;

    /**
     * Convert Workout entity to WorkoutResponse DTO
     */
    public WorkoutResponse toWorkoutResponse(Workout workout) {
        if (workout == null) {
            return null;
        }

        WorkoutResponse response = new WorkoutResponse();
        response.setId(workout.getId());
        response.setName(workout.getName());
        response.setDescription(workout.getDescription());
        response.setScheduledDateTime(workout.getScheduledDateTime());
        response.setStartedAt(workout.getStartedAt());
        response.setCompletedAt(workout.getCompletedAt());
        response.setStatus(workout.getStatus());
        response.setDurationMinutes(workout.getDurationMinutes());
        response.setTotalCaloriesBurned(workout.getTotalCaloriesBurned());
        response.setNotes(workout.getNotes());
        response.setCreatedAt(workout.getCreatedAt());
        response.setUpdatedAt(workout.getUpdatedAt());
        
        if (workout.getUser() != null) {
            response.setUserId(workout.getUser().getId());
            response.setUsername(workout.getUser().getUsername());
        }

        if (workout.getWorkoutExercises() != null && !workout.getWorkoutExercises().isEmpty()) {
            List<WorkoutExerciseResponse> exerciseResponses = workout.getWorkoutExercises()
                    .stream()
                    .map(this::toWorkoutExerciseResponse)
                    .sorted((a, b) -> Integer.compare(
                            a.getExerciseOrder() != null ? a.getExerciseOrder() : Integer.MAX_VALUE,
                            b.getExerciseOrder() != null ? b.getExerciseOrder() : Integer.MAX_VALUE))
                    .collect(Collectors.toList());
            response.setExercises(exerciseResponses);
        }

        return response;
    }

    /**
     * Convert CreateWorkoutRequest DTO to Workout entity
     */
    public Workout toWorkout(CreateWorkoutRequest request, User user) {
        if (request == null) {
            return null;
        }

        Workout workout = new Workout();
        workout.setName(request.getName());
        workout.setDescription(request.getDescription());
        workout.setScheduledDateTime(request.getScheduledDateTime());
        workout.setUser(user);

        return workout;
    }

    /**
     * Convert WorkoutExercise entity to WorkoutExerciseResponse DTO
     */
    public WorkoutExerciseResponse toWorkoutExerciseResponse(WorkoutExercise workoutExercise) {
        if (workoutExercise == null) {
            return null;
        }

        WorkoutExerciseResponse response = new WorkoutExerciseResponse();
        response.setId(workoutExercise.getId());
        response.setSets(workoutExercise.getSets());
        response.setRepetitions(workoutExercise.getRepetitions());
        response.setWeightKg(workoutExercise.getWeightKg());
        response.setDistanceKm(workoutExercise.getDistanceKm());
        response.setDurationSeconds(workoutExercise.getDurationSeconds());
        response.setCaloriesBurned(workoutExercise.getCaloriesBurned());
        response.setRestSeconds(workoutExercise.getRestSeconds());
        response.setExerciseOrder(workoutExercise.getExerciseOrder());
        response.setNotes(workoutExercise.getNotes());
        response.setCompleted(workoutExercise.isCompleted());
        response.setCreatedAt(workoutExercise.getCreatedAt());
        response.setUpdatedAt(workoutExercise.getUpdatedAt());

        if (workoutExercise.getExercise() != null) {
            response.setExerciseId(workoutExercise.getExercise().getId());
            response.setExerciseName(workoutExercise.getExercise().getName());
            response.setExercise(exerciseMapper.toExerciseResponse(workoutExercise.getExercise()));
        }

        return response;
    }

    /**
     * Convert WorkoutExerciseRequest DTO to WorkoutExercise entity
     */
    public WorkoutExercise toWorkoutExercise(WorkoutExerciseRequest request, Workout workout) {
        if (request == null) {
            return null;
        }

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setSets(request.getSets());
        workoutExercise.setRepetitions(request.getRepetitions());
        workoutExercise.setWeightKg(request.getWeightKg());
        workoutExercise.setDistanceKm(request.getDistanceKm());
        workoutExercise.setDurationSeconds(request.getDurationSeconds());
        workoutExercise.setCaloriesBurned(request.getCaloriesBurned());
        workoutExercise.setRestSeconds(request.getRestSeconds());
        workoutExercise.setExerciseOrder(request.getExerciseOrder());
        workoutExercise.setNotes(request.getNotes());
        workoutExercise.setWorkout(workout);

        return workoutExercise;
    }

    /**
     * Convert list of Workout entities to list of WorkoutResponse DTOs
     */
    public List<WorkoutResponse> toWorkoutResponseList(List<Workout> workouts) {
        if (workouts == null) {
            return null;
        }

        return workouts.stream()
                .map(this::toWorkoutResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a basic WorkoutResponse with essential info (without exercises)
     */
    public WorkoutResponse toBasicWorkoutResponse(Workout workout) {
        if (workout == null) {
            return null;
        }

        return new WorkoutResponse(
                workout.getId(),
                workout.getName(),
                workout.getStatus(),
                workout.getScheduledDateTime()
        );
    }
}
