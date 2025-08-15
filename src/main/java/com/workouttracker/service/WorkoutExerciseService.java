package com.workouttracker.service;

import com.workouttracker.dto.mapper.WorkoutMapper;
import com.workouttracker.dto.response.WorkoutExerciseResponse;
import com.workouttracker.model.User;
import com.workouttracker.model.WorkoutExercise;
import com.workouttracker.repository.WorkoutExerciseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class WorkoutExerciseService {

    private static final Logger logger = LoggerFactory.getLogger(WorkoutExerciseService.class);

    @Autowired
    private WorkoutExerciseRepository workoutExerciseRepository;

    @Autowired
    private WorkoutMapper workoutMapper;

    @Autowired
    private UserService userService;

    /**
     * Get workout exercises by workout ID
     */
    public List<WorkoutExerciseResponse> getWorkoutExercisesByWorkoutId(Long workoutId) {
        User currentUser = userService.getCurrentUserEntity();
        
        List<WorkoutExercise> workoutExercises = workoutExerciseRepository
                .findByWorkoutIdAndUserId(workoutId, currentUser.getId());
        
        return workoutExercises.stream()
                .map(workoutMapper::toWorkoutExerciseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get workout exercise by ID
     */
    public WorkoutExerciseResponse getWorkoutExerciseById(Long workoutExerciseId) {
        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("Workout exercise not found with id: " + workoutExerciseId));
        
        // Security check - ensure the workout exercise belongs to the current user
        User currentUser = userService.getCurrentUserEntity();
        if (!workoutExercise.getWorkout().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: Workout exercise does not belong to current user");
        }
        
        return workoutMapper.toWorkoutExerciseResponse(workoutExercise);
    }

    /**
     * Mark workout exercise as completed
     */
    @Transactional
    public WorkoutExerciseResponse markExerciseAsCompleted(Long workoutExerciseId) {
        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("Workout exercise not found with id: " + workoutExerciseId));
        
        // Security check
        User currentUser = userService.getCurrentUserEntity();
        if (!workoutExercise.getWorkout().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: Workout exercise does not belong to current user");
        }
        
        workoutExercise.markAsCompleted();
        WorkoutExercise updatedExercise = workoutExerciseRepository.save(workoutExercise);
        
        logger.info("Workout exercise marked as completed: {}", workoutExerciseId);
        return workoutMapper.toWorkoutExerciseResponse(updatedExercise);
    }

    /**
     * Update workout exercise details (sets, reps, weight, etc.)
     */
    @Transactional
    public WorkoutExerciseResponse updateWorkoutExercise(Long workoutExerciseId, 
                                                       Integer sets, Integer repetitions, 
                                                       Double weight, String notes) {
        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("Workout exercise not found with id: " + workoutExerciseId));
        
        // Security check
        User currentUser = userService.getCurrentUserEntity();
        if (!workoutExercise.getWorkout().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: Workout exercise does not belong to current user");
        }
        
        // Update fields if provided
        if (sets != null) workoutExercise.setSets(sets);
        if (repetitions != null) workoutExercise.setRepetitions(repetitions);
        if (weight != null) workoutExercise.setWeightKg(weight);
        if (notes != null) workoutExercise.setNotes(notes);
        
        WorkoutExercise updatedExercise = workoutExerciseRepository.save(workoutExercise);
        
        logger.info("Workout exercise updated: {}", workoutExerciseId);
        return workoutMapper.toWorkoutExerciseResponse(updatedExercise);
    }

    /**
     * Get all workout exercises for current user
     */
    public List<WorkoutExerciseResponse> getAllUserWorkoutExercises() {
        User currentUser = userService.getCurrentUserEntity();
        
        List<WorkoutExercise> workoutExercises = workoutExerciseRepository.findByUserId(currentUser.getId());
        
        return workoutExercises.stream()
                .map(workoutMapper::toWorkoutExerciseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get workout exercises for specific exercise (user's history with that exercise)
     */
    public List<WorkoutExerciseResponse> getUserExerciseHistory(Long exerciseId) {
        User currentUser = userService.getCurrentUserEntity();
        
        List<WorkoutExercise> workoutExercises = workoutExerciseRepository
                .findByUserIdAndExerciseId(currentUser.getId(), exerciseId);
        
        return workoutExercises.stream()
                .map(workoutMapper::toWorkoutExerciseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get personal records for user by exercise
     */
    public List<WorkoutExerciseResponse> getPersonalRecords(Long exerciseId) {
        User currentUser = userService.getCurrentUserEntity();
        
        List<WorkoutExercise> personalRecords = workoutExerciseRepository
                .findPersonalRecordsByExercise(currentUser.getId(), exerciseId);
        
        return personalRecords.stream()
                .map(workoutMapper::toWorkoutExerciseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get exercise progress over time for specific exercise
     */
    public List<ExerciseProgressData> getExerciseProgressOverTime(Long exerciseId) {
        User currentUser = userService.getCurrentUserEntity();
        
        List<Object[]> progressData = workoutExerciseRepository
                .getExerciseProgressOverTime(currentUser.getId(), exerciseId);
        
        return progressData.stream()
                .map(data -> new ExerciseProgressData(
                        (LocalDateTime) data[0], // completedAt
                        (Integer) data[1],       // sets
                        (Integer) data[2],       // repetitions
                        (Double) data[3]         // weightKg
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get exercise statistics for user
     */
    public List<ExerciseStatData> getExerciseStatistics() {
        User currentUser = userService.getCurrentUserEntity();
        
        List<Object[]> statsData = workoutExerciseRepository.getExerciseStatsByUserId(currentUser.getId());
        
        return statsData.stream()
                .map(data -> new ExerciseStatData(
                        (String) data[0],  // exerciseName
                        ((Number) data[1]).longValue(),    // count
                        ((Number) data[2]).doubleValue(),  // avgSets
                        ((Number) data[3]).doubleValue(),  // avgReps
                        data[4] != null ? ((Number) data[4]).doubleValue() : 0.0 // avgWeight
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get total volume lifted in date range
     */
    public Double getTotalVolumeLiftedInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = userService.getCurrentUserEntity();
        
        Double totalVolume = workoutExerciseRepository.getTotalVolumeLiftedInDateRange(
                currentUser.getId(), startDate, endDate);
        
        return totalVolume != null ? totalVolume : 0.0;
    }

    /**
     * Get muscle group workout frequency
     */
    public List<MuscleGroupFrequency> getMuscleGroupFrequency() {
        User currentUser = userService.getCurrentUserEntity();
        
        List<Object[]> frequencyData = workoutExerciseRepository.getMuscleGroupFrequencyByUserId(currentUser.getId());
        
        return frequencyData.stream()
                .map(data -> new MuscleGroupFrequency(
                        data[0].toString(), // muscleGroup
                        ((Number) data[1]).longValue() // count
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get count of completed exercises for user
     */
    public long getCompletedExercisesCount() {
        User currentUser = userService.getCurrentUserEntity();
        return workoutExerciseRepository.countCompletedExercisesByUserId(currentUser.getId());
    }

    /**
     * Inner classes for data transfer
     */
    public static class ExerciseProgressData {
        private LocalDateTime date;
        private Integer sets;
        private Integer repetitions;
        private Double weightKg;

        public ExerciseProgressData(LocalDateTime date, Integer sets, Integer repetitions, Double weightKg) {
            this.date = date;
            this.sets = sets;
            this.repetitions = repetitions;
            this.weightKg = weightKg;
        }

        // Getters
        public LocalDateTime getDate() { return date; }
        public Integer getSets() { return sets; }
        public Integer getRepetitions() { return repetitions; }
        public Double getWeightKg() { return weightKg; }
    }

    public static class ExerciseStatData {
        private String exerciseName;
        private long count;
        private double avgSets;
        private double avgRepetitions;
        private double avgWeight;

        public ExerciseStatData(String exerciseName, long count, double avgSets, double avgRepetitions, double avgWeight) {
            this.exerciseName = exerciseName;
            this.count = count;
            this.avgSets = avgSets;
            this.avgRepetitions = avgRepetitions;
            this.avgWeight = avgWeight;
        }

        // Getters
        public String getExerciseName() { return exerciseName; }
        public long getCount() { return count; }
        public double getAvgSets() { return avgSets; }
        public double getAvgRepetitions() { return avgRepetitions; }
        public double getAvgWeight() { return avgWeight; }
    }

    public static class MuscleGroupFrequency {
        private String muscleGroup;
        private long count;

        public MuscleGroupFrequency(String muscleGroup, long count) {
            this.muscleGroup = muscleGroup;
            this.count = count;
        }

        // Getters
        public String getMuscleGroup() { return muscleGroup; }
        public long getCount() { return count; }
    }
}
