package view;

import model.Event;
import model.Registration;

/**
 * 「我的報名」表格顯示用資料列。
 */
public class RegistrationRow {
    private final String serialNumber;
    private final String activityId;
    private final String participantId;
    private final String activityName;
    private final String detail;
    private final String date;
    private final String location;
    private final boolean upcoming;

    public RegistrationRow(String serialNumber, String activityId, String participantId,
                           String activityName, String detail, String date, String location, boolean upcoming) {
        this.serialNumber = serialNumber;
        this.activityId = activityId;
        this.participantId = participantId;
        this.activityName = activityName;
        this.detail = detail;
        this.date = date;
        this.location = location;
        this.upcoming = upcoming;
    }

    public static RegistrationRow from(Registration reg, Event event) {
        String name = event != null ? event.getName() : reg.getActivityID();
        String detailText;
        String dateVal = "";
        String locVal = "";
        boolean upVal = false;
        if (event != null) {
            dateVal = getShortDate(event.getEventTime());
            locVal = event.getLocation();
            upVal = util.EventDateUtil.isUpcoming(event);
            detailText = String.format(
                    "地點：%s%n時間：%s%n主辦人：%s%n主辦單位：%s%n報名時間：%s",
                    event.getLocation(),
                    event.getEventTime(),
                    event.getOrganizerName(),
                    event.getUnit(),
                    formatTime(reg.getRegistrationTime())
            );
        } else {
            detailText = "活動資料已不存在（ID：" + reg.getActivityID() + "）";
        }
        return new RegistrationRow(
                reg.getSerialNumber(),
                reg.getActivityID(),
                reg.getParticipantID(),
                name,
                detailText,
                dateVal,
                locVal,
                upVal
        );
    }

    private static String getShortDate(String eventTime) {
        if (eventTime == null || eventTime.isBlank()) return "";
        try {
            String start = eventTime.contains("~") ? eventTime.split("~")[0].trim() : eventTime.trim();
            String datePart = start.split(" ")[0]; // "2026/06/20"
            String[] parts = datePart.contains("/") ? datePart.split("/") : datePart.split("-");
            if (parts.length >= 3) {
                return parts[1] + "/" + parts[2];
            }
            return datePart;
        } catch (Exception e) {
            return eventTime;
        }
    }

    private static String formatTime(String raw) {
        if (raw == null) return "";
        return raw.replace('T', ' ').split("\\.")[0];
    }

    public String getSerialNumber() { return serialNumber; }
    public String getActivityId() { return activityId; }
    public String getParticipantId() { return participantId; }
    public String getActivityName() { return activityName; }
    public String getDetail() { return detail; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public boolean isUpcoming() { return upcoming; }
}
