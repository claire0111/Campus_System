package view;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.Event;

public class EventTableView {

    public static TableView<Event> create(
            ObservableList<Event> data,
            Consumer<Event> onDetail
    ) {
        TableView<Event> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Event, String> colName = new TableColumn<>("活動名稱");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Event, String> colLocation = new TableColumn<>("地點");
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Event, String> colTime = new TableColumn<>("活動時間");
        colTime.setCellValueFactory(new PropertyValueFactory<>("eventTime"));

        TableColumn<Event, String> colUnit = new TableColumn<>("主辦單位");
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn<Event, String> colStatus = new TableColumn<>("狀態");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(status);
                getStyleClass().removeAll("status-open", "status-closed", "status-pending");
                if (status.contains("報名中")) getStyleClass().add("status-open");
                else if (status.contains("結束")) getStyleClass().add("status-closed");
                else getStyleClass().add("status-pending");
            }
        });

        TableColumn<Event, Void> colAction = new TableColumn<>("操作");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("查看詳情");

            {
                btn.getStyleClass().add("btn-primary");
                btn.setOnAction(e -> onDetail.accept(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                HBox box = new HBox(btn);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(colName, colLocation, colTime, colUnit, colStatus, colAction);
        table.setItems(data);

        table.setRowFactory(tv -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    onDetail.accept(row.getItem());
                }
            });
            return row;
        });

        return table;
    }
}
