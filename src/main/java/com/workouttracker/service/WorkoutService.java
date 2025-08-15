package com.workouttracker.service;

import com.workouttracker.dto.mapper.WorkoutMapper;
import com.workouttracker.dto.request.CreateWorkoutRequest;
import com.workouttracker.dto.request.WorkoutExerciseRequest;
import com.workouttracker.dto.response.WorkoutResponse;
import com.workouttracker.model.Exercise;
import com.workouttracker.model.User;
import com.workouttracker.model.Workout;
import com.workouttracker.model.WorkoutExercise;
import com.workouttracker.repository.ExerciseRepository;
import com.workouttracker.repository.WorkoutExerciseRepository;
import com.workouttracker.repository.WorkoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class WorkoutService {

    private static final Logger logger = LoggerFactory.getLogger(WorkoutService.class);

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private WorkoutExerciseRepository workoutExerciseRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutMapper workoutMapper;

    @Autowired
    private UserService userService;

    /**
     * Create a new workout
     */
    @Transactional
    public WorkoutResponse createWorkout(CreateWorkoutRequest request) {
        logger.info("Creating workout: {}", request.getName());

        User currentUser = userService.getCurrentUserEntity();
        
        // Create workout
        Workout workout = workoutMapper.toWorkout(request, currentUser);
        if (workout.getScheduledDateTime() == null) {
            workout.setScheduledDateTime(LocalDateTime.now());
        }
        
        Workout savedWorkout = workoutRepository.save(workout);

        // Create workout exercises
        AtomicInteger order = new AtomicInteger(1);
        Set<WorkoutExercise> workoutExercises = request.getExercises().stream()
                .map(exerciseRequest -> createWorkoutExercise(exerciseRequest, savedWorkout, order.getAndIncrement()))
                .collect(Collectors.toSet());

        savedWorkout.setWorkoutExercises(workoutExercises);
        
        logger.info("Workout created successfully with ID: {}", savedWorkout.getId());
        return workoutMapper.toWorkoutResponse(savedWorkout);
    }

    /**
     * Get workout by ID for current user
     */
    public WorkoutResponse getWorkoutById(Long workoutId) {
        User currentUser = userService.getCurrentUserEntity();
        
        Workout workout = workoutRepository.findByIdAndUserId(workoutId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));
        
        return workoutMapper.toWorkoutResponse(workout);
    }

    /**
     * Get all workouts for current user
     */
    public List<WorkoutResponse> getAllUserWorkouts() {
        User currentUser = userService.getCurrentUserEntity();
        List<Workout> workouts = workoutRepository.findByUserIdOrderByScheduledDateTime(currentUser.getId());
        return workoutMapper.toWorkoutResponseList(workouts);
    }

    /**
     * Get paginated workouts for current user
     */
    public Page<WorkoutResponse> getUserWorkouts(Pageable pageable) {
        User currentUser = userService.getCurrentUserEntity();
        Page<Workout> workouts = workoutRepository.findByUserIdOrderByScheduledDateTimeDesc(currentUser.getId(), pageable);
        return workouts.map(workoutMapper::toWorkoutResponse);
    }

    /**
     * Get workouts by status for current user
     */
    public List<WorkoutResponse> getWorkoutsByStatus(Workout.WorkoutStatus status) {
        User currentUser = userService.getCurrentUserEntity();
        List<Workout> workouts = workoutRepository.findByUserIdAndStatus(currentUser.getId(), status);
        return workoutMapper.toWorkoutResponseList(workouts);
    }

    /**
     * Get upcoming workouts for current user
     */
    public List<WorkoutResponse> getUpcomingWorkouts() {
        User currentUser = userService.getCurrentUserEntity();
        List<Workout> workouts = workoutRepository.findUpcomingWorkouts(
                currentUser.getId(), 
                LocalDateTime.now(), 
                Workout.WorkoutStatus.SCHEDULED
        );
        return workoutMapper.toWorkoutResponseList(workouts);
    }

    /**
     * Get today's workouts for current user
     */
    public List<WorkoutResponse> getTodayWorkouts() {
        User currentUser = userService.getCurrentUserEntity();
        List<Workout> workouts = workoutRepository.findTodayWorkouts(currentUser.getId(), LocalDateTime.now());
        return workoutMapper.toWorkoutResponseList(workouts);
    }

    /**
     * Get workouts in date range
     */
    public List<WorkoutResponse> getWorkoutsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = userService.getCurrentUserEntity();
        List<Workout> workouts = workoutRepository.findWorkoutsByUserAndDateRange(
                currentUser.getId(), startDate, endDate);
        return workoutMapper.toWorkoutResponseList(workouts);
    }

    /**
     * Get completed workouts in date range
     */
    public List<WorkoutResponse> getCompletedWorkoutsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = userService.getCurrentUserEntity();
        List<Workout> workouts = workoutRepository.findCompletedWorkoutsBetween(
                currentUser.getId(), startDate, endDate);
        return workoutMapper.toWorkoutResponseList(workouts);
    }

    /**
     * Search workouts by name
     */
    public List<WorkoutResponse> searchWorkoutsByName(String name) {
        User currentUser = userService.getCurrentUserEntity();
        List<Workout> workouts = workoutRepository.findByUserIdAndNameContainingIgnoreCase(
                currentUser.getId(), name);
        return workoutMapper.toWorkoutResponseList(workouts);
    }

    /**
     * Update workout
     */
    @Transactional
    public WorkoutResponse updateWorkout(Long workoutId, CreateWorkoutRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        
        Workout workout = workoutRepository.findByIdAndUserId(workoutId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        // Update basic workout info
        workout.setName(request.getName());
        workout.setDescription(request.getDescription());
        if (request.getScheduledDateTime() != null) {
            workout.setScheduledDateTime(request.getScheduledDateTime());
        }

        // Update exercises if provided
        if (request.getExercises() != null && !request.getExercises().isEmpty()) {
            // Remove existing exercises
            workoutExerciseRepository.deleteByWorkoutId(workoutId);
            
            // Add new exercises
            AtomicInteger order = new AtomicInteger(1);
            Set<WorkoutExercise> newExercises = request.getExercises().stream()
                    .map(exerciseRequest -> createWorkoutExercise(exerciseRequest, workout, order.getAndIncrement()))
                    .collect(Collectors.toSet());
            
            workout.setWorkoutExercises(newExercises);
        }

        Workout updatedWorkout = workoutRepository.save(workout);
        logger.info("Workout updated successfully: {}", updatedWorkout.getId());
        
        return workoutMapper.toWorkoutResponse(updatedWorkout);
    }

    /**
     * Start workout
     */
    @Transactional
    public WorkoutResponse startWorkout(Long workoutId) {
        User currentUser = userService.getCurrentUserEntity();
        
        Workout workout = workoutRepository.findByIdAndUserId(workoutId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        if (workout.getStatus() != Workout.WorkoutStatus.SCHEDULED) {
            throw new RuntimeException("Workout cannot be started. Current status: " + workout.getStatus());
        }

        workout.startWorkout();
        Workout startedWorkout = workoutRepository.save(workout);
        
        logger.info("Workout started: {}", startedWorkout.getId());
        return workoutMapper.toWorkoutResponse(startedWorkout);
    }

    /**
     * Complete workout
     */
    @Transactional
    public WorkoutResponse completeWorkout(Long workoutId, String notes, Integer totalCaloriesBurned) {
        User currentUser = userService.getCurrentUserEntity();
        
        Workout workout = workoutRepository.findByIdAndUserId(workoutId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        if (workout.getStatus() != Workout.WorkoutStatus.IN_PROGRESS) {
            throw new RuntimeException("Workout cannot be completed. Current status: " + workout.getStatus());
        }

        workout.completeWorkout();
        workout.setNotes(notes);
        if (totalCaloriesBurned != null) {
            workout.setTotalCaloriesBurned(totalCaloriesBurned);
        }

        Workout completedWorkout = workoutRepository.save(workout);
        
        logger.info("Workout completed: {}", completedWorkout.getId());
        return workoutMapper.toWorkoutResponse(completedWorkout);
    }

    /**
     * Cancel workout
     */
    @Transactional
    public WorkoutResponse cancelWorkout(Long workoutId) {
        User currentUser = userService.getCurrentUserEntity();
        
        Workout workout = workoutRepository.findByIdAndUserId(workoutId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        workout.cancelWorkout();
        Workout cancelledWorkout = workoutRepository.save(workout);
        
        logger.info("Workout cancelled: {}", cancelledWorkout.getId());
        return workoutMapper.toWorkoutResponse(cancelledWorkout);
    }

    /**
     * Delete workout
     */
    @Transactional
    public void deleteWorkout(Long workoutId) {
        User currentUser = userService.getCurrentUserEntity();
        
        Workout workout = workoutRepository.findByIdAndUserId(workoutId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        workoutRepository.delete(workout);
        logger.info("Workout deleted: {}", workoutId);
    }

    /**
     * Get workout statistics for current user
     */
    public WorkoutStats getWorkoutStats() {
        User currentUser = userService.getCurrentUserEntity();
        
        Object[] stats = workoutRepository.getWorkoutStatsByUserId(currentUser.getId());
        
        long totalWorkouts = stats[0] != null ? ((Number) stats[0]).longValue() : 0;
        double avgDuration = stats[1] != null ? ((Number) stats[1]).doubleValue() : 0;
        long totalCalories = stats[2] != null ? ((Number) stats[2]).longValue() : 0;
        
        long scheduledCount = workoutRepository.countByUserIdAndStatus(currentUser.getId(), Workout.WorkoutStatus.SCHEDULED);
        long completedCount = workoutRepository.countByUserIdAndStatus(currentUser.getId(), Workout.WorkoutStatus.COMPLETED);
        long inProgressCount = workoutRepository.countByUserIdAndStatus(currentUser.getId(), Workout.WorkoutStatus.IN_PROGRESS);
        
        return new WorkoutStats(totalWorkouts, completedCount, scheduledCount, inProgressCount, avgDuration, totalCalories);
    }

    /**
     * Get overdue workouts
     */
    public List<WorkoutResponse> getOverdueWorkouts() {
        User currentUser = userService.getCurrentUserEntity();
        List<Workout> workouts = workoutRepository.findOverdueWorkouts(currentUser.getId(), LocalDateTime.now());
        return workoutMapper.toWorkoutResponseList(workouts);
    }

    /**
     * Helper method to create WorkoutExercise
     */
    private WorkoutExercise createWorkoutExercise(WorkoutExerciseRequest request, Workout workout, int order) {
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + request.getExerciseId()));

        WorkoutExercise workoutExercise = workoutMapper.toWorkoutExercise(request, workout);
        workoutExercise.setExercise(exercise);
        
        if (workoutExercise.getExerciseOrder() == null) {
            workoutExercise.setExerciseOrder(order);
        }

        return workoutExerciseRepository.save(workoutExercise);
    }

    /**
     * Inner class for workout statistics
     */
    public static class WorkoutStats {
        private long totalWorkouts;
        private long completedWorkouts;
        private long scheduledWorkouts;
        private long inProgressWorkouts;
        private double avgDurationMinutes;
        private long totalCaloriesBurned;

        public WorkoutStats(long totalWorkouts, long completedWorkouts, long scheduledWorkouts, 
                          long inProgressWorkouts, double avgDurationMinutes, long totalCaloriesBurned) {
            this.totalWorkouts = totalWorkouts;
            this.completedWorkouts = completedWorkouts;
            this.scheduledWorkouts = scheduledWorkouts;
            this.inProgressWorkouts = inProgressWorkouts;
            this.avgDurationMinutes = avgDurationMinutes;
            this.totalCaloriesBurned = totalCaloriesBurned;
        }

        // Getters
        public long getTotalWorkouts() { return totalWorkouts; }
        public long getCompletedWorkouts() { return completedWorkouts; }
        public long getScheduledWorkouts() { return scheduledWorkouts; }
        public long getInProgressWorkouts() { return inProgressWorkouts; }
        public double getAvgDurationMinutes() { return avgDurationMinutes; }
        public long getTotalCaloriesBurned() { return totalCaloriesBurned; }
    }
}
