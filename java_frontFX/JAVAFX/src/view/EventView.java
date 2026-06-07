package view;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Event;
import service.EventService;
import service.RegistrationService;
import service.LoginService;

public class EventView {

    public VBox createTable(
            EventService eventService,
            LoginService loginService,
            RegistrationService regService
    ) {
        ObservableList<Event> events = eventService.getEvents();

        TableView<Event> table = EventTableView.create(
                events,
                event -> EventDialogView.show(event)
        );

        VBox.setVgrow(table, Priority.ALWAYS);
        VBox box = new VBox(table);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }
}
