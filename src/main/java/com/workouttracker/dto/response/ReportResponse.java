package com.workouttracker.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResponse {

    private String reportId;
    private String reportType;
    private String title;
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime generatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime periodStart;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime periodEnd;
    
    private UserSummary userSummary;
    private WorkoutSummary workoutSummary;
    private ProgressMetrics progressMetrics;
    private List<WorkoutSessionData> workoutSessions;
    private List<ExerciseProgressData> exerciseProgress;
    private Map<String, Object> additionalMetrics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long userId;
        private String username;
        private String email;
        private String fullName;
        private Integer age;
        private String fitnessLevel;
        private String primaryGoal;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutSummary {
        private Integer totalWorkouts;
        private Integer completedWorkouts;
        private Integer cancelledWorkouts;
        private Double completionRate;
        private Integer totalExercises;
        private Integer uniqueExercises;
        private Double averageWorkoutDuration;
        private Double totalCaloriesBurned;
        private Map<String, Integer> workoutsByCategory;
        private Map<String, Integer> exercisesByMuscleGroup;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressMetrics {
        private Double strengthGainPercentage;
        private Double enduranceImprovement;
        private Double consistencyScore;
        private Integer personalRecords;
        private Double averageRating;
        private String trendDirection; // "IMPROVING", "STABLE", "DECLINING"
        private List<String> achievements;
        private List<String> recommendations;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutSessionData {
        private Long workoutId;
        private String workoutName;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime scheduledDate;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime completedDate;
        
        private String status;
        private Integer duration;
        private Double caloriesBurned;
        private Integer exerciseCount;
        private Double rating;
        private String notes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseProgressData {
        private String exerciseName;
        private String category;
        private String muscleGroup;
        private Integer totalSessions;
        private Double bestWeight;
        private Integer bestReps;
        private Integer bestSets;
        private Double averageRating;
        private Double progressPercentage;
        private String lastPerformed;
        private Map<String, Object> performanceHistory;
    }
}
