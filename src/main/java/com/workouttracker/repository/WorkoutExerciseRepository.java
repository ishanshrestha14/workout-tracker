package com.workouttracker.repository;

import com.workouttracker.model.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {

    /**
     * Find workout exercises by workout ID
     */
    List<WorkoutExercise> findByWorkoutId(Long workoutId);

    /**
     * Find workout exercises by workout ID ordered by exercise order
     */
    List<WorkoutExercise> findByWorkoutIdOrderByExerciseOrder(Long workoutId);

    /**
     * Find workout exercises by exercise ID
     */
    List<WorkoutExercise> findByExerciseId(Long exerciseId);

    /**
     * Find completed workout exercises
     */
    List<WorkoutExercise> findByWorkoutIdAndCompletedTrue(Long workoutId);

    /**
     * Find workout exercises by user ID (through workout relationship)
     */
    @Query("SELECT we FROM WorkoutExercise we WHERE we.workout.user.id = :userId")
    List<WorkoutExercise> findByUserId(@Param("userId") Long userId);

    /**
     * Find workout exercises for user by exercise ID
     */
    @Query("SELECT we FROM WorkoutExercise we WHERE we.workout.user.id = :userId AND we.exercise.id = :exerciseId")
    List<WorkoutExercise> findByUserIdAndExerciseId(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);

    /**
     * Get exercise statistics for user
     */
    @Query("SELECT we.exercise.name, COUNT(we), AVG(we.sets), AVG(we.repetitions), AVG(we.weightKg) " +
           "FROM WorkoutExercise we " +
           "WHERE we.workout.user.id = :userId AND we.completed = true " +
           "GROUP BY we.exercise.id, we.exercise.name " +
           "ORDER BY COUNT(we) DESC")
    List<Object[]> getExerciseStatsByUserId(@Param("userId") Long userId);

    /**
     * Find workout exercises with highest volume for user
     */
    @Query("SELECT we FROM WorkoutExercise we " +
           "WHERE we.workout.user.id = :userId AND we.weightKg IS NOT NULL " +
           "ORDER BY (we.sets * we.repetitions * we.weightKg) DESC")
    List<WorkoutExercise> findHighestVolumeExercisesByUserId(@Param("userId") Long userId);

    /**
     * Get total volume lifted by user in date range
     */
    @Query("SELECT SUM(we.sets * we.repetitions * COALESCE(we.weightKg, 0)) " +
           "FROM WorkoutExercise we " +
           "WHERE we.workout.user.id = :userId " +
           "AND we.workout.completedAt BETWEEN :startDate AND :endDate " +
           "AND we.completed = true")
    Double getTotalVolumeLiftedInDateRange(@Param("userId") Long userId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Find personal records for user by exercise
     */
    @Query("SELECT we FROM WorkoutExercise we " +
           "WHERE we.workout.user.id = :userId " +
           "AND we.exercise.id = :exerciseId " +
           "AND we.weightKg IS NOT NULL " +
           "AND we.completed = true " +
           "ORDER BY we.weightKg DESC")
    List<WorkoutExercise> findPersonalRecordsByExercise(@Param("userId") Long userId, 
                                                       @Param("exerciseId") Long exerciseId);

    /**
     * Get muscle group workout frequency for user
     */
    @Query("SELECT e.primaryMuscleGroup, COUNT(we) " +
           "FROM WorkoutExercise we JOIN we.exercise e " +
           "WHERE we.workout.user.id = :userId AND we.completed = true " +
           "GROUP BY e.primaryMuscleGroup " +
           "ORDER BY COUNT(we) DESC")
    List<Object[]> getMuscleGroupFrequencyByUserId(@Param("userId") Long userId);

    /**
     * Find workout exercises by workout and user (security check)
     */
    @Query("SELECT we FROM WorkoutExercise we " +
           "WHERE we.workout.id = :workoutId AND we.workout.user.id = :userId " +
           "ORDER BY we.exerciseOrder")
    List<WorkoutExercise> findByWorkoutIdAndUserId(@Param("workoutId") Long workoutId, 
                                                  @Param("userId") Long userId);

    /**
     * Count total exercises completed by user
     */
    @Query("SELECT COUNT(we) FROM WorkoutExercise we " +
           "WHERE we.workout.user.id = :userId AND we.completed = true")
    long countCompletedExercisesByUserId(@Param("userId") Long userId);

    /**
     * Find most recent workout exercises for user by exercise
     */
    @Query("SELECT we FROM WorkoutExercise we " +
           "WHERE we.workout.user.id = :userId " +
           "AND we.exercise.id = :exerciseId " +
           "AND we.completed = true " +
           "ORDER BY we.workout.completedAt DESC")
    List<WorkoutExercise> findRecentWorkoutExercisesByExercise(@Param("userId") Long userId, 
                                                             @Param("exerciseId") Long exerciseId);

    /**
     * Get workout exercise progress over time for specific exercise
     */
    @Query("SELECT we.workout.completedAt, we.sets, we.repetitions, we.weightKg " +
           "FROM WorkoutExercise we " +
           "WHERE we.workout.user.id = :userId " +
           "AND we.exercise.id = :exerciseId " +
           "AND we.completed = true " +
           "AND we.workout.status = 'COMPLETED' " +
           "ORDER BY we.workout.completedAt ASC")
    List<Object[]> getExerciseProgressOverTime(@Param("userId") Long userId, 
                                             @Param("exerciseId") Long exerciseId);

    /**
     * Delete workout exercises by workout ID
     */
    void deleteByWorkoutId(Long workoutId);

    /**
     * Find workout exercises by user ID and completed date between (for reporting)
     */
    @Query("SELECT we FROM WorkoutExercise we " +
           "WHERE we.workout.user.id = :userId " +
           "AND we.workout.completedAt BETWEEN :startDate AND :endDate " +
           "AND we.completed = true " +
           "ORDER BY we.workout.completedAt ASC")
    List<WorkoutExercise> findByWorkoutUserIdAndCompletedDateBetween(@Param("userId") Long userId,
                                                                   @Param("startDate") LocalDateTime startDate,
                                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Find workout exercises (with workout and exercise loaded) for workouts scheduled
     * in a date range, in the order they should appear in a CSV export
     */
    @Query("SELECT we FROM WorkoutExercise we " +
           "JOIN FETCH we.workout w " +
           "JOIN FETCH we.exercise e " +
           "WHERE w.user.id = :userId " +
           "AND w.scheduledDateTime BETWEEN :startDate AND :endDate " +
           "ORDER BY w.scheduledDateTime ASC, w.id ASC, we.exerciseOrder ASC")
    List<WorkoutExercise> findForExportByUserIdAndScheduledDateBetween(@Param("userId") Long userId,
                                                                     @Param("startDate") LocalDateTime startDate,
                                                                     @Param("endDate") LocalDateTime endDate);
}
