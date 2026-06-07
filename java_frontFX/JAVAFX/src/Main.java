import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
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
import service.EventService;
import service.LoginService;
import service.RegistrationService;
import view.MainView;

// 引入 JSON 庫
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Main extends Application {

    // UI 元件
    // 全域宣告導覽列與主畫面的元件，方便登入成功後即時刷新介面
    private Hyperlink menu1;
    private Hyperlink menu2;
    private Hyperlink menu3;
    private Label tableTitle;
    private VBox centerArea;
    private VBox heroSearch;
    private TableView<Event> table;
    private TableView<Event> myRegisteredTable;

    @Override
    public void start(Stage stage) {

        // 建立 Service
        LoginService loginService = new LoginService();
        EventService eventService = new EventService();
        RegistrationService regService = new RegistrationService();

        // 載入資料
        eventService.loadEvents("data/activity sheet.csv");

        // 建立 View 
        MainView mainView = new MainView(
                eventService,
                loginService,
                regService
        );

        // 建立 UI
        VBox root = new VBox();
        Scene scene = new Scene(root, 1050, 700);
        stage.setTitle("校園活動管理系統");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }


    // @Override
    // @SuppressWarnings("unchecked")
    // public void start(Stage stage) {

    //     // ================= 版面與切換邏輯 =================
    //     tableTitle = new Label("活動列表");
    //     tableTitle
    //             .setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 10 0 5 0; -fx-text-fill: #1e293b;");
    //     tableTitle.setMaxWidth(Double.MAX_VALUE);
    //     tableTitle.setAlignment(Pos.CENTER);

    //     centerArea = new VBox(12, heroSearch, tableTitle, table);
    //     centerArea.setStyle("-fx-padding: 15; -fx-background-color: #ffffff;");
    //     VBox.setVgrow(table, Priority.ALWAYS);
    //     VBox.setVgrow(myRegisteredTable, Priority.ALWAYS);

    //     menu1.setOnAction(e -> {
    //         tableTitle.setText("活動列表");
    //         centerArea.getChildren().setAll(heroSearch, tableTitle, table);
    //     });

    //     menu2.setOnAction(e -> {
    //         if (!isLoggedIn) {
    //             Alert alert = new Alert(Alert.AlertType.WARNING, "查看內容前請先登入系統！");
    //             alert.setHeaderText(null);
    //             alert.showAndWait();
    //             showLoginDialog();
    //             return;
    //         }

    //         if ("教職員".equals(currentUserRole)) {
    //             tableTitle.setText("已管理的活動清單 (目前權限：教職員)");
    //             myRegisteredTable.setItems(rawData);
    //             myColName.setText("管理活動名稱");
    //         } else {
    //             tableTitle.setText("我的報名清單 (目前權限：學生)");
    //             myRegisteredTable.setItems(registeredEvents);
    //             myColName.setText("已報名活動名稱");
    //         }
    //         centerArea.getChildren().setAll(tableTitle, myRegisteredTable);
    //     });

    //     menu3.setOnAction(e -> {
    //         if (isLoggedIn) {
    //             isLoggedIn = false;
    //             currentUserRole = "";
    //             menu3.setText("登入");
    //             menu2.setText("我的報名");
    //             registeredEvents.clear();
    //             menu1.fire();
    //             Alert alert = new Alert(Alert.AlertType.INFORMATION, "您已成功安全登出！");
    //             alert.setHeaderText(null);
    //             alert.showAndWait();
    //         } else {
    //             showLoginDialog();
    //         }
    //     });

    //     VBox root = new VBox();
    //     root.setStyle("-fx-background-color: #ffffff;");
    //     root.getChildren().addAll(navbar, centerArea);
    //     VBox.setVgrow(centerArea, Priority.ALWAYS);

    //     Scene scene = new Scene(root, 1050, 700);
    //     stage.setTitle("雲科大校園活動管理系統 - JavaFX 版本");
    //     stage.setScene(scene);
    //     stage.show();
    // }

    // private ObservableList<Event> loadEventsFromJson(String filePath) {
    //     ObservableList<Event> list = FXCollections.observableArrayList();
    //     try {
    //         String content = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
    //         JSONArray jsonArray = new JSONArray(content);
    //         LocalDateTime now = LocalDateTime.now();

    //         for (int i = 0; i < jsonArray.length(); i++) {
    //             JSONObject obj = jsonArray.getJSONObject(i);
    //             String name = obj.optString("activity_name", "未命名活動");
    //             String location = obj.optString("location", "未指定");
    //             String regTime = obj.optString("registration_time", "");
    //             String eventTime = obj.optString("activity_time", "");
    //             String contact = obj.optString("contact_person", "無");
    //             String unit = obj.optString("data_unit", "無");
    //             String detailContent = obj.optString("data_content", "暫無詳細內容。");
    //             String startStr = obj.optString("data_start", "");
    //             String endStr = obj.optString("data_end", "");

    //             String status = "🟢 報名中";
    //             try {
    //                 if (!startStr.isEmpty()) {
    //                     LocalDateTime startDate = LocalDateTime.parse(startStr);
    //                     if (now.isBefore(startDate)) {
    //                         status = "尚未開始";
    //                     }
    //                 }
    //                 if (!endStr.isEmpty()) {
    //                     LocalDateTime endDate = LocalDateTime.parse(endStr);
    //                     if (now.isAfter(endDate)) {
    //                         status = "⛔ 已結束";
    //                     }
    //                 }
    //             } catch (Exception e) {
    //             }

    //             list.add(new Event(name, location, regTime, eventTime, contact, status, unit, detailContent));
    //         }
    //         System.out.println("成功載入 " + list.size() + " 筆 JSON 活動資料！");
    //     } catch (Exception e) {
    //         System.out.println("讀取 JSON 檔案失敗，請確認檔案路徑是否正確：" + e.getMessage());
    //         list.add(new Event("資料載入失敗", "請檢查正確的 events.json 檔案路徑", "", "", "", "❌ 錯誤", "", ""));
    //     }
    //     return list;
    // }

    // private void showEventDetailDialog(Event event) {
    //     Alert alert = new Alert(Alert.AlertType.INFORMATION);
    //     alert.setTitle("活動詳細資訊與報名");
    //     alert.setHeaderText(event.getName());

    //     TextArea textArea = new TextArea(event.getContent());
    //     textArea.setEditable(false);
    //     textArea.setWrapText(true);
    //     textArea.setPrefHeight(200);
    //     VBox contentBox = new VBox(8);
    //     contentBox.getChildren().addAll(
    //             new Label("📍 活動地點：" + event.getLocation()),
    //             new Label("🏢 主辦單位：" + event.getUnit()),
    //             new Label("⏰ 活動期間：" + event.getEventTime()),
    //             new Label("📝 報名期間：" + event.getRegTime()),
    //             new Label("👤 聯絡窗口：" + event.getContact()),
    //             new Label("📊 目前狀態：" + event.getStatus()),
    //             new Label("\n📋 詳細活動內容："),
    //             textArea);
    //     alert.getDialogPane().setContent(contentBox);
    //     alert.getDialogPane().setMinWidth(550);
    //     alert.showAndWait();
    // }

    // private void showLoginDialog() {
    //     Dialog<Void> dialog = new Dialog<>();
    //     dialog.setTitle("校園活動管理系統 - 安全登入");

    //     ButtonType closeButtonType = new ButtonType("關閉", ButtonBar.ButtonData.CANCEL_CLOSE);
    //     dialog.getDialogPane().getButtonTypes().add(closeButtonType);

    //     VBox container = new VBox(25);
    //     container.setAlignment(Pos.CENTER);
    //     container.setStyle(
    //             "-fx-background-color: #ffffff; " +
    //                     "-fx-padding: 40; " +
    //                     "-fx-pref-width: 520px; " +
    //                     "-fx-background-radius: 20px;");

    //     Label titleLabel = new Label("使用者登入");
    //     titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0a5338;");

    //     Label subTitleLabel = new Label("請選擇您的身分");
    //     subTitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

    //     String roleButtonCss = ".role-button {" +
    //             "    -fx-background-color: #ffffff;" +
    //             "    -fx-border-color: #000000;" +
    //             "    -fx-border-width: 2px;" +
    //             "    -fx-border-radius: 10px;" +
    //             "    -fx-background-radius: 10px;" +
    //             "    -fx-text-fill: #000000;" +
    //             "    -fx-font-size: 16px;" +
    //             "    -fx-font-weight: bold;" +
    //             "    -fx-padding: 20 0;" +
    //             "    -fx-cursor: hand;" +
    //             "}" +
    //             ".role-button:hover {" +
    //             "    -fx-background-color: #0a5338;" +
    //             "    -fx-border-color: #0a5338;" +
    //             "    -fx-text-fill: #ffffff;" +
    //             "}" +
    //             ".role-button:focused {" +
    //             "    -fx-background-color: #0a5338;" +
    //             "    -fx-border-color: #0a5338;" +
    //             "    -fx-text-fill: #ffffff;" +
    //             "}";

    //     Button facultyBtn = new Button("💼 教職員登入");
    //     Button studentBtn = new Button("🎓 學生登入");

    //     facultyBtn.getStyleClass().add("role-button");
    //     studentBtn.getStyleClass().add("role-button");

    //     facultyBtn.setMaxWidth(Double.MAX_VALUE);
    //     studentBtn.setMaxWidth(Double.MAX_VALUE);
    //     HBox.setHgrow(facultyBtn, Priority.ALWAYS);
    //     HBox.setHgrow(studentBtn, Priority.ALWAYS);

    //     HBox buttonBox = new HBox(20);
    //     buttonBox.setAlignment(Pos.CENTER);
    //     buttonBox.getChildren().addAll(facultyBtn, studentBtn);

    //     buttonBox.getStylesheets().add("data:text/css," + roleButtonCss.replaceAll(" ", "%20"));

    //     container.getChildren().addAll(titleLabel, subTitleLabel, buttonBox);

    //     facultyBtn.setOnAction(e -> createLoginForm(container, "教職員"));
    //     studentBtn.setOnAction(e -> createLoginForm(container, "學生"));

    //     dialog.getDialogPane().setContent(container);
    //     dialog.setHeaderText(null);
    //     dialog.setGraphic(null);

    //     dialog.showAndWait();
    // }

    // private void createLoginForm(VBox container, String role) {
    //     container.setStyle(
    //             "-fx-background-color: #ffffff; " +
    //                     "-fx-padding: 40; " +
    //                     "-fx-pref-width: 460px; " +
    //                     "-fx-background-radius: 20px;");
    //     container.getChildren().clear();
    //     container.setSpacing(20);

    //     Label formTitle = new Label(role + " 登入");
    //     formTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0a5338;");

    //     VBox accountGroup = new VBox(8);
    //     Label accountLabel = new Label("帳號");
    //     accountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151; -fx-font-weight: bold;");
    //     TextField accountField = new TextField();
    //     accountField.setPromptText("請輸入學號/帳號");
    //     accountField.setStyle(
    //             "-fx-padding: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-size: 14px;");
    //     accountGroup.getChildren().addAll(accountLabel, accountField);

    //     VBox passwordGroup = new VBox(8);
    //     Label passwordLabel = new Label("密碼");
    //     passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151; -fx-font-weight: bold;");
    //     PasswordField passwordField = new PasswordField();
    //     passwordField.setPromptText("請輸入密碼");
    //     passwordField.setStyle(
    //             "-fx-padding: 12; -fx-border-color: #cbd5e1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-size: 14px;");
    //     passwordGroup.getChildren().addAll(passwordLabel, passwordField);

    //     accountGroup.setMaxWidth(Double.MAX_VALUE);
    //     passwordGroup.setMaxWidth(Double.MAX_VALUE);
    //     HBox.setHgrow(accountGroup, Priority.ALWAYS);
    //     HBox.setHgrow(passwordGroup, Priority.ALWAYS);

    //     HBox inputRow = new HBox(15);
    //     inputRow.setAlignment(Pos.CENTER);
    //     inputRow.getChildren().addAll(accountGroup, passwordGroup);

    //     Button submitBtn = new Button("確認登入");
    //     submitBtn.setMaxWidth(Double.MAX_VALUE);
    //     submitBtn.setStyle(
    //             "-fx-background-color: #009cf7; " +
    //                     "-fx-text-fill: white; " +
    //                     "-fx-font-size: 16px; " +
    //                     "-fx-font-weight: bold; " +
    //                     "-fx-background-radius: 10px; " +
    //                     "-fx-padding: 12 0; " +
    //                     "-fx-cursor: hand;");

    //     Hyperlink backLink = new Hyperlink("← 返回選擇身分");
    //     backLink.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-underline: false;");
    //     HBox backLinkBox = new HBox(backLink);
    //     backLinkBox.setAlignment(Pos.CENTER);

    //     backLink.setOnAction(e -> {
    //         container.getChildren().clear();
    //         container.setStyle(
    //                 "-fx-background-color: #ffffff; " +
    //                         "-fx-padding: 40; " +
    //                         "-fx-pref-width: 520px; " +
    //                         "-fx-background-radius: 20px;");
    //         showLoginDialog();
    //     });

    //     submitBtn.setOnAction(e -> {
    //         String username = accountField.getText().trim();
    //         String password = passwordField.getText();

    //         boolean isValid = false;

    //         if ("學生".equals(role)) {
    //             if (("stu".equals(username) && "111".equals(password))) {
    //                 isValid = true;
    //             }
    //         } else if ("教職員".equals(role)) {
    //             if (("tea".equals(username) && "222".equals(password))) {
    //                 isValid = true;
    //             }
    //         }

    //         if (isValid) {
    //             isLoggedIn = true;
    //             currentUserRole = role;

    //             menu3.setText("登出 (" + currentUserRole + ")");

    //             if ("教職員".equals(currentUserRole)) {
    //                 menu2.setText("已管理清單");
    //             } else {
    //                 menu2.setText("我的報名");
    //             }

    //             Alert alert = new Alert(Alert.AlertType.INFORMATION);
    //             alert.setTitle("登入成功");
    //             alert.setHeaderText(null);
    //             alert.setContentText("歡迎回來，" + role + " " + username + "！");
    //             alert.showAndWait();

    //             Stage stage = (Stage) container.getScene().getWindow();
    //             stage.close();

    //             menu2.fire();

    //         } else {
    //             Alert alert = new Alert(Alert.AlertType.ERROR);
    //             alert.setTitle("登入失敗");
    //             alert.setHeaderText(null);
    //             if ("學生".equals(role)) {
    //                 alert.setContentText("帳號或密碼錯誤，請重新輸入！\n");
    //             } else {
    //                 alert.setContentText("帳號或密碼錯誤，請重新輸入！\n");
    //             }
    //             alert.showAndWait();
    //         }
    //     });

    //     container.getChildren().addAll(formTitle, inputRow, submitBtn, backLinkBox);

    //     if (container.getScene() != null && container.getScene().getWindow() != null) {
    //         container.getScene().getWindow().sizeToScene();
    //     }
    // }

}