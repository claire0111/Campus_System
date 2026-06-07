package view;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Event;

import java.util.Optional;
import java.util.function.Consumer;

public class EventFormView {

    public static void showCreate(String organizerId, Consumer<EventFormData> onSave) {
        showDialog(null, organizerId, onSave);
    }

    public static void showEdit(Event event, String organizerId, Consumer<EventFormData> onSave) {
        showDialog(event, organizerId, onSave);
    }

    private static void showDialog(Event existing, String organizerId, Consumer<EventFormData> onSave) {
        Dialog<EventFormData> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "建立活動" : "編輯活動");
        dialog.setHeaderText(existing == null ? "請填寫活動資訊" : "修改活動：" + existing.getName());

        ButtonType saveBtn = new ButtonType("儲存", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(existing != null ? existing.getName() : "");
        TextField locationField = new TextField(existing != null ? existing.getLocation() : "");
        TextField eventTimeField = new TextField(existing != null ? existing.getEventTime() : "");
        TextField regStartField = new TextField(existing != null ? existing.getRegStart() : "");
        TextField regEndField = new TextField(existing != null ? existing.getRegEnd() : "");
        TextField unitField = new TextField(existing != null ? existing.getUnit() : "");
        TextField limitField = new TextField(existing != null ? String.valueOf(existing.getLimit()) : "");
        TextArea contentArea = new TextArea(existing != null ? existing.getContent() : "");
        contentArea.setPrefRowCount(4);
        contentArea.setWrapText(true);

        int row = 0;
        grid.add(new Label("標題 *"), 0, row);
        grid.add(nameField, 1, row++);
        grid.add(new Label("地點 *"), 0, row);
        grid.add(locationField, 1, row++);
        grid.add(new Label("活動時間 *"), 0, row);
        grid.add(eventTimeField, 1, row++);
        grid.add(new Label("報名開始 *"), 0, row);
        grid.add(regStartField, 1, row++);
        grid.add(new Label("報名結束 *"), 0, row);
        grid.add(regEndField, 1, row++);
        grid.add(new Label("主辦單位 *"), 0, row);
        grid.add(unitField, 1, row++);
        grid.add(new Label("人數限制 *"), 0, row);
        grid.add(limitField, 1, row++);
        grid.add(new Label("活動說明"), 0, row);
        grid.add(contentArea, 1, row++);

        Label hint = new Label("時間格式範例：2026/7/10 12:00");
        hint.getStyleClass().add("hint-text");
        grid.add(hint, 1, row);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(480);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!validate(nameField, locationField, eventTimeField,
                    regStartField, regEndField, unitField, limitField)) {
                event.consume();
            }
        });

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;

            EventFormData data = new EventFormData();
            data.id = existing != null ? existing.getId() : null;
            data.name = nameField.getText().trim();
            data.location = locationField.getText().trim();
            data.eventTime = eventTimeField.getText().trim();
            data.regStart = regStartField.getText().trim();
            data.regEnd = regEndField.getText().trim();
            data.unit = unitField.getText().trim();
            data.content = contentArea.getText().trim();
            data.limit = Integer.parseInt(limitField.getText().trim());
            data.contact = organizerId;
            return data;
        });

        Optional<EventFormData> result = dialog.showAndWait();
        result.ifPresent(onSave);
    }

    private static boolean validate(TextField name, TextField location, TextField eventTime,
                                    TextField regStart, TextField regEnd, TextField unit,
                                    TextField limit) {
        if (name.getText().trim().isEmpty() || location.getText().trim().isEmpty()
                || eventTime.getText().trim().isEmpty() || regStart.getText().trim().isEmpty()
                || regEnd.getText().trim().isEmpty() || unit.getText().trim().isEmpty()) {
            showError("請填寫所有必填欄位");
            return false;
        }
        try {
            int l = Integer.parseInt(limit.getText().trim());
            if (l <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("人數限制必須為正整數");
            return false;
        }
        return true;
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("輸入錯誤");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static class EventFormData {
        public String id;
        public String name;
        public String location;
        public String eventTime;
        public String regStart;
        public String regEnd;
        public String unit;
        public String content;
        public int limit;
        public String contact;
    }
}
