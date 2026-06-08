package view;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Event;
import model.Organizer;
import util.EventDateUtil;
import util.EventImageUtil;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.function.Consumer;

public class EventFormView {

    public static void showCreate(Organizer organizer, Consumer<EventFormData> onSave) {
        showDialog(null, organizer, onSave);
    }

    public static void showEdit(Event event, Organizer organizer, Consumer<EventFormData> onSave) {
        showDialog(event, organizer, onSave);
    }

    private static void showDialog(Event existing, Organizer organizer, Consumer<EventFormData> onSave) {
        Dialog<EventFormData> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "建立活動" : "編輯活動");
        dialog.setHeaderText(existing == null ? "請填寫活動資訊" : "修改活動：" + existing.getName());

        ButtonType saveBtn = new ButtonType("儲存", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        Label organizerLabel = new Label(
                "主辦人：" + organizer.getName() + "　　單位：" + organizer.getUnit());
        organizerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0a5338;");
        organizerLabel.setWrapText(true);

        TextField nameField = new TextField(existing != null ? existing.getName() : "");
        TextField locationField = new TextField(existing != null ? existing.getLocation() : "");
        TextField limitField = new TextField(existing != null ? String.valueOf(existing.getLimit()) : "");
        TextArea contentArea = new TextArea(existing != null ? existing.getContent() : "");
        contentArea.setPrefRowCount(3);
        contentArea.setWrapText(true);

        DateTimePicker eventStart = new DateTimePicker("活動開始");
        DateTimePicker eventEnd = new DateTimePicker("活動結束");
        DateTimePicker regStart = new DateTimePicker("報名開始");
        DateTimePicker regEnd = new DateTimePicker("報名結束");

        if (existing != null) {
            fillRange(eventStart, eventEnd, existing.getEventTime());
            regStart.setFrom(EventDateUtil.parseDateTime(existing.getRegStart()));
            regEnd.setFrom(EventDateUtil.parseDateTime(existing.getRegEnd()));
        }

        ImageView preview = new ImageView();
        preview.setFitWidth(160);
        preview.setFitHeight(100);
        preview.setPreserveRatio(true);
        preview.getStyleClass().add("event-image-preview");

        File[] selectedImage = {null};
        if (existing != null && existing.getImagePath() != null && !existing.getImagePath().isBlank()) {
            Image img = EventImageUtil.loadImage(existing.getImagePath());
            if (img != null) preview.setImage(img);
        }

        Button uploadBtn = new Button("選擇圖片");
        uploadBtn.getStyleClass().add("btn-secondary");
        uploadBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("選擇活動封面圖片");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("圖片檔案", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"));
            File file = chooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                selectedImage[0] = file;
                preview.setImage(new Image(file.toURI().toString(), 160, 100, true, true));
            }
        });

        VBox imageBox = new VBox(8, preview, uploadBtn);
        imageBox.setAlignment(Pos.CENTER_LEFT);

        int row = 0;
        grid.add(organizerLabel, 0, row++, 2, 1);
        GridPane.setColumnSpan(organizerLabel, 2);

        grid.add(new Label("標題 *"), 0, row);
        grid.add(nameField, 1, row++);
        grid.add(new Label("地點 *"), 0, row);
        grid.add(locationField, 1, row++);
        grid.add(new Label("活動封面"), 0, row);
        grid.add(imageBox, 1, row++);
        grid.add(new Label("活動時間 *"), 0, row);
        grid.add(wrapPickers(eventStart, eventEnd), 1, row++);
        grid.add(new Label("報名時間 *"), 0, row);
        grid.add(wrapPickers(regStart, regEnd), 1, row++);
        grid.add(new Label("人數限制 *"), 0, row);
        grid.add(limitField, 1, row++);
        grid.add(new Label("活動說明"), 0, row);
        grid.add(contentArea, 1, row++);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(560);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!validate(nameField, locationField, limitField,
                    eventStart, eventEnd, regStart, regEnd)) {
                event.consume();
            }
        });

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;

            EventFormData data = new EventFormData();
            data.id = existing != null ? existing.getId() : null;
            data.name = nameField.getText().trim();
            data.location = locationField.getText().trim();
            data.eventTime = eventStart.format() + " ~ " + eventEnd.format();
            data.regStart = regStart.format();
            data.regEnd = regEnd.format();
            data.unit = organizer.getUnit();
            data.content = contentArea.getText().trim();
            data.limit = Integer.parseInt(limitField.getText().trim());
            data.contact = organizer.getId();
            data.organizerName = organizer.getName();
            data.imageFile = selectedImage[0];
            if (existing != null) {
                data.imagePath = existing.getImagePath();
            }
            return data;
        });

        Optional<EventFormData> result = dialog.showAndWait();
        result.ifPresent(onSave);
    }

    private static VBox wrapPickers(DateTimePicker first, DateTimePicker second) {
        VBox box = new VBox(6, first.getNode(), second.getNode());
        return box;
    }

    private static void fillRange(DateTimePicker start, DateTimePicker end, String range) {
        if (range == null || !range.contains("~")) {
            start.setFrom(EventDateUtil.parseDateTime(range));
            return;
        }
        String[] parts = range.split("~");
        start.setFrom(EventDateUtil.parseDateTime(parts[0].trim()));
        end.setFrom(EventDateUtil.parseDateTime(parts[1].trim()));
    }

    private static boolean validate(TextField name, TextField location, TextField limit,
                                      DateTimePicker eventStart, DateTimePicker eventEnd,
                                      DateTimePicker regStart, DateTimePicker regEnd) {
        if (name.getText().trim().isEmpty() || location.getText().trim().isEmpty()) {
            showError("請填寫標題與地點");
            return false;
        }
        if (!eventStart.isComplete() || !eventEnd.isComplete()
                || !regStart.isComplete() || !regEnd.isComplete()) {
            showError("請選擇完整的日期與時間");
            return false;
        }
        LocalDateTime es = eventStart.toDateTime();
        LocalDateTime ee = eventEnd.toDateTime();
        LocalDateTime rs = regStart.toDateTime();
        LocalDateTime re = regEnd.toDateTime();

        if (!ee.isAfter(es)) {
            showError("活動結束時間必須晚於開始時間");
            return false;
        }
        if (!re.isAfter(rs)) {
            showError("報名結束時間必須晚於開始時間");
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

    /** 日期 + 時分選擇器 */
    private static class DateTimePicker {
        private final DatePicker datePicker = new DatePicker();
        private final Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 9);
        private final Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0, 1);

        DateTimePicker(String prompt) {
            datePicker.setPromptText(prompt);
            hourSpinner.setEditable(true);
            minuteSpinner.setEditable(true);
            hourSpinner.setPrefWidth(70);
            minuteSpinner.setPrefWidth(70);
        }

        HBox getNode() {
            Label colon = new Label(":");
            HBox box = new HBox(6, datePicker, hourSpinner, colon, minuteSpinner);
            box.setAlignment(Pos.CENTER_LEFT);
            return box;
        }

        void setFrom(LocalDateTime dt) {
            if (dt == null) return;
            datePicker.setValue(dt.toLocalDate());
            hourSpinner.getValueFactory().setValue(dt.getHour());
            minuteSpinner.getValueFactory().setValue(dt.getMinute());
        }

        boolean isComplete() {
            return datePicker.getValue() != null;
        }

        LocalDateTime toDateTime() {
            LocalDate date = datePicker.getValue();
            return LocalDateTime.of(date, LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue()));
        }

        String format() {
            LocalDateTime dt = toDateTime();
            return String.format("%d/%d/%d %02d:%02d",
                    dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(),
                    dt.getHour(), dt.getMinute());
        }
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
        public String organizerName;
        public String imagePath;
        public File imageFile;
    }
}
