package view;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
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

        // 1. 活動名稱
        TableColumn<RegistrationRow, String> colName = new TableColumn<>("活動名稱");
        colName.setCellValueFactory(new PropertyValueFactory<>("activityName"));
        colName.setPrefWidth(220);

        // 2. 日期
        TableColumn<RegistrationRow, String> colDate = new TableColumn<>("日期");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setPrefWidth(100);
        colDate.setStyle("-fx-alignment: CENTER;");

        // 3. 地點
        TableColumn<RegistrationRow, String> colLocation = new TableColumn<>("地點");
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colLocation.setPrefWidth(180);

        // 4. 狀態 (Badge)
        TableColumn<RegistrationRow, Boolean> colStatus = new TableColumn<>("狀態");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("upcoming"));
        colStatus.setPrefWidth(120);
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean upcoming, boolean empty) {
                super.updateItem(upcoming, empty);
                if (empty || upcoming == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(upcoming ? "已確認" : "已參與");
                badge.getStyleClass().add("status-badge");
                if (upcoming) {
                    badge.getStyleClass().add("status-badge-confirmed");
                } else {
                    badge.getStyleClass().add("status-badge-attended");
                }
                HBox box = new HBox(badge);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });

        // 5. 操作 (Hyperlinks)
        TableColumn<RegistrationRow, Void> colAction = new TableColumn<>("操作");
        colAction.setPrefWidth(200);
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Hyperlink cancelLink = new Hyperlink("取消報名");
            private final Hyperlink ticketLink = new Hyperlink("檢視票券");
            private final HBox container = new HBox(16, ticketLink);

            {
                cancelLink.setStyle("-fx-text-fill: #ef4444; -fx-underline: false; -fx-font-weight: 500;");
                cancelLink.hoverProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) cancelLink.setStyle("-fx-text-fill: #dc2626; -fx-underline: true; -fx-font-weight: 500;");
                    else cancelLink.setStyle("-fx-text-fill: #ef4444; -fx-underline: false; -fx-font-weight: 500;");
                });

                ticketLink.setStyle("-fx-text-fill: #009CF7; -fx-underline: false; -fx-font-weight: 500;");
                ticketLink.hoverProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) ticketLink.setStyle("-fx-text-fill: #0080d0; -fx-underline: true; -fx-font-weight: 500;");
                    else ticketLink.setStyle("-fx-text-fill: #009CF7; -fx-underline: false; -fx-font-weight: 500;");
                });

                ticketLink.setOnAction(e -> {
                    RegistrationRow row = getTableView().getItems().get(getIndex());
                    showTicketDialog(row);
                });

                cancelLink.setOnAction(e -> {
                    RegistrationRow row = getTableView().getItems().get(getIndex());
                    
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("確認取消報名");
                    confirm.setHeaderText("確定要取消此活動的報名嗎？");
                    confirm.setContentText("活動名稱：" + row.getActivityName());
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == javafx.scene.control.ButtonType.OK) {
                            onCancel.accept(row.getParticipantId(), row.getActivityId());
                            getTableView().getItems().remove(row);
                        }
                    });
                });
                
                container.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                RegistrationRow row = getTableView().getItems().get(getIndex());
                container.getChildren().clear();
                container.getChildren().add(ticketLink);
                if (row.isUpcoming()) {
                    container.getChildren().add(cancelLink);
                }
                setGraphic(container);
            }
        });

        table.getColumns().addAll(colName, colDate, colLocation, colStatus, colAction);
        table.setItems(data);
        table.setFixedCellSize(-1);
        return table;
    }

    private void showTicketDialog(RegistrationRow row) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("活動票券資訊");
        alert.setHeaderText("🎫 電子票券資訊");
        
        VBox vbox = new VBox(12);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new javafx.geometry.Insets(16));
        vbox.setPrefWidth(320);
        
        Label lblName = new Label(row.getActivityName());
        lblName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0a5338; -fx-wrap-text: true; -fx-alignment: center;");
        
        Label lblSerial = new Label("報名序號：" + row.getSerialNumber());
        lblSerial.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        
        Label lblDetail = new Label(row.getDetail());
        lblDetail.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-line-spacing: 4;");
        
        Label barCode = new Label("||| | ||| | || || | ||| || ||| || ||| ");
        barCode.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-padding: 12 0 0 0;");
        
        vbox.getChildren().addAll(lblName, lblSerial, lblDetail, barCode);
        
        alert.getDialogPane().setContent(vbox);
        alert.showAndWait();
    }
}
