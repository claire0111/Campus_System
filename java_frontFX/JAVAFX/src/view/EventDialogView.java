package view;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import model.Event;

public class EventDialogView {

    // 詳細資訊視窗
    public static void show(Event e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("活動詳細資訊");
        alert.setHeaderText(e.getName());

        VBox content = new VBox(10);

        Label location = new Label("📍 地點：" + e.getLocation());
        Label unit     = new Label("🏢 單位：" + e.getUnit());
        Label time     = new Label("⏰ 活動時間：" + e.getEventTime());
        Label regTime  = new Label("📝 報名時間：" + e.getRegTime());
        Label contact  = new Label("👤 聯絡人：" + e.getContact());
        Label status   = new Label("📊 狀態：" + e.getStatus());

        String labelStyle = "-fx-font-size: 14px;";
        location.setStyle(labelStyle);
        unit.setStyle(labelStyle);
        time.setStyle(labelStyle);
        regTime.setStyle(labelStyle);
        contact.setStyle(labelStyle);
        status.setStyle(labelStyle);

        TextArea detail = new TextArea(e.getContent());
        detail.setWrapText(true);
        detail.setEditable(false);
        detail.setPrefHeight(180);

        content.getChildren().addAll(
                location,
                unit,
                time,
                regTime,
                contact,
                status,
                new Label("📋 詳細內容："),
                detail
        );

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setMinWidth(520);

        alert.showAndWait();
    }
}
