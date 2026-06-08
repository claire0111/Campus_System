package controller;

import model.Event;
import model.Organizer;
import service.EventService;
import service.LoginService;
import service.RegistrationService;
import view.EventFormView;

public class AdminController {

    private final EventService eventService;
    private final RegistrationService regService;
    private final LoginService loginService;

    public AdminController(EventService eventService,
                           RegistrationService regService,
                           LoginService loginService) {
        this.eventService = eventService;
        this.regService = regService;
        this.loginService = loginService;
    }

    public void createEvent(Runnable onSuccess) {
        if (!(loginService.getCurrentUser() instanceof Organizer organizer)) return;

        EventFormView.showCreate(organizer, data -> {
            Event created = eventService.addEvent(
                    data.name, data.location, data.eventTime,
                    data.regStart, data.regEnd, data.unit,
                    data.contact, data.content, data.limit,
                    data.organizerName, data.imageFile
            );
            organizer.trackHostedEvent(created.getId());
            onSuccess.run();
        });
    }

    public boolean canManage(Event event) {
        return loginService.isOrganizer()
                && eventService.isOrganizerOf(event, loginService.getUserId());
    }

    public void deleteEvent(Event event, Runnable onSuccess) {
        if (!canManage(event)) return;
        regService.cancelByEvent(event);
        eventService.deleteEvent(event.getId());
        onSuccess.run();
    }
}
