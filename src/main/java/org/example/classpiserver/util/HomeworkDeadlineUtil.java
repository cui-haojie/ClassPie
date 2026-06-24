package org.example.classpiserver.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class HomeworkDeadlineUtil {

    private HomeworkDeadlineUtil() {
    }

    public static boolean isDeadlinePassed(String deadline) {
        LocalDateTime end = parseDeadlineEnd(deadline);
        return end != null && LocalDateTime.now().isAfter(end);
    }

    /**
     * 仅日期或 00:00:00 视为当天 23:59:59 截止。
     */
    public static LocalDateTime parseDeadlineEnd(String deadline) {
        if (deadline == null || deadline.isBlank()) {
            return null;
        }
        String text = deadline.trim();
        if (text.length() >= 10 && text.charAt(4) == '-' && text.charAt(7) == '-') {
            String datePart = text.substring(0, 10);
            try {
                LocalDate date = LocalDate.parse(datePart, DateTimeFormatter.ISO_LOCAL_DATE);
                if (text.length() <= 10 || text.contains("00:00:00")) {
                    return date.atTime(23, 59, 59);
                }
            } catch (DateTimeParseException ignored) {
                // fall through
            }
        }
        try {
            String normalized = text.replace('T', ' ').replaceAll("\\.\\d+", "");
            if (normalized.length() == 16) {
                return LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            if (text.contains("T")) {
                return LocalDateTime.parse(text.replaceAll("\\.\\d+", ""), DateTimeFormatter.ISO_DATE_TIME);
            }
            return LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    public static String formatDisplay(String deadline) {
        LocalDateTime end = parseDeadlineEnd(deadline);
        if (end == null) {
            if (deadline == null) {
                return "";
            }
            return deadline.replaceAll("\\.\\d+", "").replaceAll(":\\d{2}$", "");
        }
        return end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static String normalizeInput(String deadline) {
        if (deadline == null || deadline.isBlank()) {
            return deadline;
        }
        String text = deadline.trim().replace('T', ' ');
        if (text.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return text + " 23:59:00";
        }
        if (text.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            return text + ":00";
        }
        return text.replaceAll("\\.\\d+", "");
    }
}
