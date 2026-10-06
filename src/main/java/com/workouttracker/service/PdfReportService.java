package com.workouttracker.service;

import com.workouttracker.dto.response.ReportResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Renders a progress report as a PDF: a summary of the period, a table of
 * workout sessions and the best performance per exercise.
 */
@Service
public class PdfReportService {

    private static final PDFont REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private static final float MARGIN = 50;
    private static final float BODY_SIZE = 10;
    private static final float LINE_HEIGHT = 15;

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String[] SESSION_HEADERS = {"Date", "Workout", "Status", "Exercises", "Minutes", "Calories"};
    private static final float[] SESSION_WIDTHS = {75, 170, 85, 60, 50, 55};

    private static final String[] EXERCISE_HEADERS = {"Exercise", "Category", "Sessions", "Best weight (kg)", "Sets x reps"};
    private static final float[] EXERCISE_WIDTHS = {150, 130, 55, 90, 70};

    public byte[] render(ReportResponse report) {
        try (PDDocument document = new PDDocument()) {
            PDDocumentInformation info = document.getDocumentInformation();
            info.setTitle(report.getTitle());
            info.setCreator("Workout Tracker");

            try (PageWriter writer = new PageWriter(document)) {
                writeHeader(writer, report);
                writeSummary(writer, report.getWorkoutSummary());
                writeSessions(writer, report.getWorkoutSessions());
                writeExerciseProgress(writer, report.getExerciseProgress());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to render PDF report", e);
        }
    }

    private void writeHeader(PageWriter writer, ReportResponse report) throws IOException {
        writer.text(report.getTitle(), BOLD, 18);
        writer.space(4);

        ReportResponse.UserSummary user = report.getUserSummary();
        if (user != null) {
            String name = user.getFullName() != null && !user.getFullName().isBlank()
                    ? user.getFullName() + " (" + user.getUsername() + ")"
                    : user.getUsername();
            writer.text("User: " + name, REGULAR, BODY_SIZE);
        }
        writer.text("Period: " + format(report.getPeriodStart(), DATE) + " to " + format(report.getPeriodEnd(), DATE),
                REGULAR, BODY_SIZE);
        writer.text("Generated: " + format(report.getGeneratedAt(), DATE_TIME), REGULAR, BODY_SIZE);
    }

    private void writeSummary(PageWriter writer, ReportResponse.WorkoutSummary summary) throws IOException {
        writer.heading("Summary");
        if (summary == null) {
            writer.text("No data for this period.", REGULAR, BODY_SIZE);
            return;
        }

        writer.keyValue("Workouts scheduled", number(summary.getTotalWorkouts()));
        writer.keyValue("Completed", number(summary.getCompletedWorkouts()));
        writer.keyValue("Cancelled", number(summary.getCancelledWorkouts()));
        writer.keyValue("Completion rate", summary.getCompletionRate() != null
                ? String.format(Locale.ROOT, "%.1f%%", summary.getCompletionRate()) : "-");
        writer.keyValue("Exercises performed", number(summary.getTotalExercises()));
        writer.keyValue("Different exercises", number(summary.getUniqueExercises()));
        writer.keyValue("Average duration", summary.getAverageWorkoutDuration() != null
                ? String.format(Locale.ROOT, "%.0f min", summary.getAverageWorkoutDuration()) : "-");
        writer.keyValue("Calories burned", summary.getTotalCaloriesBurned() != null
                ? String.format(Locale.ROOT, "%.0f", summary.getTotalCaloriesBurned()) : "-");
    }

    private void writeSessions(PageWriter writer, List<ReportResponse.WorkoutSessionData> sessions) throws IOException {
        writer.heading("Workouts");
        if (sessions == null || sessions.isEmpty()) {
            writer.text("No workouts scheduled in this period.", REGULAR, BODY_SIZE);
            return;
        }

        List<String[]> rows = new ArrayList<>();
        for (ReportResponse.WorkoutSessionData session : sessions) {
            rows.add(new String[]{
                    format(session.getScheduledDate(), DATE),
                    session.getWorkoutName(),
                    session.getStatus(),
                    number(session.getExerciseCount()),
                    number(session.getDuration()),
                    session.getCaloriesBurned() != null
                            ? String.format(Locale.ROOT, "%.0f", session.getCaloriesBurned()) : "-"
            });
        }
        writer.table(SESSION_HEADERS, SESSION_WIDTHS, rows);
    }

    private void writeExerciseProgress(PageWriter writer, List<ReportResponse.ExerciseProgressData> exercises)
            throws IOException {
        writer.heading("Best performance by exercise");
        if (exercises == null || exercises.isEmpty()) {
            writer.text("No completed exercises in this period.", REGULAR, BODY_SIZE);
            return;
        }

        List<String[]> rows = new ArrayList<>();
        for (ReportResponse.ExerciseProgressData exercise : exercises) {
            rows.add(new String[]{
                    exercise.getExerciseName(),
                    exercise.getCategory(),
                    number(exercise.getTotalSessions()),
                    exercise.getBestWeight() != null
                            ? String.format(Locale.ROOT, "%.1f", exercise.getBestWeight()) : "-",
                    exercise.getBestSets() != null && exercise.getBestReps() != null
                            ? exercise.getBestSets() + " x " + exercise.getBestReps() : "-"
            });
        }
        writer.table(EXERCISE_HEADERS, EXERCISE_WIDTHS, rows);
    }

    private static String number(Number value) {
        return value != null ? value.toString() : "-";
    }

    private static String format(LocalDateTime value, DateTimeFormatter formatter) {
        return value != null ? value.format(formatter) : "-";
    }

    /**
     * Replaces characters the built-in PDF fonts cannot encode (e.g. emoji or
     * non-Latin scripts) with '?', and line breaks with spaces.
     */
    static String sanitize(String value, PDFont font) {
        if (value == null) {
            return "";
        }

        StringBuilder result = new StringBuilder(value.length());
        value.codePoints().forEach(cp -> {
            if (Character.isISOControl(cp)) {
                result.append(' ');
                return;
            }
            String ch = new String(Character.toChars(cp));
            try {
                font.encode(ch);
                result.append(ch);
            } catch (IOException | IllegalArgumentException e) {
                result.append('?');
            }
        });
        return result.toString();
    }

    /**
     * Writes lines top to bottom, starting a new page when the current one is full.
     */
    private static final class PageWriter implements AutoCloseable {

        private final PDDocument document;
        private PDPageContentStream stream;
        private float y;

        PageWriter(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        void heading(String title) throws IOException {
            space(LINE_HEIGHT);
            text(title, BOLD, 13);
            space(2);
        }

        void text(String value, PDFont font, float size) throws IOException {
            ensureSpace(size + 5);
            write(value, font, size, MARGIN);
            y -= size + 5;
        }

        void keyValue(String key, String value) throws IOException {
            ensureSpace(LINE_HEIGHT);
            write(key, REGULAR, BODY_SIZE, MARGIN);
            write(value, BOLD, BODY_SIZE, MARGIN + 160);
            y -= LINE_HEIGHT;
        }

        void table(String[] headers, float[] widths, List<String[]> rows) throws IOException {
            ensureSpace(LINE_HEIGHT * 2);
            row(headers, widths, BOLD);
            for (String[] row : rows) {
                if (y - LINE_HEIGHT < MARGIN) {
                    newPage();
                    row(headers, widths, BOLD);
                }
                row(row, widths, REGULAR);
            }
        }

        void space(float amount) {
            y -= amount;
        }

        private void row(String[] cells, float[] widths, PDFont font) throws IOException {
            float x = MARGIN;
            for (int i = 0; i < cells.length; i++) {
                write(fit(sanitize(cells[i], font), font, widths[i] - 6), font, BODY_SIZE, x);
                x += widths[i];
            }
            y -= LINE_HEIGHT;
        }

        private void write(String value, PDFont font, float size, float x) throws IOException {
            stream.beginText();
            stream.setFont(font, size);
            stream.newLineAtOffset(x, y - size);
            stream.showText(sanitize(value, font));
            stream.endText();
        }

        /** Truncates text with an ellipsis so it stays inside its column. */
        private static String fit(String value, PDFont font, float maxWidth) throws IOException {
            if (width(value, font) <= maxWidth) {
                return value;
            }
            String ellipsis = "...";
            int end = value.length();
            while (end > 0 && width(value.substring(0, end) + ellipsis, font) > maxWidth) {
                end--;
            }
            return value.substring(0, end) + ellipsis;
        }

        private static float width(String value, PDFont font) throws IOException {
            return font.getStringWidth(value) / 1000 * BODY_SIZE;
        }

        private void ensureSpace(float needed) throws IOException {
            if (y - needed < MARGIN) {
                newPage();
            }
        }

        private void newPage() throws IOException {
            if (stream != null) {
                stream.close();
            }
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            y = page.getMediaBox().getHeight() - MARGIN;
        }

        @Override
        public void close() throws IOException {
            stream.close();
        }
    }
}
