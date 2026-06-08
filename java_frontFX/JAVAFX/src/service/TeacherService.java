package service;

import util.CsvUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教職員 / 主辦單位資料查詢。
 */
public class TeacherService {

    private static Map<String, String[]> cache;

    public static String getName(String teacherId) {
        String[] row = getRow(teacherId);
        return row != null ? row[1] : teacherId;
    }

    public static String getUnit(String teacherId) {
        String[] row = getRow(teacherId);
        return row != null && row.length > 5 ? row[5] : "";
    }

    private static String[] getRow(String teacherId) {
        if (teacherId == null || teacherId.isBlank()) return null;
        ensureLoaded();
        return cache.get(teacherId);
    }

    private static void ensureLoaded() {
        if (cache != null) return;
        cache = new HashMap<>();
        try {
            List<String[]> rows = CsvUtil.readAll("data/teacher list.csv");
            for (String[] data : rows) {
                if (data.length > 0) cache.put(data[0], data);
            }
        } catch (Exception e) {
            cache = new HashMap<>();
        }
    }
}
