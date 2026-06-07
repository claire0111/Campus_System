package util;

import java.time.LocalDateTime;

public class EventStatusUtil {
    // 狀態計算
    public static String calculate(String start, String end, LocalDateTime now) {

        try {
            if (!start.isEmpty()) {
                LocalDateTime s = LocalDateTime.parse(
                        start.replace("/", "-").replace(" ", "T")
                );

                if (now.isBefore(s)) {
                    return "尚未開始";
                }
            }

            if (!end.isEmpty()) {
                LocalDateTime e = LocalDateTime.parse(
                        end.replace("/", "-").replace(" ", "T")
                );

                if (now.isAfter(e)) {
                    return "⛔ 已結束";
                }
            }

        } catch (Exception e) {
            return "⚠️ 時間錯誤";
        }

        return "🟢 報名中";
    }
}
