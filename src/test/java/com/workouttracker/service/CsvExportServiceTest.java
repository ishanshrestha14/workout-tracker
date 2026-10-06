package com.workouttracker.service;

import com.workouttracker.model.Exercise;
import com.workouttracker.model.User;
import com.workouttracker.model.Workout;
import com.workouttracker.model.WorkoutExercise;
import com.workouttracker.repository.WorkoutExerciseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvExportServiceTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 10, 31, 23, 59);

    @Mock
    private WorkoutExerciseRepository workoutExerciseRepository;

    @InjectMocks
    private CsvExportService csvExportService;

    @Test
    void exportWorkouts_returnsOnlyHeaderWhenThereIsNoData() {
        when(workoutExerciseRepository.findForExportByUserIdAndScheduledDateBetween(1L, START, END))
                .thenReturn(List.of());

        String csv = csvExportService.exportWorkouts(1L, START, END);

        assertThat(csv).isEqualTo(CsvExportService.HEADER + "\r\n");
    }

    @Test
    void exportWorkouts_writesOneCrlfTerminatedRowPerExercise() {
        Workout workout = new Workout("Push day", null, LocalDateTime.of(2026, 10, 2, 9, 30), new User());
        workout.setStatus(Workout.WorkoutStatus.COMPLETED);

        WorkoutExercise bench = workoutExercise(workout, "Bench Press", Exercise.Category.STRENGTH_TRAINING, 3, 10, 60.5);
        bench.setCompleted(true);
        WorkoutExercise run = workoutExercise(workout, "Treadmill", Exercise.Category.CARDIO, 1, 1, null);
        run.setDistanceKm(5.0);
        run.setDurationSeconds(1500);
        run.setCaloriesBurned(300);

        when(workoutExerciseRepository.findForExportByUserIdAndScheduledDateBetween(1L, START, END))
                .thenReturn(List.of(bench, run));

        String[] lines = csvExportService.exportWorkouts(1L, START, END).split("\r\n");

        assertThat(lines).containsExactly(
                CsvExportService.HEADER,
                "2026-10-02T09:30:00,Push day,COMPLETED,Bench Press,STRENGTH_TRAINING,3,10,60.5,,,,true",
                "2026-10-02T09:30:00,Push day,COMPLETED,Treadmill,CARDIO,1,1,,5.0,1500,300,false");
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '`', value = {
            "Leg day           | Leg day",
            "Push, pull        | \"Push, pull\"",
            "The \"big\" one   | \"The \"\"big\"\" one\"",
            "=HYPERLINK(\"x\") | \"'=HYPERLINK(\"\"x\"\")\"",
            "+1 day            | '+1 day",
            "-5kg              | '-5kg",
            "@SUM(A1)          | '@SUM(A1)",
    })
    void text_escapesSpecialCharactersAndFormulaPrefixes(String input, String expected) {
        assertThat(CsvExportService.text(input.strip())).isEqualTo(expected.strip());
    }

    @Test
    void text_quotesValuesContainingLineBreaks() {
        assertThat(CsvExportService.text("line one\nline two")).isEqualTo("\"line one\nline two\"");
    }

    @Test
    void text_returnsEmptyStringForNull() {
        assertThat(CsvExportService.text(null)).isEmpty();
    }

    private static WorkoutExercise workoutExercise(Workout workout, String exerciseName, Exercise.Category category,
                                                   int sets, int reps, Double weightKg) {
        Exercise exercise = new Exercise(exerciseName, null, category, Exercise.MuscleGroup.FULL_BODY,
                Exercise.DifficultyLevel.BEGINNER);
        return new WorkoutExercise(workout, exercise, sets, reps, weightKg);
    }
}
