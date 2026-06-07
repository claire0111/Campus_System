package view;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.Event;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdminEventTableView {

    public static TableView<Event> create(
            ObservableList<Event> data,
            String organizerId,
            BiConsumer<Event, Boolean> onEditOrDelete,
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

        TableColumn<Event, String> colLimit = new TableColumn<>("人數限制");
        colLimit.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(cell.getValue().getLimit())));

        TableColumn<Event, String> colStatus = new TableColumn<>("狀態");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Event, Void> colAction = new TableColumn<>("操作");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("編輯");
            private final Button deleteBtn = new Button("刪除");
            private final Button detailBtn = new Button("詳情");

            {
                editBtn.getStyleClass().add("btn-primary");
                deleteBtn.getStyleClass().add("btn-danger");
                detailBtn.getStyleClass().add("btn-secondary");

                editBtn.setOnAction(e -> onEditOrDelete.accept(getTableView().getItems().get(getIndex()), true));
                deleteBtn.setOnAction(e -> onEditOrDelete.accept(getTableView().getItems().get(getIndex()), false));
                detailBtn.setOnAction(e -> onDetail.accept(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Event event = getTableView().getItems().get(getIndex());
                boolean isOwner = organizerId.equals(event.getContact());

                editBtn.setDisable(!isOwner);
                deleteBtn.setDisable(!isOwner);

                HBox box = new HBox(6, detailBtn, editBtn, deleteBtn);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(colName, colLocation, colTime, colLimit, colStatus, colAction);
        table.setItems(data);
        return table;
    }
}
