package model;

import service.RegistrationService;

import java.util.ArrayList;
import java.util.List;

/**
 * 學生 — 可瀏覽活動、報名與取消報名。
 */
public class Student extends User implements Registerable {

    private final List<String> registeredActivityIds = new ArrayList<>();

    public Student(String id, String name) {
        super(id, name);
    }

    @Override
    public String getRole() { return "student"; }

    @Override
    public boolean isStudent() { return true; }

    @Override
    public void displayMenu() {
        System.out.println("【學生選單】1.瀏覽活動 2.我的報名 3.登出");
    }

    @Override
    public String getParticipantId() { return id; }

    @Override
    public RegistrationService.RegisterResult registerFor(Event event, RegistrationService service) {
        RegistrationService.RegisterResult result = service.register(id, event);
        if (result == RegistrationService.RegisterResult.SUCCESS) {
            registeredActivityIds.add(event.getId());
        }
        return result;
    }

    @Override
    public void cancelRegistration(Event event, RegistrationService service) {
        service.cancel(id, event.getId());
        registeredActivityIds.remove(event.getId());
    }

    public List<String> getRegisteredActivityIds() {
        return registeredActivityIds;
    }
}
