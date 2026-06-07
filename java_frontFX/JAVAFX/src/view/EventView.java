package view;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import service.EventService;

public class EventView {
    public VBox createTable(EventService service) {

        TableView table = new TableView();

        TableColumn col1 = new TableColumn("活動名稱");
        TableColumn col2 = new TableColumn("地點");

        table.getColumns().addAll(col1, col2);

        table.setItems(service.getEvents());

        VBox box = new VBox(table);
        return box;
    }
}
