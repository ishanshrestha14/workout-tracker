package com.workouttracker.repository;

import com.workouttracker.model.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    /**
     * Find exercises by category
     */
    List<Exercise> findByCategory(Exercise.Category category);

    /**
     * Find exercises by primary muscle group
     */
    List<Exercise> findByPrimaryMuscleGroup(Exercise.MuscleGroup primaryMuscleGroup);

    /**
     * Find exercises by difficulty level
     */
    List<Exercise> findByDifficultyLevel(Exercise.DifficultyLevel difficultyLevel);

    /**
     * Find active exercises only
     */
    List<Exercise> findByIsActiveTrue();

    /**
     * Find exercises by name containing (case insensitive)
     */
    @Query("SELECT e FROM Exercise e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) AND e.isActive = true")
    List<Exercise> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find exercises by category and active status
     */
    List<Exercise> findByCategoryAndIsActiveTrue(Exercise.Category category);

    /**
     * Find exercises by primary muscle group and active status
     */
    List<Exercise> findByPrimaryMuscleGroupAndIsActiveTrue(Exercise.MuscleGroup primaryMuscleGroup);

    /**
     * Find exercises by difficulty level and active status
     */
    List<Exercise> findByDifficultyLevelAndIsActiveTrue(Exercise.DifficultyLevel difficultyLevel);

    /**
     * Search exercises by multiple criteria
     */
    @Query("SELECT e FROM Exercise e WHERE " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:primaryMuscleGroup IS NULL OR e.primaryMuscleGroup = :primaryMuscleGroup) AND " +
           "(:difficultyLevel IS NULL OR e.difficultyLevel = :difficultyLevel) AND " +
           "e.isActive = true " +
           "ORDER BY e.name")
    List<Exercise> findExercisesByCriteria(
            @Param("category") Exercise.Category category,
            @Param("primaryMuscleGroup") Exercise.MuscleGroup primaryMuscleGroup,
            @Param("difficultyLevel") Exercise.DifficultyLevel difficultyLevel);

    /**
     * Find exercises that target specific muscle groups (primary or secondary)
     */
    @Query("SELECT DISTINCT e FROM Exercise e LEFT JOIN e.secondaryMuscleGroups smg WHERE " +
           "e.primaryMuscleGroup = :muscleGroup OR smg = :muscleGroup AND e.isActive = true")
    List<Exercise> findExercisesByMuscleGroup(@Param("muscleGroup") Exercise.MuscleGroup muscleGroup);

    /**
     * Get paginated exercises with filters
     */
    @Query("SELECT e FROM Exercise e WHERE " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:primaryMuscleGroup IS NULL OR e.primaryMuscleGroup = :primaryMuscleGroup) AND " +
           "(:searchTerm IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "e.isActive = true")
    Page<Exercise> findExercisesWithFilters(
            @Param("category") Exercise.Category category,
            @Param("primaryMuscleGroup") Exercise.MuscleGroup primaryMuscleGroup,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    /**
     * Find exercise by name (case insensitive)
     */
    @Query("SELECT e FROM Exercise e WHERE LOWER(e.name) = LOWER(:name)")
    Optional<Exercise> findByNameIgnoreCase(@Param("name") String name);

    /**
     * Get random exercises for workout suggestions
     */
    @Query(value = "SELECT * FROM exercises WHERE is_active = true ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Exercise> findRandomExercises(@Param("limit") int limit);

    /**
     * Count exercises by category
     */
    @Query("SELECT COUNT(e) FROM Exercise e WHERE e.category = :category AND e.isActive = true")
    long countByCategory(@Param("category") Exercise.Category category);
}
