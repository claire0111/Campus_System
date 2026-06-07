package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Event;
import service.LoginService;
import service.RegistrationService;
import util.EventStatusUtil;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class EventDetailView {

    public static VBox create(
            Event event,
            LoginService loginService,
            RegistrationService regService,
            Runnable onBack,
            Consumer<Event> onRegister
    ) {
        VBox root = new VBox(12);
        root.getStyleClass().add("detail-page");

        Button backBtn = new Button("← 返回活動列表");
        backBtn.getStyleClass().add("btn-ghost");
        backBtn.setOnAction(e -> onBack.run());

        VBox card = new VBox(10);
        card.getStyleClass().add("detail-card");

        Label title = new Label(event.getName());
        title.getStyleClass().add("detail-title");

        String liveStatus = EventStatusUtil.calculate(
                event.getRegStart(), event.getRegEnd(), LocalDateTime.now());
        event.setStatus(liveStatus);

        card.getChildren().addAll(
                title,
                detailRow("📍 地點", event.getLocation()),
                detailRow("🏢 主辦單位", event.getUnit()),
                detailRow("⏰ 活動時間", event.getEventTime()),
                detailRow("📝 報名時間", event.getRegTime()),
                detailRow("👤 聯絡人", event.getContact()),
                statusRow(liveStatus),
                capacityRow(event, regService)
        );

        TextArea detail = new TextArea(event.getContent());
        detail.setWrapText(true);
        detail.setEditable(false);
        detail.setPrefHeight(180);
        detail.getStyleClass().add("detail-content");

        Label contentLabel = new Label("📋 詳細內容");
        contentLabel.getStyleClass().add("detail-label");

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setPadding(new Insets(8, 0, 0, 0));

        if (loginService.isLoggedIn() && loginService.isStudent()) {
            Button registerBtn = new Button("報名參加");
            registerBtn.getStyleClass().add("btn-primary");

            if (regService.isRegistered(loginService.getUserId(), event)) {
                registerBtn.setText("✓ 已報名");
                registerBtn.setDisable(true);
            } else if (regService.isFull(event)) {
                registerBtn.setText("名額已滿");
                registerBtn.setDisable(true);
            } else if (!EventStatusUtil.isRegistrationOpen(liveStatus)) {
                registerBtn.setText("無法報名");
                registerBtn.setDisable(true);
            } else {
                registerBtn.setOnAction(e -> onRegister.accept(event));
            }
            actionBox.getChildren().add(registerBtn);
        }

        card.getChildren().addAll(contentLabel, detail, actionBox);
        VBox.setVgrow(detail, Priority.ALWAYS);
        VBox.setVgrow(card, Priority.ALWAYS);

        root.getChildren().addAll(backBtn, card);
        return root;
    }

    private static Label detailRow(String prefix, String value) {
        Label lbl = new Label(prefix + "：" + value);
        lbl.getStyleClass().add("detail-label");
        return lbl;
    }

    private static Label statusRow(String status) {
        Label lbl = new Label("📊 狀態：" + status);
        lbl.getStyleClass().add("detail-label");
        if (status.contains("報名中")) lbl.getStyleClass().add("status-open");
        else if (status.contains("結束")) lbl.getStyleClass().add("status-closed");
        else lbl.getStyleClass().add("status-pending");
        return lbl;
    }

    private static Label capacityRow(Event event, RegistrationService regService) {
        int registered = regService.countByActivity(event);
        int limit = event.getLimit();
        String text = limit > 0
                ? "👥 名額：" + registered + " / " + limit
                : "👥 已報名：" + registered + " 人";
        Label lbl = new Label(text);
        lbl.getStyleClass().add("detail-label");
        return lbl;
    }
}
