package com.workouttracker.repository;

import com.workouttracker.model.Workout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    /**
     * Find workouts by user ID
     */
    List<Workout> findByUserId(Long userId);

    /**
     * Find workouts by user ID and status
     */
    List<Workout> findByUserIdAndStatus(Long userId, Workout.WorkoutStatus status);

    /**
     * Find workouts by user ID ordered by scheduled date
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId ORDER BY w.scheduledDateTime ASC")
    List<Workout> findByUserIdOrderByScheduledDateTime(@Param("userId") Long userId);

    /**
     * Find upcoming workouts for user
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND w.scheduledDateTime >= :fromDate AND w.status = :status ORDER BY w.scheduledDateTime ASC")
    List<Workout> findUpcomingWorkouts(@Param("userId") Long userId, 
                                     @Param("fromDate") LocalDateTime fromDate, 
                                     @Param("status") Workout.WorkoutStatus status);

    /**
     * Find completed workouts for user in date range
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND w.status = 'COMPLETED' AND w.completedAt BETWEEN :startDate AND :endDate ORDER BY w.completedAt DESC")
    List<Workout> findCompletedWorkoutsBetween(@Param("userId") Long userId, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);

    /**
     * Find workouts by user and date range
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND w.scheduledDateTime BETWEEN :startDate AND :endDate ORDER BY w.scheduledDateTime ASC")
    List<Workout> findWorkoutsByUserAndDateRange(@Param("userId") Long userId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * Find workouts for today
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND DATE(w.scheduledDateTime) = DATE(:today) ORDER BY w.scheduledDateTime ASC")
    List<Workout> findTodayWorkouts(@Param("userId") Long userId, @Param("today") LocalDateTime today);

    /**
     * Find paginated workouts for user
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId ORDER BY w.scheduledDateTime DESC")
    Page<Workout> findByUserIdOrderByScheduledDateTimeDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find workout by ID and user ID (for security)
     */
    @Query("SELECT w FROM Workout w WHERE w.id = :workoutId AND w.user.id = :userId")
    Optional<Workout> findByIdAndUserId(@Param("workoutId") Long workoutId, @Param("userId") Long userId);

    /**
     * Count workouts by user and status
     */
    @Query("SELECT COUNT(w) FROM Workout w WHERE w.user.id = :userId AND w.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Workout.WorkoutStatus status);

    /**
     * Get workout statistics for user
     */
    @Query("SELECT COUNT(w), AVG(w.durationMinutes), SUM(w.totalCaloriesBurned) FROM Workout w WHERE w.user.id = :userId AND w.status = 'COMPLETED'")
    List<Object[]> getWorkoutStatsByUserId(@Param("userId") Long userId);

    /**
     * Find most recent completed workouts
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND w.status = 'COMPLETED' ORDER BY w.completedAt DESC")
    Page<Workout> findRecentCompletedWorkouts(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find workouts that need to be started (overdue scheduled workouts)
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND w.status = 'SCHEDULED' AND w.scheduledDateTime < :currentTime")
    List<Workout> findOverdueWorkouts(@Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find workouts by name containing (case insensitive) for specific user
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND LOWER(w.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY w.scheduledDateTime DESC")
    List<Workout> findByUserIdAndNameContainingIgnoreCase(@Param("userId") Long userId, @Param("name") String name);

    /**
     * Get workout completion rate for user in date range
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN w.status = 'COMPLETED' THEN 1 END) as completed, " +
           "COUNT(w) as total " +
           "FROM Workout w WHERE w.user.id = :userId AND w.scheduledDateTime BETWEEN :startDate AND :endDate")
    Object[] getCompletionRateStats(@Param("userId") Long userId, 
                                  @Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Find workouts with exercises count
     */
    @Query("SELECT w, COUNT(we) FROM Workout w LEFT JOIN w.workoutExercises we WHERE w.user.id = :userId GROUP BY w ORDER BY w.scheduledDateTime DESC")
    Page<Object[]> findWorkoutsWithExerciseCount(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find workouts by user ID and scheduled date between (for reporting)
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND w.scheduledDateTime BETWEEN :startDate AND :endDate ORDER BY w.scheduledDateTime ASC")
    List<Workout> findByUserIdAndScheduledDateBetween(@Param("userId") Long userId, 
                                                    @Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
}
