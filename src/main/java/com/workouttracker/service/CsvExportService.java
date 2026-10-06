package com.workouttracker.service;

import com.workouttracker.model.Exercise;
import com.workouttracker.model.Workout;
import com.workouttracker.model.WorkoutExercise;
import com.workouttracker.repository.WorkoutExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exports a user's workout data as CSV (RFC 4180), one row per exercise performed.
 */
@Service
@Transactional(readOnly = true)
public class CsvExportService {

    static final String HEADER = "Date,Workout,Status,Exercise,Category,Sets,Reps,Weight (kg)," +
            "Distance (km),Duration (s),Calories,Completed";

    private static final String LINE_SEPARATOR = "\r\n";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private WorkoutExerciseRepository workoutExerciseRepository;

    public String exportWorkouts(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<WorkoutExercise> rows = workoutExerciseRepository
                .findForExportByUserIdAndScheduledDateBetween(userId, startDate, endDate);

        return Stream.concat(Stream.of(HEADER), rows.stream().map(this::toCsvRow))
                .collect(Collectors.joining(LINE_SEPARATOR, "", LINE_SEPARATOR));
    }

    private String toCsvRow(WorkoutExercise we) {
        Workout workout = we.getWorkout();
        Exercise exercise = we.getExercise();

        return Stream.of(
                workout.getScheduledDateTime() != null ? workout.getScheduledDateTime().format(DATE_FORMAT) : "",
                text(workout.getName()),
                workout.getStatus().name(),
                text(exercise.getName()),
                exercise.getCategory().name(),
                number(we.getSets()),
                number(we.getRepetitions()),
                number(we.getWeightKg()),
                number(we.getDistanceKm()),
                number(we.getDurationSeconds()),
                number(we.getCaloriesBurned()),
                String.valueOf(we.isCompleted())
        ).collect(Collectors.joining(","));
    }

    private static String number(Number value) {
        return value != null ? value.toString() : "";
    }

    /**
     * Escapes user-entered text. Values that a spreadsheet would treat as a formula
     * (CSV injection) are prefixed with a single quote, and values containing a comma,
     * quote or line break are quoted with inner quotes doubled.
     */
    static String text(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        String escaped = value;
        if ("=+-@\t\r".indexOf(escaped.charAt(0)) >= 0) {
            escaped = "'" + escaped;
        }

        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            escaped = "\"" + escaped.replace("\"", "\"\"") + "\"";
        }

        return escaped;
    }
}
