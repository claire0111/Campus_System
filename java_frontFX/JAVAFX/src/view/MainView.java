package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import model.Organizer;
import model.Student;
import service.EventService;
import service.EventService.SortMode;
import service.LoginService;
import service.RegistrationService;
import service.RegistrationService.RegisterResult;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MainView {

    private final LoginService loginService;
    private final EventService eventService;
    private final RegistrationService regService;

    private VBox root;
    private VBox centerArea;

    private NavbarView navbarView;
    private SearchView searchView;
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
        navbarView = new NavbarView();
        searchView = new SearchView();
        registrationView = new RegistrationView();
        loginView = new LoginView();

        root = new VBox();
        root.getStyleClass().add("app-root");

        var navbar = navbarView.create(
                this::showEvents,
                this::showMyRegistrations,
                this::handleLoginOrLogout
        );

        centerArea = new VBox();
        centerArea.getStyleClass().add("content-area");
        VBox.setVgrow(centerArea, Priority.ALWAYS);

        root.getChildren().addAll(navbar, centerArea);
        VBox.setVgrow(root, Priority.ALWAYS);

        showEvents();

        Scene scene = new Scene(root, 1100, 720);
        loadStylesheet(scene);
        return scene;
    }

    private void loadStylesheet(Scene scene) {
        try {
            var url = getClass().getResource("/style.css");
            if (url != null) {
                scene.getStylesheets().add(url.toExternalForm());
                return;
            }
            var path = Paths.get("src/style.css");
            if (Files.exists(path)) {
                scene.getStylesheets().add(path.toUri().toString());
            }
        } catch (Exception e) {
            System.out.println("無法載入樣式表: " + e.getMessage());
        }
    }

    // ================= 顯示活動列表 =================
    private void showEvents() {
        eventService.refreshStatus();

        Label title = makeTitle("📋 即將舉行的活動");
        Label subtitle = makeSubtitle("僅顯示尚未結束的活動，請點擊「查看詳情」進入說明頁後報名");

        VBox[] searchHolder = new VBox[1];

        VBox search = searchView.create((keyword, sortMode) -> {
            var list = eventService.searchAndSort(keyword, sortMode);
            var table = EventTableView.create(list, this::showEventDetail);
            centerArea.getChildren().setAll(title, subtitle, searchHolder[0], wrapTable(table));
        });
        searchHolder[0] = search;

        var list = eventService.searchAndSort("", SortMode.EVENT_TIME_ASC);
        var table = EventTableView.create(list, this::showEventDetail);
        centerArea.getChildren().setAll(title, subtitle, search, wrapTable(table));
    }

    private VBox wrapTable(TableView<?> table) {
        VBox wrapper = new VBox(table);
        wrapper.getStyleClass().add("table-wrapper");
        VBox.setVgrow(table, Priority.ALWAYS);
        return wrapper;
    }

    // ================= 活動詳細說明頁 =================
    private void showEventDetail(Event event) {
        eventService.refreshStatus();
        Event fresh = eventService.getById(event.getId());
        if (fresh == null) fresh = event;

        VBox detail = EventDetailView.create(
                fresh, loginService, regService, this::showEvents, this::handleRegister
        );
        VBox.setVgrow(detail, Priority.ALWAYS);
        centerArea.getChildren().setAll(detail);
    }

    // ================= 我的報名 / 活動管理 =================
    private void showMyRegistrations() {
        if (!loginService.isLoggedIn()) {
            showAlert("請先登入", "請先登入後再查看！");
            return;
        }

        if (loginService.isOrganizer()) {
            showOrganizerManagement(loginService.getUserId());
        } else {
            Label title = makeTitle("📝 我的報名清單");
            Label subtitle = makeSubtitle("您可以在此取消已報名的活動");
            VBox tableBox = registrationView.createTable(
                    regService, eventService, loginService.getUserId());
            centerArea.getChildren().setAll(title, subtitle, tableBox);
        }
    }

    // ================= 主辦單位活動管理 =================
    private void showOrganizerManagement(String organizerId) {
        eventService.refreshStatus();
        Label title = makeTitle("📂 活動管理");
        Label subtitle = makeSubtitle("您只能編輯或刪除自己主辦的活動");

        Button createBtn = new Button("+ 建立活動");
        createBtn.getStyleClass().add("btn-primary");
        createBtn.setOnAction(e -> EventFormView.showCreate(organizerId, data -> {
            Event created = eventService.addEvent(
                    data.name, data.location, data.eventTime,
                    data.regStart, data.regEnd, data.unit,
                    data.contact, data.content, data.limit
            );
            if (loginService.getCurrentUser() instanceof Organizer org) {
                org.trackHostedEvent(created.getId());
            }
            showAlert("建立成功", "活動「" + data.name + "」已建立！");
            showOrganizerManagement(organizerId);
        }));

        VBox header = new VBox(6, title, subtitle, createBtn);
        header.getStyleClass().add("admin-header");

        var table = AdminEventTableView.create(
                eventService.getEvents(),
                organizerId,
                (event, isEdit) -> handleEditOrDelete(event, isEdit, organizerId),
                this::showEventDetail
        );
        centerArea.getChildren().setAll(header, wrapTable(table));
    }

    private void handleEditOrDelete(Event event, boolean isEdit, String organizerId) {
        if (!eventService.isOrganizerOf(event, organizerId)) {
            showAlert("權限不足", "您只能管理自己主辦的活動！");
            return;
        }

        if (isEdit) {
            EventFormView.showEdit(event, organizerId, data -> {
                event.setName(data.name);
                event.setLocation(data.location);
                event.setEventTime(data.eventTime);
                event.setRegStart(data.regStart);
                event.setRegEnd(data.regEnd);
                event.setUnit(data.unit);
                event.setContent(data.content);
                event.setLimit(data.limit);
                eventService.updateEvent(event);
                showAlert("更新成功", "活動「" + data.name + "」已更新！");
                showOrganizerManagement(organizerId);
            });
        } else {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("確認刪除");
            confirm.setHeaderText("確定要刪除此活動嗎？");
            confirm.setContentText("活動：" + event.getName() + "\n相關報名紀錄也將一併移除。");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    regService.cancelByEvent(event);
                    eventService.deleteEvent(event.getId());
                    showAlert("刪除成功", "活動已刪除。");
                    showOrganizerManagement(organizerId);
                }
            });
        }
    }

    // ================= 登入 / 登出 =================
    private void handleLoginOrLogout() {
        if (loginService.isLoggedIn()) {
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
                        () -> showLoginForm("主辦單位")
                )
        );
    }

    private void showLoginForm(String role) {
        centerArea.getChildren().setAll(
                loginView.createLoginForm(
                        role,
                        (id, pwd) -> {
                            if (loginService.login(role, id, pwd)) {
                                String name = loginService.getUserName();
                                navbarView.setMenu3Text("登出 (" + name + ")");
                                navbarView.setMenu2Text(
                                        loginService.isOrganizer() ? "活動管理" : "我的報名"
                                );
                                loginService.getCurrentUser().displayMenu();
                                showAlert("登入成功", "歡迎回來，" + name + "！");
                                showEvents();
                            } else {
                                showAlert("登入失敗", "帳號或密碼錯誤，請重新輸入！");
                            }
                        },
                        this::showLoginPage
                )
        );
    }

    // ================= 報名邏輯（透過 Registerable 介面） =================
    private void handleRegister(Event event) {
        if (!loginService.isLoggedIn()) {
            showAlert("請先登入", "請先登入後再進行報名！");
            return;
        }
        if (!(loginService.getCurrentUser() instanceof Student student)) {
            showAlert("權限不足", "只有學生可以報名活動！");
            return;
        }

        eventService.refreshStatus();
        Event fresh = eventService.getById(event.getId());
        if (fresh == null) fresh = event;

        RegisterResult result = student.registerFor(fresh, regService);

        switch (result) {
            case SUCCESS -> {
                showAlert("報名成功", "您已成功報名：" + fresh.getName());
                showEventDetail(fresh);
            }
            case DUPLICATE -> showAlert("重複報名", "您已報名過此活動！");
            case FULL -> showAlert("名額已滿", "此活動報名人數已達上限，無法報名。");
            case CLOSED -> showAlert("無法報名", "目前不在報名期間內。");
        }
    }

    private Label makeTitle(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("page-title");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER_LEFT);
        return lbl;
    }

    private Label makeSubtitle(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("page-subtitle");
        lbl.setMaxWidth(Double.MAX_VALUE);
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
