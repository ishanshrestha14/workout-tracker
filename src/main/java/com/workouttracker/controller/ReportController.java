package com.workouttracker.controller;

import com.workouttracker.dto.response.ReportResponse;
import com.workouttracker.service.CsvExportService;
import com.workouttracker.service.PdfReportService;
import com.workouttracker.service.ReportService;
import com.workouttracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports", description = "Workout progress reports and analytics APIs")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private CsvExportService csvExportService;

    @Autowired
    private PdfReportService pdfReportService;

    @Operation(summary = "Generate progress report", 
               description = "Generate a comprehensive progress report for a specific time period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range or parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/progress")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> generateProgressReport(
            @Parameter(description = "Start date for the report (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date for the report (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            // Validate date range
            if (startDate.isAfter(endDate)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid date range");
                errorResponse.put("message", "Start date must be before end date");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Get current user ID
            Long userId = userService.getCurrentUserEntity().getId();
            
            logger.info("Generating progress report for user {} from {} to {}", userId, startDate, endDate);
            
            ReportResponse report = reportService.generateProgressReport(userId, startDate, endDate);
            
            return ResponseEntity.ok(report);
            
        } catch (RuntimeException e) {
            logger.error("Error generating progress report: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Report generation failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error generating progress report: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred while generating the report");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Generate weekly report", 
               description = "Generate a progress report for the past week")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly report generated successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/weekly")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> generateWeeklyReport() {
        try {
            Long userId = userService.getCurrentUserEntity().getId();
            
            logger.info("Generating weekly report for user {}", userId);
            
            ReportResponse report = reportService.generateWeeklyReport(userId);
            
            return ResponseEntity.ok(report);
            
        } catch (RuntimeException e) {
            logger.error("Error generating weekly report: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Weekly report generation failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error generating weekly report: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred while generating the weekly report");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Generate monthly report", 
               description = "Generate a progress report for the past month")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Monthly report generated successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/monthly")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> generateMonthlyReport() {
        try {
            Long userId = userService.getCurrentUserEntity().getId();
            
            logger.info("Generating monthly report for user {}", userId);
            
            ReportResponse report = reportService.generateMonthlyReport(userId);
            
            return ResponseEntity.ok(report);
            
        } catch (RuntimeException e) {
            logger.error("Error generating monthly report: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Monthly report generation failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error generating monthly report: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred while generating the monthly report");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Generate performance analytics", 
               description = "Generate detailed performance analytics and trends for a specific period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Performance analytics generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range or parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> generatePerformanceAnalytics(
            @Parameter(description = "Start date for analytics (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date for analytics (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            // Validate date range
            if (startDate.isAfter(endDate)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid date range");
                errorResponse.put("message", "Start date must be before end date");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Long userId = userService.getCurrentUserEntity().getId();
            
            logger.info("Generating performance analytics for user {} from {} to {}", userId, startDate, endDate);
            
            ReportResponse report = reportService.generatePerformanceAnalytics(userId, startDate, endDate);
            
            return ResponseEntity.ok(report);
            
        } catch (RuntimeException e) {
            logger.error("Error generating performance analytics: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Analytics generation failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error generating performance analytics: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred while generating analytics");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Generate user-specific report", 
               description = "Generate a progress report for a specific user (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User report generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied - admin role required"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateUserReport(
            @Parameter(description = "User ID for the report")
            @PathVariable Long userId,
            
            @Parameter(description = "Start date for the report (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date for the report (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            // Validate date range
            if (startDate.isAfter(endDate)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid date range");
                errorResponse.put("message", "Start date must be before end date");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            logger.info("Generating report for user {} from {} to {}", userId, startDate, endDate);
            
            ReportResponse report = reportService.generateProgressReport(userId, startDate, endDate);
            
            return ResponseEntity.ok(report);
            
        } catch (RuntimeException e) {
            logger.error("Error generating user report: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User report generation failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error generating user report: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred while generating the user report");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Export report as PDF", 
               description = "Export a progress report as PDF file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF report exported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> exportReportAsPdf(
            @Parameter(description = "Start date for the report (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date for the report (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            if (startDate.isAfter(endDate)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid date range");
                errorResponse.put("message", "Start date must be before end date");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Long userId = userService.getCurrentUserEntity().getId();
            
            logger.info("Exporting PDF report for user {} from {} to {}", userId, startDate, endDate);
            
            ReportResponse report = reportService.generateProgressReport(userId, startDate, endDate);
            byte[] pdf = pdfReportService.render(report);
            
            String filename = String.format("progress-report_%s_%s.pdf", startDate.toLocalDate(), endDate.toLocalDate());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);
            
        } catch (Exception e) {
            logger.error("Error exporting PDF report: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "PDF export failed");
            errorResponse.put("message", "An error occurred while exporting the report as PDF");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Export report as CSV", 
               description = "Export workout data as CSV file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV report exported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> exportReportAsCsv(
            @Parameter(description = "Start date for the report (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date for the report (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            if (startDate.isAfter(endDate)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid date range");
                errorResponse.put("message", "Start date must be before end date");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Long userId = userService.getCurrentUserEntity().getId();
            
            logger.info("Exporting CSV report for user {} from {} to {}", userId, startDate, endDate);
            
            String csvContent = csvExportService.exportWorkouts(userId, startDate, endDate);
            
            String filename = String.format("workouts_%s_%s.csv", startDate.toLocalDate(), endDate.toLocalDate());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent);
            
        } catch (Exception e) {
            logger.error("Error exporting CSV report: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "CSV export failed");
            errorResponse.put("message", "An error occurred while exporting the report as CSV");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
