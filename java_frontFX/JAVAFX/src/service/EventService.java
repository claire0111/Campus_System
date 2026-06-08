package service;

import exception.DataLoadException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import model.Event;
import util.CsvUtil;
import util.EventDateUtil;
import util.EventImageUtil;
import util.EventStatusUtil;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventService {

    public enum SortMode {
        EVENT_TIME_ASC, EVENT_TIME_DESC, REG_START_ASC
    }

    private ObservableList<Event> events = FXCollections.observableArrayList();
    private String filePath = "data/activity sheet.csv";

    public ObservableList<Event> loadEvents(String filePath) {
        this.filePath = filePath;
        ObservableList<Event> list = FXCollections.observableArrayList();

        try {
            List<String[]> rows = CsvUtil.readAll(filePath);
            LocalDateTime now = LocalDateTime.now();

            for (String[] data : rows) {
                if (data.length < 10) continue;
                if (data[0].isEmpty() && data[1].isEmpty()) continue;

                String regStart = data[5];
                String regEnd = data[6];
                String status = EventStatusUtil.calculate(regStart, regEnd, now);
                String imagePath = data.length > 11 ? data[11] : "";
                String organizerName = data.length > 12 ? data[12] : "";

                list.add(new Event(
                        data[0], data[1], data[2], regStart, regEnd, data[4],
                        data[8], status, data[7], data[3], parseLimit(data[9]),
                        imagePath, organizerName
                ));
            }
        } catch (DataLoadException e) {
            list.add(new Event(
                    "0", "CSV讀取失敗", "請檢查檔案",
                    "", "", "", "", "❌ 錯誤", "", e.getMessage(), 0, "", ""
            ));
        }

        this.events = list;
        return list;
    }

    private int parseLimit(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public void refreshStatus() {
        LocalDateTime now = LocalDateTime.now();
        for (Event e : events) {
            e.setStatus(EventStatusUtil.calculate(e.getRegStart(), e.getRegEnd(), now));
        }
    }

    public ObservableList<Event> getEvents() { return events; }

    public ObservableList<Event> getUpcomingEvents() {
        ObservableList<Event> upcoming = FXCollections.observableArrayList();
        for (Event e : events) {
            if (EventDateUtil.isUpcoming(e)) upcoming.add(e);
        }
        return upcoming;
    }

    public Event getById(String id) {
        for (Event e : events) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    public Event findByActivityRef(String ref) {
        if (ref == null || ref.isBlank()) return null;
        Event byId = getById(ref);
        if (byId != null) return byId;
        for (Event e : events) {
            if (ref.equals(e.getName())) return e;
        }
        return null;
    }

    public ObservableList<Event> searchAndSort(String keyword, SortMode sortMode) {
        String lower = (keyword == null || keyword.isBlank()) ? null : keyword.toLowerCase();
        ObservableList<Event> result = FXCollections.observableArrayList();
        for (Event e : events) {
            if (!EventDateUtil.isUpcoming(e)) continue;
            if (lower != null && !matchesKeyword(e, lower)) continue;
            result.add(e);
        }
        sortEvents(result, sortMode);
        return result;
    }

    private boolean matchesKeyword(Event event, String lower) {
        return event.getName().toLowerCase().contains(lower)
                || event.getLocation().toLowerCase().contains(lower)
                || event.getUnit().toLowerCase().contains(lower)
                || event.getContent().toLowerCase().contains(lower)
                || event.getContact().toLowerCase().contains(lower)
                || event.getOrganizerName().toLowerCase().contains(lower);
    }

    public void sortEvents(ObservableList<Event> list, SortMode mode) {
        Comparator<Event> cmp = switch (mode) {
            case EVENT_TIME_ASC -> Comparator.comparing(
                    e -> EventDateUtil.parseEventStart(e.getEventTime()),
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case EVENT_TIME_DESC -> Comparator.comparing(
                    e -> EventDateUtil.parseEventStart(e.getEventTime()),
                    Comparator.nullsLast(Comparator.reverseOrder()));
            case REG_START_ASC -> Comparator.comparing(
                    e -> EventDateUtil.parseDateTime(e.getRegStart()),
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };
        FXCollections.sort(list, cmp);
    }

    public FilteredList<Event> search(String keyword) {
        FilteredList<Event> filtered = new FilteredList<>(events, EventDateUtil::isUpcoming);
        if (keyword == null || keyword.isBlank()) return filtered;
        String lower = keyword.toLowerCase();
        filtered.setPredicate(e -> EventDateUtil.isUpcoming(e) && matchesKeyword(e, lower));
        return filtered;
    }

    public Event addEvent(String name, String location, String eventTime,
                          String regStart, String regEnd, String unit,
                          String contact, String content, int limit,
                          String organizerName, File imageFile) {
        String newId = generateNextId();
        String status = EventStatusUtil.calculate(regStart, regEnd, LocalDateTime.now());
        String imagePath = imageFile != null ? EventImageUtil.save(newId, imageFile) : "";

        Event event = new Event(
                newId, name, location, regStart, regEnd, eventTime,
                contact, status, unit, content, limit, imagePath, organizerName
        );
        events.add(event);
        saveToCSV();
        return event;
    }

    public boolean updateEvent(Event event, File newImageFile) {
        if (newImageFile != null) {
            if (event.getImagePath() != null && !event.getImagePath().isBlank()) {
                EventImageUtil.delete(event.getImagePath());
            }
            event.setImagePath(EventImageUtil.save(event.getId(), newImageFile));
        }
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(event.getId())) {
                event.setStatus(EventStatusUtil.calculate(
                        event.getRegStart(), event.getRegEnd(), LocalDateTime.now()));
                events.set(i, event);
                saveToCSV();
                return true;
            }
        }
        return false;
    }

    public boolean updateEvent(Event event) {
        return updateEvent(event, null);
    }

    public boolean deleteEvent(String id) {
        Event target = getById(id);
        if (target != null) {
            EventImageUtil.delete(target.getImagePath());
        }
        boolean removed = events.removeIf(e -> e.getId().equals(id));
        if (removed) saveToCSV();
        return removed;
    }

    public boolean isOrganizerOf(Event event, String organizerId) {
        return event != null && organizerId != null
                && organizerId.equals(event.getContact());
    }

    private String generateNextId() {
        int max = 0;
        for (Event e : events) {
            try {
                max = Math.max(max, Integer.parseInt(e.getId()));
            } catch (Exception ignored) {}
        }
        return String.valueOf(max + 1);
    }

    private void saveToCSV() {
        try {
            List<String[]> rows = new ArrayList<>();
            for (Event e : events) {
                String datastart = LocalDate.now().toString().replace("-", "/");
                rows.add(new String[]{
                        e.getId(), e.getName(), e.getLocation(), e.getContent(),
                        e.getEventTime(), e.getRegStart(), e.getRegEnd(),
                        e.getUnit(), e.getContact(), String.valueOf(e.getLimit()), datastart,
                        e.getImagePath(), e.getOrganizerName()
                });
            }
            CsvUtil.writeAll(filePath,
                    "activityID,activityName,location,Content,activityTime,registrationStart,registrationEnd,unit,contact,limit,datastart,imagePath,organizerName",
                    rows);
        } catch (DataLoadException ex) {
            System.out.println("❌ 儲存活動 CSV 失敗: " + ex.getMessage());
        }
    }
}
