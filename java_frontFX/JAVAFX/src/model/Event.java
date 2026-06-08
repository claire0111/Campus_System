package model;

import service.TeacherService;
import util.EventDateUtil;

public class Event implements EventDateUtil.EventTimeHolder {
    private String id;
    private String name;
    private String location;
    private String regStart;
    private String regEnd;
    private String regTime;
    private String eventTime;
    private String contact;
    private String organizerName;
    private String status;
    private String unit;
    private String content;
    private int limit;
    private String imagePath;

    public Event(String id, String name, String location,
                 String regStart, String regEnd, String eventTime,
                 String contact, String status, String unit,
                 String content, int limit, String imagePath, String organizerName) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.regStart = regStart;
        this.regEnd = regEnd;
        this.regTime = regStart + " ~ " + regEnd;
        this.eventTime = eventTime;
        this.contact = contact;
        this.organizerName = organizerName != null && !organizerName.isBlank()
                ? organizerName : TeacherService.getName(contact);
        this.status = status;
        this.unit = unit;
        this.content = content;
        this.limit = limit;
        this.imagePath = imagePath != null ? imagePath : "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getRegStart() { return regStart; }
    public void setRegStart(String regStart) {
        this.regStart = regStart;
        this.regTime = regStart + " ~ " + regEnd;
    }

    public String getRegEnd() { return regEnd; }
    public void setRegEnd(String regEnd) {
        this.regEnd = regEnd;
        this.regTime = regStart + " ~ " + regEnd;
    }

    public String getRegTime() { return regTime; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath != null ? imagePath : ""; }

    public String getOrganizerDisplay() {
        if (organizerName != null && !organizerName.isBlank()) {
            return organizerName + (unit != null && !unit.isBlank() ? "（" + unit + "）" : "");
        }
        return contact;
    }
}
