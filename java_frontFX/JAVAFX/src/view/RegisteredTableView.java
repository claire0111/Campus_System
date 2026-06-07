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
import javafx.scene.shape.Rectangle;
import model.Registration;
import service.EventService;
import service.RegistrationService;

import java.util.function.BiConsumer;

public class RegisteredTableView {

    // studentID: 目前登入學生 ID
    // onCancel: (studentID, activityID) -> void
    public TableView<Registration> createTable(
            ObservableList<Registration> data,
            BiConsumer<String, String> onCancel
    ) {

        TableView<Registration> table = new TableView<>();

        // ================= STYLE =================
        table.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-control-inner-background: #ffffff;" +
                "-fx-control-inner-background-alt: #f8fafc;" +
                "-fx-table-cell-border-color: #ddd;" +
                "-fx-padding: 0;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-radius: 12px;" +
                "-fx-border-width: 1px;" +
                "-fx-border-color: #ddd;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
                "-fx-font-size: 14px;"
        );

        // ================= ROUND CORNER =================
        Rectangle clip = new Rectangle();
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        table.setClip(clip);
        table.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.getWidth());
            clip.setHeight(newVal.getHeight());
        });

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ================= COLUMNS =================
        TableColumn<Registration, String> colSerial = new TableColumn<>("序號");
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colSerial.prefWidthProperty().bind(table.widthProperty().multiply(0.10));

        TableColumn<Registration, String> colActivity = new TableColumn<>("活動 ID");
        colActivity.setCellValueFactory(new PropertyValueFactory<>("activityID"));
        colActivity.prefWidthProperty().bind(table.widthProperty().multiply(0.40));

        TableColumn<Registration, String> colTime = new TableColumn<>("報名時間");
        colTime.setCellValueFactory(new PropertyValueFactory<>("registrationTime"));
        colTime.prefWidthProperty().bind(table.widthProperty().multiply(0.35));

        TableColumn<Registration, Void> colAction = new TableColumn<>("操作");
        colAction.prefWidthProperty().bind(table.widthProperty().multiply(0.15));

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("取消報名");

            {
                btn.setStyle(
                        "-fx-background-color: #ef4444;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 6; -fx-cursor: hand;");

                btn.setOnAction(e -> {
                    Registration reg = getTableView().getItems().get(getIndex());
                    onCancel.accept(reg.getParticipantID(), reg.getActivityID());
                    // 從表格中即時移除
                    getTableView().getItems().remove(reg);
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

        table.getColumns().addAll(colSerial, colActivity, colTime, colAction);
        table.setItems(data);

        return table;
    }
}
