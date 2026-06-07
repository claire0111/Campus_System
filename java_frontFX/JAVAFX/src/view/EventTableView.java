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
            Consumer<Event> onRegister,
            Consumer<Event> onDetail
    ) {

        TableView<Event> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ================= 樣式 =================
        table.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-control-inner-background: #ffffff;" +
                "-fx-control-inner-background-alt: #f8fafc;" +
                "-fx-font-size: 14px;"
        );

        // ================= Columns =================
        TableColumn<Event, String> colName = new TableColumn<>("活動名稱");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Event, String> colLocation = new TableColumn<>("地點");
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Event, String> colTime = new TableColumn<>("活動時間");
        colTime.setCellValueFactory(new PropertyValueFactory<>("eventTime"));

        TableColumn<Event, String> colStatus = new TableColumn<>("狀態");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // ================= 操作欄 =================
        TableColumn<Event, Void> colAction = new TableColumn<>("操作");

        colAction.setCellFactory(param -> new TableCell<>() {

            private final Button btn = new Button("報名");

            {
                btn.setStyle(
                        "-fx-background-color: #0a5338; -fx-text-fill: white;" +
                        "-fx-font-size: 13px; -fx-background-radius: 6; -fx-cursor: hand;");

                btn.setOnAction(e -> {
                    Event event = getTableView().getItems().get(getIndex());
                    onRegister.accept(event);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Event event = getTableView().getItems().get(getIndex());

                if ("⛔ 已結束".equals(event.getStatus())) {
                    btn.setText("已結束");
                    btn.setDisable(true);
                    btn.setStyle("-fx-background-color: #d1d5db; -fx-text-fill: #6b7280; -fx-font-size: 13px; -fx-background-radius: 6;");
                } else if ("尚未開始".equals(event.getStatus())) {
                    btn.setText("未開始");
                    btn.setDisable(true);
                    btn.setStyle("-fx-background-color: #d1d5db; -fx-text-fill: #6b7280; -fx-font-size: 13px; -fx-background-radius: 6;");
                } else {
                    btn.setText("報名");
                    btn.setDisable(false);
                    btn.setStyle(
                            "-fx-background-color: #0a5338; -fx-text-fill: white;" +
                            "-fx-font-size: 13px; -fx-background-radius: 6; -fx-cursor: hand;");
                }

                HBox box = new HBox(btn);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(colName, colLocation, colTime, colStatus, colAction);
        table.setItems(data);

        // ================= 雙擊顯示詳細 =================
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
