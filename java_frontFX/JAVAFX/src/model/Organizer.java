package model;

import java.util.ArrayList;
import java.util.List;

public class Organizer extends User {

    private final String unit;
    private final List<String> hostedActivityIds = new ArrayList<>();

    public Organizer(String id, String name, String unit) {
        super(id, name);
        this.unit = unit != null ? unit : "";
    }

    @Override
    public String getRole() { return "organizer"; }

    @Override
    public boolean isOrganizer() { return true; }

    public String getUnit() { return unit; }

    @Override
    public void displayMenu() {
        System.out.println("【主辦單位選單】1.活動管理 2.建立活動 3.登出");
    }

    public void trackHostedEvent(String activityId) {
        if (!hostedActivityIds.contains(activityId)) {
            hostedActivityIds.add(activityId);
        }
    }

    public List<String> getHostedActivityIds() {
        return hostedActivityIds;
    }
}
