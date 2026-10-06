package com.workouttracker.service;

import com.workouttracker.dto.response.ReportResponse;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class PdfReportServiceTest {

    private final PdfReportService pdfReportService = new PdfReportService();

    @Test
    void rendersSummaryWorkoutsAndExerciseBests() throws IOException {
        ReportResponse report = report(List.of(session("Push day", "COMPLETED")));

        String text = extractText(pdfReportService.render(report));

        assertThat(text)
                .contains("Workout Progress Report")
                .contains("User: Alice Smith (alice)")
                .contains("Period: 2026-10-01 to 2026-10-31")
                .contains("Completion rate 75.0%")
                .contains("Push day")
                .contains("COMPLETED")
                .contains("Bench Press")
                .contains("4 x 8");
    }

    @Test
    void producesDocumentWithTitleMetadata() throws IOException {
        byte[] pdf = pdfReportService.render(report(List.of()));

        assertThat(new String(pdf, 0, 5)).isEqualTo("%PDF-");
        try (PDDocument document = Loader.loadPDF(pdf)) {
            assertThat(document.getDocumentInformation().getTitle()).isEqualTo("Workout Progress Report");
        }
    }

    @Test
    void explainsWhenThereAreNoWorkouts() throws IOException {
        ReportResponse report = report(List.of());
        report.setExerciseProgress(List.of());

        String text = extractText(pdfReportService.render(report));

        assertThat(text)
                .contains("No workouts scheduled in this period.")
                .contains("No completed exercises in this period.");
    }

    @Test
    void continuesLongTablesOnNewPagesWithRepeatedHeader() throws IOException {
        List<ReportResponse.WorkoutSessionData> sessions = IntStream.rangeClosed(1, 120)
                .mapToObj(i -> session("Workout " + i, "SCHEDULED"))
                .toList();

        byte[] pdf = pdfReportService.render(report(sessions));

        try (PDDocument document = Loader.loadPDF(pdf)) {
            assertThat(document.getNumberOfPages()).isGreaterThan(1);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(2);
            stripper.setEndPage(2);
            assertThat(stripper.getText(document)).contains("Date Workout Status");
        }
        assertThat(extractText(pdf)).contains("Workout 120");
    }

    @Test
    void replacesCharactersTheFontCannotEncode() throws IOException {
        String text = extractText(pdfReportService.render(report(List.of(
                session("Leg day 💪", "COMPLETED"),
                session("सोमबार", "COMPLETED"),
                session("Café run", "COMPLETED")))));

        assertThat(text)
                .contains("Leg day ?")
                .contains("?????")
                .contains("Café run");
    }

    @Test
    void truncatesValuesWiderThanTheirColumn() throws IOException {
        String longName = "Extremely long workout name that would run into the next column of the table";

        String text = extractText(pdfReportService.render(report(List.of(session(longName, "COMPLETED")))));

        assertThat(text).doesNotContain(longName).contains("Extremely long workout name");
        assertThat(text).contains("...");
    }

    private static String extractText(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private static ReportResponse report(List<ReportResponse.WorkoutSessionData> sessions) {
        return ReportResponse.builder()
                .title("Workout Progress Report")
                .generatedAt(LocalDateTime.of(2026, 11, 1, 8, 0))
                .periodStart(LocalDateTime.of(2026, 10, 1, 0, 0))
                .periodEnd(LocalDateTime.of(2026, 10, 31, 23, 59))
                .userSummary(ReportResponse.UserSummary.builder()
                        .username("alice").fullName("Alice Smith").build())
                .workoutSummary(ReportResponse.WorkoutSummary.builder()
                        .totalWorkouts(4).completedWorkouts(3).cancelledWorkouts(0).completionRate(75.0)
                        .totalExercises(12).uniqueExercises(5).averageWorkoutDuration(48.0)
                        .totalCaloriesBurned(1350.0).build())
                .workoutSessions(sessions)
                .exerciseProgress(List.of(ReportResponse.ExerciseProgressData.builder()
                        .exerciseName("Bench Press").category("STRENGTH_TRAINING").totalSessions(3)
                        .bestWeight(80.0).bestSets(4).bestReps(8).build()))
                .build();
    }

    private static ReportResponse.WorkoutSessionData session(String name, String status) {
        return ReportResponse.WorkoutSessionData.builder()
                .workoutName(name)
                .status(status)
                .scheduledDate(LocalDateTime.of(2026, 10, 2, 9, 0))
                .exerciseCount(4)
                .duration(50)
                .caloriesBurned(400.0)
                .build();
    }
}
