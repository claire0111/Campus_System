package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Event;
import model.Registration;
import service.EventService;
import service.RegistrationService;

public class RegistrationView {

    private final RegisteredTableView registeredTableView = new RegisteredTableView();

    public VBox createTable(RegistrationService regService,
                            EventService eventService,
                            String studentId) {

        ObservableList<RegistrationRow> rows = FXCollections.observableArrayList();
        for (Registration reg : regService.getByStudent(studentId)) {
            Event event = eventService.findByActivityRef(reg.getActivityID());
            rows.add(RegistrationRow.from(reg, event));
        }

        TableView<RegistrationRow> table = registeredTableView.createTable(
                rows,
                (sid, activityId) -> regService.cancel(sid, activityId)
        );

        VBox.setVgrow(table, Priority.ALWAYS);
        VBox box = new VBox(table);
        box.getStyleClass().add("table-wrapper");
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }
}
