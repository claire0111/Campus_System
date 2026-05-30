import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;

// 引入 JSON 庫
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Main extends Application {

    // 1. 更新活動資料模型
    public static class Event {
        private final String name;
        private final String location;
        private final String regTime;
        private final String eventTime;
        private final String contact;
        private final String status; // "尚未開始", "🟢 報名中", "⛔ 已結束"
        private final String unit;
        private final String content;

        public Event(String name, String location, String regTime, String eventTime, String contact, String status, String unit, String content) {
            this.name = name;
            this.location = location;
            this.regTime = regTime;
            this.eventTime = eventTime;
            this.contact = contact;
            this.status = status;
            this.unit = unit;
            this.content = content;
        }

        public String getName() { return name; }
        public String getLocation() { return location; }
        public String getRegTime() { return regTime; }
        public String getEventTime() { return eventTime; }
        public String getContact() { return contact; }
        public String getStatus() { return status; }
        public String getUnit() { return unit; }
        public String getContent() { return content; }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage stage) {

        // ================= NAVBAR (導覽列) =================
        HBox navbar = new HBox(25);
        navbar.setStyle("-fx-background-color: rgb(214,249,214); -fx-padding: 10 20; -fx-alignment: center-left; -fx-min-height: 70px;");

        HBox brandSection = new HBox(12);
        brandSection.setAlignment(Pos.CENTER_LEFT);
        try {
            String imagePath = getClass().getResource("/images/yuntech.png").toExternalForm();
            Image logoImage = new Image(imagePath);
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitWidth(45);
            logoView.setFitHeight(45);
            
            Circle clipCircle = new Circle(22.5, 22.5, 22.5);
            logoView.setClip(clipCircle);
            brandSection.getChildren().add(logoView);
        } catch (Exception e) {
            System.out.println("提示：未讀取到 images/yuntech.png 圖片，改為純文字顯示。");
        }

        Label brandLabel = new Label("校園活動管理系統");
        brandLabel.setStyle("-fx-font-size: 26px; -fx-text-fill: rgb(8,100,60); -fx-font-weight: bold;");
        brandSection.getChildren().add(brandLabel);

        Hyperlink menu1 = new Hyperlink("活動列表");
        Hyperlink menu2 = new Hyperlink("我的報名");
        Hyperlink menu3 = new Hyperlink("登入");
        String menuStyle = "-fx-text-fill: #0a5338; -fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: false;";
        menu1.setStyle(menuStyle);
        menu2.setStyle(menuStyle);
        menu3.setStyle(menuStyle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); 
        
        HBox menuBox = new HBox(20, menu1, menu2, menu3);
        menuBox.setAlignment(Pos.CENTER_RIGHT);
        navbar.getChildren().addAll(brandSection, spacer, menuBox);

        // ================= HERO SEARCH (搜尋區域) =================
        VBox heroSearch = new VBox(15);
        heroSearch.setStyle("-fx-background-color: #ffffff; -fx-padding: 30; -fx-alignment: center;");

        Label searchTitle = new Label("活動查詢");
        searchTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        TextField searchField = new TextField();
        searchField.setPromptText("搜尋活動名稱 / 地點");
        searchField.setPrefWidth(350);
        searchField.setStyle("-fx-padding: 12; -fx-background-radius: 25; -fx-border-radius: 25; -fx-border-color: #cbd5e1; -fx-font-size: 14px;");
        Button searchBtn = new Button("🔍 搜尋");
        searchBtn.setStyle("-fx-background-color: #009cf7; -fx-text-fill: white; -fx-background-radius: 25; -fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand;");
        HBox searchForm = new HBox(12, searchField, searchBtn);
        searchForm.setAlignment(Pos.CENTER);
        heroSearch.getChildren().addAll(searchTitle, searchForm);

        // ================= TABLE VIEW (活動列表表格) =================
        TableView<Event> table = new TableView<>();
        
        // 1. 將你的網頁 CSS 完整轉換為 JavaFX 樣式
        table.setStyle(
            "-fx-background-color: transparent; " +            
            "-fx-control-inner-background: #ffffff; " +        
            "-fx-control-inner-background-alt: #ffffff; " +    
            "-fx-table-cell-border-color: #ddd; " +            
            "-fx-padding: 0; " +
            "-fx-background-radius: 12px; " +                  
            "-fx-border-radius: 12px; " +
            "-fx-border-width: 1px; " +
            "-fx-border-color: #ddd; " +                     
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2); " + 
            "-fx-font-size: 14px;"
        );

        // 2. 利用 JavaFX 內建的 CSS 選擇器，將「表頭」改成深綠色、文字變白色
        table.getStylesheets().add(getClass().getResource("/style.css") != null ? 
            getClass().getResource("/style.css").toExternalForm() : "");

        // 最完美的作法是直接用代碼對 table 進行修剪(Clip)，這樣不管是第一列還是整個表格，都會是完美的圓角！
        javafx.scene.shape.Rectangle tableClip = new javafx.scene.shape.Rectangle();
        tableClip.setArcWidth(24);  // 圓角弧度
        tableClip.setArcHeight(24);
        table.setClip(tableClip);

        // 讓圓角遮罩隨著表格大小動態縮放
        table.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            tableClip.setWidth(newValue.getWidth());
            tableClip.setHeight(newValue.getHeight());
        });

        // ⭐ 關鍵設定 1：對應 width: 100% 與 table-layout: fixed
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Event, String> colName = new TableColumn<>("活動名稱");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        // ⭐ 關鍵設定 2：對應 nth-child(1) { width: 25%; }
        colName.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        colName.setResizable(false);
        colName.setReorderable(false);

        TableColumn<Event, String> colLocation = new TableColumn<>("活動地點");
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        // ⭐ 關鍵設定 3：對應 nth-child(2) { width: 20%; }
        colLocation.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        colLocation.setResizable(false);
        colLocation.setReorderable(false);

        TableColumn<Event, String> colRegTime = new TableColumn<>("報名時間");
        colRegTime.setCellValueFactory(new PropertyValueFactory<>("regTime"));
        // 剩餘空間平分（第3欄）：12.5% -> 0.125
        colRegTime.prefWidthProperty().bind(table.widthProperty().multiply(0.125));
        colRegTime.setResizable(false);
        colRegTime.setReorderable(false);

        TableColumn<Event, String> colEventTime = new TableColumn<>("活動時間");
        colEventTime.setCellValueFactory(new PropertyValueFactory<>("eventTime"));
        // 剩餘空間平分（第4欄）：12.5% -> 0.125
        colEventTime.prefWidthProperty().bind(table.widthProperty().multiply(0.125));
        colEventTime.setResizable(false);
        colEventTime.setReorderable(false);

        TableColumn<Event, String> colContact = new TableColumn<>("連絡人");
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        // ⭐ 關鍵設定 4：對應 nth-child(5) { width: 10%; }
        colContact.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        colContact.setResizable(false);
        colContact.setReorderable(false);

        TableColumn<Event, String> colStatus = new TableColumn<>("活動狀態");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        // ⭐ 關鍵設定 5：對應 nth-child(6) { width: 10%; }
        colStatus.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        colStatus.setResizable(false);
        colStatus.setReorderable(false);

        TableColumn<Event, String> colAction = new TableColumn<>("操作");
        // ⭐ 關鍵設定 6：對應 nth-child(7) { width: 10%; }
        colAction.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        colAction.setResizable(false);
        colAction.setReorderable(false);
        
        // 設定操作欄位的自訂渲染工廠
        colAction.setCellFactory(new Callback<TableColumn<Event, String>, TableCell<Event, String>>() {
            @Override
            public TableCell<Event, String> call(TableColumn<Event, String> param) {
                return new TableCell<Event, String>() {
                    private final Button btn = new Button();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Event currentEvent = getTableView().getItems().get(getIndex());
                            String status = currentEvent.getStatus();

                            if ("尚未開始".equals(status)) {
                                btn.setText("未開始");
                                btn.setDisable(true);
                                btn.setStyle("-fx-background-color: #94a3b8; -fx-text-fill: white; -fx-background-radius: 5;");
                            } else if ("⛔ 已結束".equals(status)) {
                                btn.setText("已結束");
                                btn.setDisable(true);
                                btn.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: #64748b; -fx-background-radius: 5;");
                            } else {
                                btn.setText("報名");
                                btn.setDisable(false);
                                btn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 15;");
                                btn.setOnAction(e -> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("系統提示");
                                    alert.setHeaderText(null);
                                    alert.setContentText("您已成功送出活動【" + currentEvent.getName() + "】的報名申請！");
                                    alert.showAndWait();
                                });
                            }
                            
                            HBox container = new HBox(btn);
                            container.setAlignment(Pos.CENTER);
                            setGraphic(container);
                        }
                    }
                };
            }
        });

        // 欄位設定完後，直接加進 TableView
        table.getColumns().addAll(colName, colLocation, colRegTime, colEventTime, colContact, colStatus, colAction);

        // 外部 JSON 檔案路徑
        ObservableList<Event> rawData = loadEventsFromJson("C:/Users/user/Desktop/java_frontFX/JAVAFX/events.json");
        FilteredList<Event> filteredData = new FilteredList<>(rawData, p -> true);
        searchBtn.setOnAction(e -> {
            String searchStr = searchField.getText().trim().toLowerCase();
            filteredData.setPredicate(event -> {
                if (searchStr.isEmpty()) return true;
                return event.getName().toLowerCase().contains(searchStr) || 
                       event.getLocation().toLowerCase().contains(searchStr);
            });
        });
        searchField.setOnAction(e -> searchBtn.fire());
        table.setItems(filteredData);

        // ================= INTERACTION =================
        table.setRowFactory(tv -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Event rowData = row.getItem();
                    showEventDetailDialog(rowData);
                }
            });
            return row;
        });

        // ================= 標題與版面修正 =================
        // 修正：將字體放大至 22px（與活動查詢相同），並設定最大寬度以實現文字真正置中
        Label tableTitle = new Label("活動列表");
        tableTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 10 0 5 0; -fx-text-fill: #1e293b;");
        tableTitle.setMaxWidth(Double.MAX_VALUE); // 讓 Label 撐滿整行
        tableTitle.setAlignment(Pos.CENTER);      // 文字靠中央置中

        // 核心修正一：設定中間內容區域為純白色背景
        VBox centerArea = new VBox(12, heroSearch, tableTitle, table);
        centerArea.setStyle("-fx-padding: 15; -fx-background-color: #ffffff;");
        
        // 核心修正二：徹底讓出底部所有位置，讓表格（table）擁有最大垂直增長權限
        VBox.setVgrow(table, Priority.ALWAYS); 

        // 核心修正三：將最底層的 root 容器背景也強制改為純白色，並移除所有預設雜色
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #ffffff;"); 
        root.getChildren().addAll(navbar, centerArea);
        
        // 讓根容器垂直拉伸以適應整個視窗大小
        VBox.setVgrow(centerArea, Priority.ALWAYS);

        Scene scene = new Scene(root, 1050, 700);

        stage.setTitle("雲科大校園活動管理系統 - JavaFX JSON版");
        stage.setScene(scene);
        stage.show();
    }

    private ObservableList<Event> loadEventsFromJson(String filePath) {
        ObservableList<Event> list = FXCollections.observableArrayList();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
            JSONArray jsonArray = new JSONArray(content);
            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.optString("activity_name", "未命名活動");
                String location = obj.optString("location", "未指定");
                String regTime = obj.optString("registration_time", "");
                String eventTime = obj.optString("activity_time", "");
                String contact = obj.optString("contact_person", "無");
                String unit = obj.optString("data_unit", "無");
                String detailContent = obj.optString("data_content", "暫無詳細內容。");
                String startStr = obj.optString("data_start", "");
                String endStr = obj.optString("data_end", "");

                String status = "🟢 報名中";
                
                try {
                    if (!startStr.isEmpty()) {
                        LocalDateTime startDate = LocalDateTime.parse(startStr);
                        if (now.isBefore(startDate)) {
                            status = "尚未開始";
                        }
                    }
                    if (!endStr.isEmpty()) {
                        LocalDateTime endDate = LocalDateTime.parse(endStr);
                        if (now.isAfter(endDate)) {
                            status = "⛔ 已結束";
                        }
                    }
                } catch (Exception e) {
                    // 若時間格式解析錯誤，維持預設
                }

                list.add(new Event(name, location, regTime, eventTime, contact, status, unit, detailContent));
            }
            System.out.println("成功載入 " + list.size() + " 筆 JSON 活動資料！");
        } catch (Exception e) {
            System.out.println("讀取 JSON 檔案失敗，請確認檔案路徑是否正確：" + e.getMessage());
            list.add(new Event("資料載入失敗", "請檢查正確的 events.json 檔案路徑", "", "", "", "❌ 錯誤", "", ""));
        }
        return list;
    }

    private void showEventDetailDialog(Event event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("活動詳細資訊與報名");
        alert.setHeaderText(event.getName());
        
        TextArea textArea = new TextArea(event.getContent());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(200);
        VBox contentBox = new VBox(8);
        contentBox.getChildren().addAll(
            new Label("📍 活動地點：" + event.getLocation()),
            new Label("🏢 主辦單位：" + event.getUnit()),
            new Label("⏰ 活動期間：" + event.getEventTime()),
            new Label("📝 報名期間：" + event.getRegTime()),
            new Label("👤 聯絡窗口：" + event.getContact()),
            new Label("📊 目前狀態：" + event.getStatus()),
            new Label("\n📋 詳細活動內容："),
            textArea
        );
        alert.getDialogPane().setContent(contentBox);
        alert.getDialogPane().setMinWidth(550);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}