package com.workouttracker.service;

import com.workouttracker.dto.mapper.ExerciseMapper;
import com.workouttracker.dto.response.ExerciseResponse;
import com.workouttracker.model.Exercise;
import com.workouttracker.repository.ExerciseRepository;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExerciseService {

    // private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseMapper exerciseMapper;

    /**
     * Get all active exercises
     */
    public List<ExerciseResponse> getAllActiveExercises() {
        List<Exercise> exercises = exerciseRepository.findByIsActiveTrue();
        return exerciseMapper.toExerciseResponseList(exercises);
    }

    /**
     * Get exercise by ID
     */
    public ExerciseResponse getExerciseById(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + exerciseId));
        
        return exerciseMapper.toExerciseResponse(exercise);
    }

    /**
     * Get exercises by category
     */
    public List<ExerciseResponse> getExercisesByCategory(Exercise.Category category) {
        List<Exercise> exercises = exerciseRepository.findByCategoryAndIsActiveTrue(category);
        return exerciseMapper.toExerciseResponseList(exercises);
    }

    /**
     * Get exercises by primary muscle group
     */
    public List<ExerciseResponse> getExercisesByMuscleGroup(Exercise.MuscleGroup muscleGroup) {
        List<Exercise> exercises = exerciseRepository.findByPrimaryMuscleGroupAndIsActiveTrue(muscleGroup);
        return exerciseMapper.toExerciseResponseList(exercises);
    }

    /**
     * Get exercises by difficulty level
     */
    public List<ExerciseResponse> getExercisesByDifficultyLevel(Exercise.DifficultyLevel difficultyLevel) {
        List<Exercise> exercises = exerciseRepository.findByDifficultyLevelAndIsActiveTrue(difficultyLevel);
        return exerciseMapper.toExerciseResponseList(exercises);
    }

    /**
     * Search exercises by name
     */
    public List<ExerciseResponse> searchExercisesByName(String name) {
        List<Exercise> exercises = exerciseRepository.findByNameContainingIgnoreCase(name);
        return exerciseMapper.toExerciseResponseList(exercises);
    }

    /**
     * Get exercises with filters and pagination
     */
    public Page<ExerciseResponse> getExercisesWithFilters(
            Exercise.Category category,
            Exercise.MuscleGroup muscleGroup,
            String searchTerm,
            Pageable pageable) {
        
        Page<Exercise> exercises = exerciseRepository.findExercisesWithFilters(
                category, muscleGroup, searchTerm, pageable);
        
        return exercises.map(exerciseMapper::toExerciseResponse);
    }

    /**
     * Get exercises by multiple criteria
     */
    public List<ExerciseResponse> getExercisesByCriteria(
            Exercise.Category category,
            Exercise.MuscleGroup muscleGroup,
            Exercise.DifficultyLevel difficultyLevel) {
        
        List<Exercise> exercises = exerciseRepository.findExercisesByCriteria(
                category, muscleGroup, difficultyLevel);
        
        return exerciseMapper.toExerciseResponseList(exercises);
    }

    /**
     * Get exercises that target specific muscle groups (primary or secondary)
     */
    public List<ExerciseResponse> getExercisesByTargetMuscleGroup(Exercise.MuscleGroup muscleGroup) {
        List<Exercise> exercises = exerciseRepository.findExercisesByMuscleGroup(muscleGroup);
        return exerciseMapper.toExerciseResponseList(exercises);
    }

    /**
     * Get random exercises for workout suggestions
     */
    public List<ExerciseResponse> getRandomExercises(int limit) {
        List<Exercise> exercises = exerciseRepository.findRandomExercises(limit);
        return exerciseMapper.toExerciseResponseList(exercises);
    }

    /**
     * Get exercise count by category
     */
    public long getExerciseCountByCategory(Exercise.Category category) {
        return exerciseRepository.countByCategory(category);
    }

    /**
     * Get exercise by name (case insensitive)
     */
    public ExerciseResponse getExerciseByName(String name) {
        Exercise exercise = exerciseRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Exercise not found with name: " + name));
        
        return exerciseMapper.toExerciseResponse(exercise);
    }

    /**
     * Check if exercise exists by name
     */
    public boolean existsByName(String name) {
        return exerciseRepository.findByNameIgnoreCase(name).isPresent();
    }

    /**
     * Get all exercise categories
     */
    public Exercise.Category[] getAllCategories() {
        return Exercise.Category.values();
    }

    /**
     * Get all muscle groups
     */
    public Exercise.MuscleGroup[] getAllMuscleGroups() {
        return Exercise.MuscleGroup.values();
    }

    /**
     * Get all difficulty levels
     */
    public Exercise.DifficultyLevel[] getAllDifficultyLevels() {
        return Exercise.DifficultyLevel.values();
    }

    /**
     * Get basic exercise info (for workout creation dropdowns)
     */
    public List<ExerciseResponse> getBasicExerciseList() {
        List<Exercise> exercises = exerciseRepository.findByIsActiveTrue();
        return exerciseMapper.toBasicExerciseResponseList(exercises);
    }

    /**
     * Get exercise statistics
     */
    public ExerciseStats getExerciseStats() {
        long totalExercises = exerciseRepository.count();
        long activeExercises = exerciseRepository.findByIsActiveTrue().size();
        
        // Count by category
        long strengthCount = exerciseRepository.countByCategory(Exercise.Category.STRENGTH_TRAINING);
        long cardioCount = exerciseRepository.countByCategory(Exercise.Category.CARDIO);
        long flexibilityCount = exerciseRepository.countByCategory(Exercise.Category.FLEXIBILITY);
        
        return new ExerciseStats(totalExercises, activeExercises, strengthCount, cardioCount, flexibilityCount);
    }

    /**
     * Inner class for exercise statistics
     */
    public static class ExerciseStats {
        private long totalExercises;
        private long activeExercises;
        private long strengthExercises;
        private long cardioExercises;
        private long flexibilityExercises;

        public ExerciseStats(long totalExercises, long activeExercises, 
                           long strengthExercises, long cardioExercises, long flexibilityExercises) {
            this.totalExercises = totalExercises;
            this.activeExercises = activeExercises;
            this.strengthExercises = strengthExercises;
            this.cardioExercises = cardioExercises;
            this.flexibilityExercises = flexibilityExercises;
        }

        // Getters
        public long getTotalExercises() { return totalExercises; }
        public long getActiveExercises() { return activeExercises; }
        public long getStrengthExercises() { return strengthExercises; }
        public long getCardioExercises() { return cardioExercises; }
        public long getFlexibilityExercises() { return flexibilityExercises; }
    }
}
