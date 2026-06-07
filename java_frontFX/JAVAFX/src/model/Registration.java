package model;

/**
 * 報名紀錄 — 關聯學生與活動。
 */
public class Registration {
    private final String serialNumber;
    private final String participantID;
    private final String activityID;
    private final String registrationTime;

    public Registration(String serialNumber, String participantID,
                        String activityID, String registrationTime) {
        this.serialNumber = serialNumber;
        this.participantID = participantID;
        this.activityID = activityID;
        this.registrationTime = registrationTime;
    }

    public String getSerialNumber() { return serialNumber; }
    public String getParticipantID() { return participantID; }
    public String getActivityID() { return activityID; }
    public String getRegistrationTime() { return registrationTime; }
}
