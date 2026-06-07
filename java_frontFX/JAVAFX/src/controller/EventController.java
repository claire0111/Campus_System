package controller;

import model.Event;
import service.EventService;
import service.LoginService;
import service.RegistrationService;
import view.EventDialogView;

public class EventController {

    private EventService eventService;
    private RegistrationService regService;
    private LoginService loginService;

    public EventController(EventService eventService,
                           RegistrationService regService,
                           LoginService loginService) {
        this.eventService = eventService;
        this.regService = regService;
        this.loginService = loginService;
    }

    // 報名活動
    public boolean handleRegister(Event event) {
        if (!loginService.isLoggedIn()) return false;
        String studentId = loginService.getCurrentUser().getId();
        return regService.register(studentId, event.getName());
    }

    // 顯示活動詳細
    public void showDetail(Event event) {
        EventDialogView.show(event);
    }
}
