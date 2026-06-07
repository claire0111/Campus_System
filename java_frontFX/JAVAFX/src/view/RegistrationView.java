package view;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import service.RegistrationService;

public class RegistrationView {
public VBox createTable(RegistrationService service, String studentId) {

        TableView table = new TableView();

        table.setItems(service.getByStudent(studentId));

        TableColumn col1 = new TableColumn("活動ID");
        table.getColumns().add(col1);

        return new VBox(table);
    }
}
