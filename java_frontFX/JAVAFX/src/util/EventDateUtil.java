package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 活動時間字串解析工具。
 */
public class EventDateUtil {

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy/M/d H:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-M-d H:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    };

    public static LocalDateTime parseDateTime(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String normalized = raw.trim().replace("/", "-").replace(" ", "T");
        try {
            return LocalDateTime.parse(normalized);
        } catch (DateTimeParseException ignored) {}

        for (DateTimeFormatter fmt : FORMATTERS) {
            try {
                return LocalDateTime.parse(raw.trim(), fmt);
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    /** 解析 "2026/07/10 16:00 ~ 2026/07/10 17:00" 的開始時間 */
    public static LocalDateTime parseEventStart(String eventTime) {
        if (eventTime == null || eventTime.isBlank()) return null;
        String start = eventTime.contains("~") ? eventTime.split("~")[0].trim() : eventTime.trim();
        return parseDateTime(start);
    }

    /** 解析活動結束時間 */
    public static LocalDateTime parseEventEnd(String eventTime) {
        if (eventTime == null || !eventTime.contains("~")) return parseEventStart(eventTime);
        String end = eventTime.split("~")[1].trim();
        return parseDateTime(end);
    }

    /** 判斷活動是否尚未結束（即將舉行或進行中） */
    public static boolean isUpcoming(EventTimeHolder event) {
        LocalDateTime end = parseEventEnd(event.getEventTime());
        if (end != null) return LocalDateTime.now().isBefore(end);
        LocalDateTime start = parseEventStart(event.getEventTime());
        return start != null && LocalDateTime.now().isBefore(start);
    }

    public interface EventTimeHolder {
        String getEventTime();
    }
}
