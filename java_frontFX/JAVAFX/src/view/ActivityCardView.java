package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import model.Event;
import service.LoginService;
import service.RegistrationService;
import util.EventImageUtil;
import util.EventStatusUtil;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class ActivityCardView {

    public static VBox create(
            Event event,
            LoginService loginService,
            RegistrationService regService,
            Consumer<Event> onDetail,
            Consumer<Event> onRegister
    ) {
        VBox card = new VBox(0);
        card.getStyleClass().add("activity-card");
        card.setPrefWidth(300);
        card.setMinWidth(280);
        card.setMaxWidth(340);

        StackPane coverPane = new StackPane();
        coverPane.getStyleClass().add("activity-card-cover");
        coverPane.setPrefHeight(150);
        coverPane.setMinHeight(150);
        coverPane.setMaxHeight(150);
        coverPane.prefWidthProperty().bind(card.widthProperty());
        coverPane.setMaxWidth(340);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        clip.widthProperty().bind(coverPane.widthProperty());
        clip.heightProperty().bind(coverPane.heightProperty());
        coverPane.setClip(clip);

        Image uploaded = EventImageUtil.loadImage(event.getImagePath());
        if (uploaded != null) {
            Region coverRegion = new Region();
            coverRegion.prefWidthProperty().bind(coverPane.widthProperty());
            coverRegion.prefHeightProperty().bind(coverPane.heightProperty());
            coverRegion.setBackground(new Background(
                    new BackgroundImage(
                            uploaded,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(100, 100, true, true, false, true)
                    )
            ));
            coverPane.getChildren().add(coverRegion);
        } else {
            String name = event.getName();
            if (name.contains("機器人") || name.contains("AI") || name.contains("工作坊")) {
                coverPane.setStyle("-fx-background-color: linear-gradient(135deg, #009CF7 0%, #2E8B57 100%);");
                Label iconLabel = new Label("🤖");
                iconLabel.setStyle("-fx-font-size: 50px;");
                coverPane.getChildren().add(iconLabel);
            } else if (name.contains("運動") || name.contains("游泳") || name.contains("自我")) {
                coverPane.setStyle("-fx-background-color: linear-gradient(135deg, #0A5338 0%, #2E8B57 100%);");
                Label iconLabel = new Label("👟");
                iconLabel.setStyle("-fx-font-size: 50px;");
                coverPane.getChildren().add(iconLabel);
            } else {
                coverPane.getStyleClass().add("activity-card-default-cover");
            }
        }

        // 2. 活動內容區
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(16));

        // 狀態徽章
        LocalDateTime now = LocalDateTime.now();
        String liveStatus = EventStatusUtil.calculate(event.getRegStart(), event.getRegEnd(), now);
        event.setStatus(liveStatus);

        Label statusBadge = new Label(liveStatus);
        statusBadge.getStyleClass().add("activity-status-badge");
        if (liveStatus.contains("報名中")) {
            statusBadge.getStyleClass().add("status-badge-open");
        } else if (liveStatus.contains("結束")) {
            statusBadge.getStyleClass().add("status-badge-closed");
        } else {
            statusBadge.getStyleClass().add("status-badge-pending");
        }

        // 活動標題
        Label titleLabel = new Label(event.getName());
        titleLabel.getStyleClass().add("activity-card-title");
        titleLabel.setWrapText(true);
        titleLabel.setMinHeight(45);
        titleLabel.setMaxHeight(45);
        titleLabel.setAlignment(Pos.TOP_LEFT);

        // 地點與時間 (同一行)
        HBox infoRow = new HBox(6);
        infoRow.setAlignment(Pos.CENTER_LEFT);
        infoRow.getStyleClass().add("activity-card-info");

        Label locIcon = new Label("📍");
        Label locLabel = new Label(event.getLocation());
        locLabel.getStyleClass().add("activity-card-value");
        locLabel.setWrapText(false);
        locLabel.setMaxWidth(140);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 簡短日期格式 (e.g. 2026/06/20 -> 06/20)
        String dateText = getShortDate(event.getEventTime());
        Label dateLabel = new Label(dateText);
        dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #009CF7; -fx-font-size: 12px;");

        infoRow.getChildren().addAll(locIcon, locLabel, spacer, dateLabel);

        // 人數與名額
        int regCount = regService.countByActivity(event);
        int limit = event.getLimit();
        String capacityText = limit > 0 ? regCount + " / " + limit + " 人" : regCount + " 人";
        Label capacityLabel = new Label("👥 名額: " + capacityText);
        capacityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        // 3. 卡片底部按鈕
        HBox footerBox = new HBox(8);
        footerBox.getStyleClass().add("activity-card-footer");
        footerBox.setAlignment(Pos.CENTER);
        VBox.setMargin(footerBox, new Insets(8, 0, 0, 0));

        Button detailBtn = new Button("查看詳情");
        detailBtn.getStyleClass().add("btn-secondary");
        detailBtn.setPrefWidth(110);
        detailBtn.setOnAction(e -> {
            e.consume(); // 避免觸發卡片整體的點擊事件
            onDetail.accept(event);
        });

        Button actionBtn = new Button("我要報名");
        actionBtn.getStyleClass().add("btn-primary");
        actionBtn.setPrefWidth(110);

        // 根據狀態設定按鈕
        if (regService.isRegistered(loginService.getUserId(), event)) {
            actionBtn.setText("已報名");
            actionBtn.setDisable(true);
        } else if (regService.isFull(event)) {
            actionBtn.setText("已額滿");
            actionBtn.setDisable(true);
        } else if (!EventStatusUtil.isRegistrationOpen(liveStatus)) {
            actionBtn.setText("無法報名");
            actionBtn.setDisable(true);
        } else {
            // 點擊「我要報名」引導至詳細頁進行報名
            actionBtn.setOnAction(e -> {
                e.consume();
                onDetail.accept(event);
            });
        }

        footerBox.getChildren().addAll(detailBtn, actionBtn);

        contentBox.getChildren().addAll(statusBadge, titleLabel, infoRow, capacityLabel, footerBox);
        card.getChildren().addAll(coverPane, contentBox);

        // 設定整張卡片點選皆可查看詳情
        card.setOnMouseClicked(e -> onDetail.accept(event));

        return card;
    }

    private static String getShortDate(String eventTime) {
        if (eventTime == null || eventTime.isBlank()) return "";
        try {
            String start = eventTime.contains("~") ? eventTime.split("~")[0].trim() : eventTime.trim();
            String datePart = start.split(" ")[0]; // "2026/06/20"
            String[] parts = datePart.contains("/") ? datePart.split("/") : datePart.split("-");
            if (parts.length >= 3) {
                return parts[1] + "/" + parts[2];
            }
            return datePart;
        } catch (Exception e) {
            return eventTime;
        }
    }
}
