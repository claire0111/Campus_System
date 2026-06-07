package view;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;

public class RegisteredTableView {

    public TableView<RegistrationRow> createTable(
            ObservableList<RegistrationRow> data,
            BiConsumer<String, String> onCancel
    ) {
        TableView<RegistrationRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<RegistrationRow, String> colSerial = new TableColumn<>("流水號");
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colSerial.setPrefWidth(80);
        colSerial.setStyle("-fx-alignment: CENTER;");

        TableColumn<RegistrationRow, String> colName = new TableColumn<>("活動名稱");
        colName.setCellValueFactory(new PropertyValueFactory<>("activityName"));
        colName.setPrefWidth(220);

        TableColumn<RegistrationRow, String> colDetail = new TableColumn<>("詳細資料");
        colDetail.setCellValueFactory(new PropertyValueFactory<>("detail"));
        colDetail.setCellFactory(col -> new TableCell<>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    VBox box = new VBox(label);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        TableColumn<RegistrationRow, Void> colAction = new TableColumn<>("操作");
        colAction.setPrefWidth(110);
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("取消報名");

            {
                btn.getStyleClass().add("btn-danger");
                btn.setOnAction(e -> {
                    RegistrationRow row = getTableView().getItems().get(getIndex());
                    onCancel.accept(row.getParticipantId(), row.getActivityId());
                    getTableView().getItems().remove(row);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(btn);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(colSerial, colName, colDetail, colAction);
        table.setItems(data);
        table.setFixedCellSize(-1);
        return table;
    }
}
