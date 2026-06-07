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

    public RegistrationRow(String serialNumber, String activityId, String participantId,
                           String activityName, String detail) {
        this.serialNumber = serialNumber;
        this.activityId = activityId;
        this.participantId = participantId;
        this.activityName = activityName;
        this.detail = detail;
    }

    public static RegistrationRow from(Registration reg, Event event) {
        String name = event != null ? event.getName() : reg.getActivityID();
        String detailText;
        if (event != null) {
            detailText = String.format(
                    "地點：%s%n時間：%s%n主辦單位：%s%n報名時間：%s",
                    event.getLocation(),
                    event.getEventTime(),
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
                detailText
        );
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
}
