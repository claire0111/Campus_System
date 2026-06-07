package model;
// 活動資料模型（將欄位改為 Property 或普通欄位，這裡使用普通欄位但在編輯後需要 refresh 表格）
public class Event {
    private String name;
    private String location;
    private String regTime;
    private String eventTime;
    private String contact;
    private String status; // "尚未開始", "🟢 報名中", "⛔ 已結束"
    private String unit;
    private String content;

    public Event(String name, String location, String regTime, String eventTime, String contact, String status,
            String unit, String content) {
        this.name = name;
        this.location = location;
        this.regTime = regTime;
        this.eventTime = eventTime;
        this.contact = contact;
        this.status = status;
        this.unit = unit;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}