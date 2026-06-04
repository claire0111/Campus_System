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

    // 儲存全域的「已報名活動」清單，供「我的報名」表格使用
    private final ObservableList<Event> registeredEvents = FXCollections.observableArrayList();
    
    // 紀錄目前登入的狀態與身分 (預設未登入)
    private boolean isLoggedIn = false;
    private String currentUserRole = ""; 

    // 全域宣告導覽列與主畫面的元件，方便登入成功後即時刷新介面
    private Hyperlink menu1;
    private Hyperlink menu2;
    private Hyperlink menu3;
    private Label tableTitle;
    private VBox centerArea;
    private VBox heroSearch;
    private TableView<Event> table;
    private TableView<Event> myRegisteredTable;
    private ObservableList<Event> rawData; // 拉成全域變數，方便編輯時兩邊表格同步更新

    // 活動資料模型（將欄位改為 Property 或普通欄位，這裡使用普通欄位但在編輯後需要 refresh 表格）
    public static class Event {
        private String name;
        private String location;
        private String regTime;
        private String eventTime;
        private String contact;
        private String status; // "尚未開始", "🟢 報名中", "⛔ 已結束"
        private String unit;
        private String content;

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
        public void setName(String name) { this.name = name; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getRegTime() { return regTime; }
        public void setRegTime(String regTime) { this.regTime = regTime; }

        public String getEventTime() { return eventTime; }
        public void setEventTime(String eventTime) { this.eventTime = eventTime; }

        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
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

        menu1 = new Hyperlink("活動列表");
        menu2 = new Hyperlink("我的報名");
        menu3 = new Hyperlink("登入");
        String menuStyle = "-fx-text-fill: #0a5338; -fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: false;";
        menu1.setStyle(menuStyle);
        menu2.setStyle(menuStyle);
        menu3.setStyle(menuStyle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); 
        
        HBox menuBox = new HBox(20, menu1, menu2, menu3);
        menuBox.setAlignment(Pos.CENTER_RIGHT);
        navbar.getChildren().addAll(brandSection, spacer, menuBox);

        // ================= SEARCH (搜尋區域) =================
        heroSearch = new VBox(15);
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

        // ================= 主活動列表表格 =================
        table = new TableView<>();
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
        table.getStylesheets().add(getClass().getResource("/style.css") != null ? 
            getClass().getResource("/style.css").toExternalForm() : "");

        javafx.scene.shape.Rectangle tableClip = new javafx.scene.shape.Rectangle();
        tableClip.setArcWidth(24);
        tableClip.setArcHeight(24);
        table.setClip(tableClip);
        table.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            tableClip.setWidth(newValue.getWidth());
            tableClip.setHeight(newValue.getHeight());
        });

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Event, String> colName = new TableColumn<>("活動名稱");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        colName.setResizable(false);
        colName.setReorderable(false);

        TableColumn<Event, String> colLocation = new TableColumn<>("活動地點");
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colLocation.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        colLocation.setResizable(false);
        colLocation.setReorderable(false);

        TableColumn<Event, String> colRegTime = new TableColumn<>("報名時間");
        colRegTime.setCellValueFactory(new PropertyValueFactory<>("regTime"));
        colRegTime.prefWidthProperty().bind(table.widthProperty().multiply(0.125));
        colRegTime.setResizable(false);
        colRegTime.setReorderable(false);

        TableColumn<Event, String> colEventTime = new TableColumn<>("活動時間");
        colEventTime.setCellValueFactory(new PropertyValueFactory<>("eventTime"));
        colEventTime.prefWidthProperty().bind(table.widthProperty().multiply(0.125));
        colEventTime.setResizable(false);
        colEventTime.setReorderable(false);

        TableColumn<Event, String> colContact = new TableColumn<>("連絡人");
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colContact.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        colContact.setResizable(false);
        colContact.setReorderable(false);

        TableColumn<Event, String> colStatus = new TableColumn<>("活動狀態");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        colStatus.setResizable(false);
        colStatus.setReorderable(false);

        TableColumn<Event, String> colAction = new TableColumn<>("操作");
        colAction.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        colAction.setResizable(false);
        colAction.setReorderable(false);
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
                                    if (!isLoggedIn) {
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("權限提示");
                                        alert.setHeaderText(null);
                                        alert.setContentText("報名活動前請先點擊右上角進行「登入」！");
                                        alert.showAndWait();
                                        showLoginDialog();
                                        return;
                                    }

                                    if ("教職員".equals(currentUserRole)) {
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("提示");
                                        alert.setHeaderText(null);
                                        alert.setContentText("目前為【教職員】權限，無法進行一般學生報名！");
                                        alert.showAndWait();
                                        return;
                                    }

                                    if (registeredEvents.contains(currentEvent)) {
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("提示");
                                        alert.setHeaderText(null);
                                        alert.setContentText("您已經報名過【" + currentEvent.getName() + "】活動囉！");
                                        alert.showAndWait();
                                        return;
                                    }

                                    registeredEvents.add(currentEvent);

                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("系統提示");
                                    alert.setHeaderText(null);
                                    alert.setContentText("您已成功送出活動【" + currentEvent.getName() + "】的報名申請！\n系統將自動引導您至查看清單。");
                                    alert.showAndWait();

                                    menu2.fire(); 
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

        table.getColumns().addAll(colName, colLocation, colRegTime, colEventTime, colContact, colStatus, colAction);
        rawData = loadEventsFromJson("C:/Users/user/Desktop/java_frontFX/JAVAFX/events.json");
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

        // ================= 我的報名表格 / 已管理表格共用樣式 =================
        myRegisteredTable = new TableView<>();
        myRegisteredTable.setStyle(
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
        myRegisteredTable.getStylesheets().add(getClass().getResource("/style.css") != null ? 
            getClass().getResource("/style.css").toExternalForm() : "");
            
        String myHeaderStyle = 
            ".table-view .column-header, .table-view .filler {" +
            "    -fx-background-color: #0a5338;" + 
            "    -fx-size: 40px;" +                
            "}" +
            ".table-view .column-header .label {" +
            "    -fx-text-fill: #ffffff;" +         
            "    -fx-font-weight: bold;" +
            "}";
        myRegisteredTable.getStylesheets().add("data:text/css," + myHeaderStyle.replaceAll(" ", "%20"));

        javafx.scene.shape.Rectangle myTableClip = new javafx.scene.shape.Rectangle();
        myTableClip.setArcWidth(24);
        myTableClip.setArcHeight(24);
        myRegisteredTable.setClip(myTableClip);
        myRegisteredTable.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            myTableClip.setWidth(newValue.getWidth());
            myTableClip.setHeight(newValue.getHeight());
        });

        myRegisteredTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Event, String> myColName = new TableColumn<>("活動名稱");
        myColName.setCellValueFactory(new PropertyValueFactory<>("name"));
        myColName.prefWidthProperty().bind(myRegisteredTable.widthProperty().multiply(0.35));
        myColName.setResizable(false);
        myColName.setReorderable(false);

        TableColumn<Event, String> myColLocation = new TableColumn<>("活動地點");
        myColLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        myColLocation.prefWidthProperty().bind(myRegisteredTable.widthProperty().multiply(0.20));
        myColLocation.setResizable(false);
        myColLocation.setReorderable(false);

        TableColumn<Event, String> myColTime = new TableColumn<>("活動時間");
        myColTime.setCellValueFactory(new PropertyValueFactory<>("eventTime"));
        myColTime.prefWidthProperty().bind(myRegisteredTable.widthProperty().multiply(0.20));
        myColTime.setResizable(false);
        myColTime.setReorderable(false);

       
        TableColumn<Event, String> myColAction = new TableColumn<>("操作");
        myColAction.prefWidthProperty().bind(myRegisteredTable.widthProperty().multiply(0.25));
        myColAction.setResizable(false);
        myColAction.setReorderable(false);
        myColAction.setCellFactory(param -> new TableCell<Event, String>() {
        
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                }
            }
        });

        myRegisteredTable.getColumns().addAll(myColName, myColLocation, myColTime, myColAction);

        // ================= 版面與切換邏輯 =================
        tableTitle = new Label("活動列表");
        tableTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 10 0 5 0; -fx-text-fill: #1e293b;");
        tableTitle.setMaxWidth(Double.MAX_VALUE);
        tableTitle.setAlignment(Pos.CENTER);

        centerArea = new VBox(12, heroSearch, tableTitle, table);
        centerArea.setStyle("-fx-padding: 15; -fx-background-color: #ffffff;");
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(myRegisteredTable, Priority.ALWAYS);

        menu1.setOnAction(e -> {
            tableTitle.setText("活動列表");
            centerArea.getChildren().setAll(heroSearch, tableTitle, table);
        });

        menu2.setOnAction(e -> {
            if (!isLoggedIn) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "查看內容前請先登入系統！");
                alert.setHeaderText(null);
                alert.showAndWait();
                showLoginDialog();
                return;
            }
            
            if ("教職員".equals(currentUserRole)) {
                tableTitle.setText("已管理的活動清單 (目前權限：教職員)");
                myRegisteredTable.setItems(rawData); 
                myColName.setText("管理活動名稱");
            } else {
                tableTitle.setText("我的報名清單 (目前權限：學生)");
                myRegisteredTable.setItems(registeredEvents); 
                myColName.setText("已報名活動名稱");
            }
            centerArea.getChildren().setAll(tableTitle, myRegisteredTable);
        });

        menu3.setOnAction(e -> {
            if (isLoggedIn) {
                isLoggedIn = false;
                currentUserRole = "";
                menu3.setText("登入");
                menu2.setText("我的報名");
                registeredEvents.clear(); 
                menu1.fire(); 
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "您已成功安全登出！");
                alert.setHeaderText(null);
                alert.showAndWait();
            } else {
                showLoginDialog();
            }
        });

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #ffffff;"); 
        root.getChildren().addAll(navbar, centerArea);
        VBox.setVgrow(centerArea, Priority.ALWAYS);

        Scene scene = new Scene(root, 1050, 700);
        stage.setTitle("雲科大校園活動管理系統 - JavaFX 版本");
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

    private void showLoginDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("校園活動管理系統 - 安全登入");

        ButtonType closeButtonType = new ButtonType("關閉", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-padding: 40; " +
            "-fx-pref-width: 520px; " + 
            "-fx-background-radius: 20px;"
        );

        Label titleLabel = new Label("使用者登入");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0a5338;");

        Label subTitleLabel = new Label("請選擇您的身分");
        subTitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        String roleButtonCss = 
            ".role-button {" +
            "    -fx-background-color: #ffffff;" +
            "    -fx-border-color: #000000;" +
            "    -fx-border-width: 2px;" +
            "    -fx-border-radius: 10px;" +
            "    -fx-background-radius: 10px;" +
            "    -fx-text-fill: #000000;" +
            "    -fx-font-size: 16px;" +
            "    -fx-font-weight: bold;" +
            "    -fx-padding: 20 0;" + 
            "    -fx-cursor: hand;" +
            "}" +
            ".role-button:hover {" +
            "    -fx-background-color: #0a5338;" +
            "    -fx-border-color: #0a5338;" +
            "    -fx-text-fill: #ffffff;" +
            "}" +
            ".role-button:focused {" +
            "    -fx-background-color: #0a5338;" +
            "    -fx-border-color: #0a5338;" +
            "    -fx-text-fill: #ffffff;" +
            "}";

        Button facultyBtn = new Button("💼 教職員登入");
        Button studentBtn = new Button("🎓 學生登入");

        facultyBtn.getStyleClass().add("role-button");
        studentBtn.getStyleClass().add("role-button");

        facultyBtn.setMaxWidth(Double.MAX_VALUE);
        studentBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(facultyBtn, Priority.ALWAYS);
        HBox.setHgrow(studentBtn, Priority.ALWAYS);

        HBox buttonBox = new HBox(20); 
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(facultyBtn, studentBtn);
        
        buttonBox.getStylesheets().add("data:text/css," + roleButtonCss.replaceAll(" ", "%20"));

        container.getChildren().addAll(titleLabel, subTitleLabel, buttonBox);

        facultyBtn.setOnAction(e -> createLoginForm(container, "教職員"));
        studentBtn.setOnAction(e -> createLoginForm(container, "學生"));

        dialog.getDialogPane().setContent(container);
        dialog.setHeaderText(null); 
        dialog.setGraphic(null);

        dialog.showAndWait();
    }

    private void createLoginForm(VBox container, String role) {
        container.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-padding: 40; " +
            "-fx-pref-width: 460px; " + 
            "-fx-background-radius: 20px;"
        );
        container.getChildren().clear();
        container.setSpacing(20);

        Label formTitle = new Label(role + " 登入");
        formTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0a5338;");

        VBox accountGroup = new VBox(8);
        Label accountLabel = new Label("帳號");
        accountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151; -fx-font-weight: bold;");
        TextField accountField = new TextField();
        accountField.setPromptText("請輸入學號/帳號");
        accountField.setStyle("-fx-padding: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-size: 14px;");
        accountGroup.getChildren().addAll(accountLabel, accountField);

        VBox passwordGroup = new VBox(8);
        Label passwordLabel = new Label("密碼");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151; -fx-font-weight: bold;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("請輸入密碼");
        passwordField.setStyle("-fx-padding: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-size: 14px;");
        passwordGroup.getChildren().addAll(passwordLabel, passwordField);

        accountGroup.setMaxWidth(Double.MAX_VALUE);
        passwordGroup.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(accountGroup, Priority.ALWAYS);
        HBox.setHgrow(passwordGroup, Priority.ALWAYS);

        HBox inputRow = new HBox(15);
        inputRow.setAlignment(Pos.CENTER);
        inputRow.getChildren().addAll(accountGroup, passwordGroup);

        Button submitBtn = new Button("確認登入");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setStyle(
            "-fx-background-color: #009cf7; " + 
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 12 0; " +
            "-fx-cursor: hand;"
        );

        Hyperlink backLink = new Hyperlink("← 返回選擇身分");
        backLink.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-underline: false;");
        HBox backLinkBox = new HBox(backLink);
        backLinkBox.setAlignment(Pos.CENTER);

        backLink.setOnAction(e -> {
            container.getChildren().clear();
            container.setStyle(
                "-fx-background-color: #ffffff; " +
                "-fx-padding: 40; " +
                "-fx-pref-width: 520px; " + 
                "-fx-background-radius: 20px;"
            );
            showLoginDialog(); 
        });

        submitBtn.setOnAction(e -> {
            String username = accountField.getText().trim();
            String password = passwordField.getText();

            boolean isValid = false;

            if ("學生".equals(role)) {
                if (("stu".equals(username) && "111".equals(password))) {
                    isValid = true;
                }
            } else if ("教職員".equals(role)) {
                if (("tea".equals(username) && "222".equals(password))) {
                    isValid = true;
                }
            }

            if (isValid) {
                isLoggedIn = true;
                currentUserRole = role;

                menu3.setText("登出 (" + currentUserRole + ")");
                
                if ("教職員".equals(currentUserRole)) {
                    menu2.setText("已管理清單");
                } else {
                    menu2.setText("我的報名");
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("登入成功");
                alert.setHeaderText(null);
                alert.setContentText("歡迎回來，" + role + " " + username + "！");
                alert.showAndWait();
                
                Stage stage = (Stage) container.getScene().getWindow();
                stage.close();

                menu2.fire();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("登入失敗");
                alert.setHeaderText(null);
                if ("學生".equals(role)) {
                    alert.setContentText("帳號或密碼錯誤，請重新輸入！\n");
                } else {
                    alert.setContentText("帳號或密碼錯誤，請重新輸入！\n");
                }
                alert.showAndWait();
            }
        });

        container.getChildren().addAll(formTitle, inputRow, submitBtn, backLinkBox);
        
        if (container.getScene() != null && container.getScene().getWindow() != null) {
            container.getScene().getWindow().sizeToScene();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}