package model;

public class Registration {
    private User user;
    private Event event;

    private String serialNumber;
    private String participantID;
    private String activityID;
    private String registrationTime;

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
