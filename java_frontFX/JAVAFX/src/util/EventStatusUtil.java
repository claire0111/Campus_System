package util;

import java.time.LocalDateTime;

public class EventStatusUtil {

    public static final String STATUS_NOT_STARTED = "尚未開始";
    public static final String STATUS_OPEN = "🟢 報名中";
    public static final String STATUS_CLOSED = "⛔ 已結束";
    public static final String STATUS_ERROR = "⚠️ 時間錯誤";

    public static String calculate(String start, String end, LocalDateTime now) {
        LocalDateTime regStart = EventDateUtil.parseDateTime(start);
        LocalDateTime regEnd = EventDateUtil.parseDateTime(end);

        if (regStart == null && regEnd == null) {
            if ((start != null && !start.isBlank()) || (end != null && !end.isBlank())) {
                return STATUS_ERROR;
            }
            return STATUS_OPEN;
        }

        if (regStart != null && now.isBefore(regStart)) {
            return STATUS_NOT_STARTED;
        }

        if (regEnd != null && now.isAfter(regEnd)) {
            return STATUS_CLOSED;
        }

        return STATUS_OPEN;
    }

    public static boolean isRegistrationOpen(String start, String end) {
        return STATUS_OPEN.equals(calculate(start, end, LocalDateTime.now()));
    }

    public static boolean isRegistrationOpen(String status) {
        return STATUS_OPEN.equals(status);
    }
}
