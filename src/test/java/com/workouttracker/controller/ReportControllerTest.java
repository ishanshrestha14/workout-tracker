package com.workouttracker.controller;

import com.workouttracker.config.SecurityConfig;
import com.workouttracker.dto.response.ReportResponse;
import com.workouttracker.model.User;
import com.workouttracker.security.JwtAuthenticationEntryPoint;
import com.workouttracker.security.JwtUtils;
import com.workouttracker.security.UserDetailsServiceImpl;
import com.workouttracker.service.CsvExportService;
import com.workouttracker.service.ReportService;
import com.workouttracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class})
class ReportControllerTest {

    private static final String ADMIN_USER_REPORT = "/reports/user/42";
    private static final String CSV_EXPORT = "/reports/export/csv";
    private static final String START = "2026-10-01T00:00:00";
    private static final String END = "2026-10-31T23:59:59";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private UserService userService;

    @MockBean
    private CsvExportService csvExportService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(roles = "USER")
    void userRoleIsForbiddenFromAdminUserReport() throws Exception {
        mockMvc.perform(get(ADMIN_USER_REPORT).param("startDate", START).param("endDate", END))
                .andExpect(status().isForbidden());

        verify(reportService, never()).generateProgressReport(any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminRoleCanReadAnotherUsersReport() throws Exception {
        when(reportService.generateProgressReport(eq(42L), any(), any())).thenReturn(new ReportResponse());

        mockMvc.perform(get(ADMIN_USER_REPORT).param("startDate", START).param("endDate", END))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousRequestToAdminUserReportIsUnauthorized() throws Exception {
        mockMvc.perform(get(ADMIN_USER_REPORT).param("startDate", START).param("endDate", END))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(reportService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void csvExportReturnsDownloadableCsv() throws Exception {
        User user = new User("alice", "alice@example.com", "hashed");
        user.setId(1L);
        when(userService.getCurrentUserEntity()).thenReturn(user);
        when(csvExportService.exportWorkouts(1L, LocalDateTime.parse(START), LocalDateTime.parse(END)))
                .thenReturn("Date,Workout\r\n");

        mockMvc.perform(get(CSV_EXPORT).param("startDate", START).param("endDate", END))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"workouts_2026-10-01_2026-10-31.csv\""))
                .andExpect(content().string("Date,Workout\r\n"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void csvExportRejectsStartDateAfterEndDate() throws Exception {
        mockMvc.perform(get(CSV_EXPORT).param("startDate", END).param("endDate", START))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid date range"));

        verifyNoInteractions(csvExportService);
    }

    @Test
    void csvExportRequiresAuthentication() throws Exception {
        mockMvc.perform(get(CSV_EXPORT).param("startDate", START).param("endDate", END))
                .andExpect(status().isUnauthorized());
    }
}
