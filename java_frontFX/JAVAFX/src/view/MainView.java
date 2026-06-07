package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.EventService;
import service.LoginService;
import service.RegistrationService;

public class MainView {

    private final LoginService loginService;
    private final EventService eventService;
    private final RegistrationService regService;

    private VBox root;
    private VBox centerArea;

    private NavbarView navbarView;
    private SearchView searchView;
    private EventView eventView;
    private RegistrationView registrationView;
    private LoginView loginView;

    public MainView(EventService eventService,
                    LoginService loginService,
                    RegistrationService regService) {
        this.eventService = eventService;
        this.loginService = loginService;
        this.regService = regService;
    }

    public Scene createScene(Stage stage) {

        // 初始化子 View
        navbarView = new NavbarView();
        searchView = new SearchView();
        eventView = new EventView();
        registrationView = new RegistrationView();
        loginView = new LoginView();

        root = new VBox();
        root.setStyle("-fx-background-color: #ffffff;");

        // 建立導覽列
        var navbar = navbarView.create(
                () -> showEvents(),
                () -> showMyRegistrations(),
                () -> handleLoginOrLogout()
        );

        centerArea = new VBox();
        VBox.setVgrow(centerArea, Priority.ALWAYS);

        root.getChildren().addAll(navbar, centerArea);
        VBox.setVgrow(root, Priority.ALWAYS);

        // 預設顯示活動列表
        showEvents();

        return new Scene(root, 1050, 700);
    }

    // ================= 顯示活動列表 =================
    private void showEvents() {
        Label title = makeTitle("📋 活動列表");

        // 用陣列持有 search 的參照，以便 lambda 內可引用（Java 不允許 var 自我參照）
        VBox[] searchHolder = new VBox[1];

        VBox search = searchView.create(keyword -> {
            javafx.collections.ObservableList<model.Event> filtered =
                    javafx.collections.FXCollections.observableArrayList(eventService.search(keyword));

            javafx.scene.control.TableView<model.Event> filteredTable = EventTableView.create(
                    filtered,
                    event -> handleRegister(event),
                    event -> EventDialogView.show(event)
            );
            VBox.setVgrow(filteredTable, Priority.ALWAYS);
            // 用 searchHolder[0] 取得 search 本身
            centerArea.getChildren().setAll(title, searchHolder[0], filteredTable);
        });
        searchHolder[0] = search;

        javafx.scene.control.TableView<model.Event> table = EventTableView.create(
                eventService.getEvents(),
                event -> handleRegister(event),
                event -> EventDialogView.show(event)
        );
        VBox.setVgrow(table, Priority.ALWAYS);

        centerArea.getChildren().setAll(title, search, table);
    }

    // ================= 顯示我的報名 =================
    private void showMyRegistrations() {

        if (!loginService.isLoggedIn()) {
            showAlert("請先登入", "請先登入後再查看您的報名記錄！");
            return;
        }

        String studentId = loginService.getUserId();
        String role = loginService.getRole();

        Label title;
        VBox tableBox;

        if ("teacher".equals(role)) {
            title = makeTitle("📂 所有活動管理清單");
            tableBox = eventView.createTable(eventService, loginService, regService);
        } else {
            title = makeTitle("📝 我的報名清單");
            tableBox = registrationView.createTable(regService, studentId);
        }

        centerArea.getChildren().setAll(title, tableBox);
    }

    // ================= 登入 / 登出 =================
    private void handleLoginOrLogout() {
        if (loginService.isLoggedIn()) {
            // 登出
            loginService.logout();
            navbarView.setMenu3Text("登入");
            navbarView.setMenu2Text("我的報名");
            showAlert("登出成功", "您已成功登出系統！");
            showEvents();
        } else {
            showLoginPage();
        }
    }

    private void showLoginPage() {
        centerArea.getChildren().setAll(
                loginView.createLoginUI(
                        () -> showLoginForm("學生"),
                        () -> showLoginForm("教職員")
                )
        );
    }

    private void showLoginForm(String role) {
        centerArea.getChildren().setAll(
                loginView.createLoginForm(
                        role,
                        // 登入 submit: (id, pwd) -> void
                        (id, pwd) -> {
                            boolean ok = loginService.login(role, id, pwd);
                            if (ok) {
                                String name = loginService.getUserName();
                                navbarView.setMenu3Text("登出 (" + name + ")");
                                if ("teacher".equals(loginService.getRole())) {
                                    navbarView.setMenu2Text("已管理清單");
                                } else {
                                    navbarView.setMenu2Text("我的報名");
                                }
                                showAlert("登入成功", "歡迎回來，" + name + "！");
                                showEvents();
                            } else {
                                showAlert("登入失敗", "帳號或密碼錯誤，請重新輸入！");
                            }
                        },
                        // 返回
                        () -> showLoginPage()
                )
        );
    }

    // ================= 報名邏輯 =================
    private void handleRegister(model.Event event) {
        if (!loginService.isLoggedIn()) {
            showAlert("請先登入", "請先登入後再進行報名！");
            return;
        }
        if (!"student".equals(loginService.getRole())) {
            showAlert("權限不足", "只有學生可以報名活動！");
            return;
        }
        String studentId = loginService.getUserId();
        boolean ok = regService.register(studentId, event.getName());
        if (ok) {
            showAlert("報名成功", "您已成功報名：" + event.getName());
        } else {
            showAlert("重複報名", "您已報名過此活動！");
        }
    }

    // ================= 輔助方法 =================
    private Label makeTitle(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-padding: 10 20 5 20;");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER_LEFT);
        return lbl;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
