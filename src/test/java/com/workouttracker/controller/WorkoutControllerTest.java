package com.workouttracker.controller;

import com.workouttracker.config.SecurityConfig;
import com.workouttracker.dto.response.WorkoutResponse;
import com.workouttracker.model.Workout;
import com.workouttracker.security.JwtAuthenticationEntryPoint;
import com.workouttracker.security.JwtUtils;
import com.workouttracker.security.UserDetailsServiceImpl;
import com.workouttracker.service.WorkoutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class})
class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkoutService workoutService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void listingWorkoutsRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/workouts"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(workoutService);
    }

    @Test
    @WithMockUser
    void listsCurrentUsersWorkouts() throws Exception {
        when(workoutService.getAllUserWorkouts()).thenReturn(List.of(
                new WorkoutResponse(1L, "Push day", Workout.WorkoutStatus.SCHEDULED, LocalDateTime.of(2026, 10, 1, 9, 0))));

        mockMvc.perform(get("/workouts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Push day"))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"));
    }

    @Test
    @WithMockUser
    void createsWorkout() throws Exception {
        when(workoutService.createWorkout(any())).thenReturn(
                new WorkoutResponse(1L, "Push day", Workout.WorkoutStatus.SCHEDULED, null));

        mockMvc.perform(post("/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Push day", "exercises": [{"exerciseId": 1, "sets": 3, "repetitions": 10}]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void rejectsWorkoutWithoutExercises() throws Exception {
        mockMvc.perform(post("/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Push day", "exercises": []}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(workoutService);
    }

    @Test
    @WithMockUser
    void returnsNotFoundForMissingWorkout() throws Exception {
        when(workoutService.getWorkoutById(99L)).thenThrow(new RuntimeException("Workout not found with id: 99"));

        mockMvc.perform(get("/workouts/99"))
                .andExpect(status().isNotFound());
    }
}
