package view;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Registration;
import service.RegistrationService;

public class RegistrationView {

    private RegisteredTableView registeredTableView = new RegisteredTableView();

    public VBox createTable(RegistrationService service, String studentId) {

        ObservableList<Registration> data = service.getByStudent(studentId);

        TableView<Registration> table = registeredTableView.createTable(
                data,
                // 取消報名 callback
                (sid, activityId) -> service.cancel(sid, activityId)
        );

        VBox.setVgrow(table, Priority.ALWAYS);
        VBox box = new VBox(table);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }
}
