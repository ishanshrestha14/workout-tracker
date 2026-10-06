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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private WorkoutExerciseRepository workoutExerciseRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private WorkoutMapper workoutMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private WorkoutService workoutService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User("alice", "alice@example.com", "hashed");
        currentUser.setId(1L);
        when(userService.getCurrentUserEntity()).thenReturn(currentUser);
    }

    @Test
    void createWorkout_defaultsScheduleToNowAndNumbersExercisesInOrder() {
        CreateWorkoutRequest request = new CreateWorkoutRequest("Push day", null, null, List.of(
                exerciseRequest(10L), exerciseRequest(20L)));
        Workout workout = new Workout("Push day", null, null, currentUser);

        when(workoutMapper.toWorkout(request, currentUser)).thenReturn(workout);
        when(workoutRepository.save(workout)).thenReturn(workout);
        when(exerciseRepository.findById(any())).thenAnswer(inv -> Optional.of(exercise(inv.getArgument(0))));
        when(workoutMapper.toWorkoutExercise(any(), any())).thenAnswer(inv -> new WorkoutExercise());
        when(workoutExerciseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(workoutMapper.toWorkoutResponse(workout)).thenReturn(new WorkoutResponse());

        LocalDateTime before = LocalDateTime.now();
        workoutService.createWorkout(request);

        assertThat(workout.getScheduledDateTime()).isAfterOrEqualTo(before);
        ArgumentCaptor<WorkoutExercise> saved = ArgumentCaptor.forClass(WorkoutExercise.class);
        verify(workoutExerciseRepository, times(2)).save(saved.capture());
        assertThat(saved.getAllValues())
                .extracting(WorkoutExercise::getExerciseOrder)
                .containsExactly(1, 2);
        assertThat(saved.getAllValues())
                .extracting(we -> we.getExercise().getId())
                .containsExactly(10L, 20L);
    }

    @Test
    void createWorkout_failsForUnknownExercise() {
        CreateWorkoutRequest request = new CreateWorkoutRequest("Push day", null, null, List.of(exerciseRequest(99L)));
        Workout workout = new Workout("Push day", null, LocalDateTime.now(), currentUser);

        when(workoutMapper.toWorkout(request, currentUser)).thenReturn(workout);
        when(workoutRepository.save(workout)).thenReturn(workout);
        when(exerciseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.createWorkout(request))
                .hasMessage("Exercise not found with id: 99");
        verify(workoutExerciseRepository, never()).save(any());
    }

    @Test
    void getWorkoutById_onlyFindsWorkoutsOwnedByCurrentUser() {
        when(workoutRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.getWorkoutById(5L))
                .hasMessage("Workout not found with id: 5");
    }

    @Test
    void startWorkout_movesScheduledWorkoutToInProgress() {
        Workout workout = workoutWithStatus(Workout.WorkoutStatus.SCHEDULED);
        when(workoutRepository.save(workout)).thenReturn(workout);

        workoutService.startWorkout(7L);

        assertThat(workout.getStatus()).isEqualTo(Workout.WorkoutStatus.IN_PROGRESS);
        assertThat(workout.getStartedAt()).isNotNull();
    }

    @Test
    void startWorkout_rejectsWorkoutThatIsNotScheduled() {
        workoutWithStatus(Workout.WorkoutStatus.COMPLETED);

        assertThatThrownBy(() -> workoutService.startWorkout(7L))
                .hasMessage("Workout cannot be started. Current status: COMPLETED");
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void completeWorkout_recordsNotesCaloriesAndDuration() {
        Workout workout = workoutWithStatus(Workout.WorkoutStatus.IN_PROGRESS);
        workout.setStartedAt(LocalDateTime.now().minusMinutes(45));
        when(workoutRepository.save(workout)).thenReturn(workout);

        workoutService.completeWorkout(7L, "Felt strong", 420);

        assertThat(workout.getStatus()).isEqualTo(Workout.WorkoutStatus.COMPLETED);
        assertThat(workout.getNotes()).isEqualTo("Felt strong");
        assertThat(workout.getTotalCaloriesBurned()).isEqualTo(420);
        assertThat(workout.getDurationMinutes()).isEqualTo(45);
    }

    @Test
    void completeWorkout_rejectsWorkoutThatWasNotStarted() {
        workoutWithStatus(Workout.WorkoutStatus.SCHEDULED);

        assertThatThrownBy(() -> workoutService.completeWorkout(7L, null, null))
                .hasMessage("Workout cannot be completed. Current status: SCHEDULED");
    }

    @Test
    void getWorkoutStats_treatsMissingAggregatesAsZero() {
        when(workoutRepository.getWorkoutStatsByUserId(1L)).thenReturn(List.<Object[]>of(new Object[]{0L, null, null}));

        WorkoutService.WorkoutStats stats = workoutService.getWorkoutStats();

        assertThat(stats.getTotalWorkouts()).isZero();
        assertThat(stats.getAvgDurationMinutes()).isZero();
        assertThat(stats.getTotalCaloriesBurned()).isZero();
    }

    private Workout workoutWithStatus(Workout.WorkoutStatus status) {
        Workout workout = new Workout("Leg day", null, LocalDateTime.now(), currentUser);
        workout.setId(7L);
        workout.setStatus(status);
        when(workoutRepository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.of(workout));
        return workout;
    }

    private static WorkoutExerciseRequest exerciseRequest(Long exerciseId) {
        WorkoutExerciseRequest request = new WorkoutExerciseRequest();
        request.setExerciseId(exerciseId);
        request.setSets(3);
        request.setRepetitions(10);
        return request;
    }

    private static Exercise exercise(Long id) {
        Exercise exercise = new Exercise("Exercise " + id, null, Exercise.Category.STRENGTH_TRAINING,
                Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.BEGINNER);
        exercise.setId(id);
        return exercise;
    }
}
