package service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import model.Event;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.util.List;

import util.EventStatusUtil;

public class EventService {
    // 活動資料管理、搜尋、狀態計算（可選）
    // 全部活動
    private ObservableList<Event> events = FXCollections.observableArrayList();

    // 載入活動(CSV 讀取)
    public ObservableList<Event> loadEvents(String filePath) {
        ObservableList<Event> list = FXCollections.observableArrayList();

        try {
            List<String> lines = Files.readAllLines(
                    Paths.get(filePath),
                    StandardCharsets.UTF_8
            );

            LocalDateTime now = LocalDateTime.now();

            for (int i = 1; i < lines.size(); i++) {

                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] data = parseCSV(line);
                if (data.length < 11) continue;

                String name = data[1];
                String location = data[2];
                String content = data[3];
                String activityTime = data[4];
                String regStart = data[5];
                String regEnd = data[6];
                String unit = data[7];
                String contact = data[8];

                // 狀態計算
                String  status = EventStatusUtil.calculate(regStart, regEnd, now);

                list.add(new Event(
                        name,
                        location,
                        regStart + " ~ " + regEnd,
                        activityTime,
                        contact,
                        status,
                        unit,
                        content
                ));
            }

        } catch (Exception e) {
            list.add(new Event(
                    "CSV讀取失敗",
                    "請檢查檔案",
                    "",
                    "",
                    "",
                    "❌ 錯誤",
                    "",
                    ""
            ));
        }

        this.events = list;
        return list;
    }

   

    // CSV parser（避免逗號炸掉）
    private String[] parseCSV(String line) {

        boolean inQuote = false;
        StringBuilder sb = new StringBuilder();
        java.util.List<String> result = new java.util.ArrayList<>();

        for (char c : line.toCharArray()) {

            if (c == '"') {
                inQuote = !inQuote;
            } else if (c == ',' && !inQuote) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        result.add(sb.toString().trim());

        return result.toArray(new String[0]);
    }


    // 取得全部活動
    public ObservableList<Event> getEvents() {
        return events;
    }

    // 搜尋功能
    public FilteredList<Event> search(String keyword) {
        FilteredList<Event> filtered = new FilteredList<>(events, e -> true);

        if (keyword == null || keyword.isBlank()) {
            return filtered;
        }

        String lower = keyword.toLowerCase();

        filtered.setPredicate(event ->
                event.getName().toLowerCase().contains(lower) ||
                event.getLocation().toLowerCase().contains(lower)
        );

        return filtered;
    }
}
