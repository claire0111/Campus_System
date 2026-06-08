package view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

        // 設置視窗為 1024x680
        Scene scene = new Scene(root, 1024, 680);
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

    // ================= 顯示活動列表 (卡片格線樣式) =================
    private void showEvents() {
        eventService.refreshStatus();

        if (navbarView != null) {
            navbarView.setActiveMenu(1);
        }

        VBox cardsBox = new VBox();
        cardsBox.getStyleClass().add("cards-container");
        VBox.setVgrow(cardsBox, Priority.ALWAYS);

        // 建立搜尋面板，當使用者點擊搜尋、排序變更時會觸發回呼
        VBox searchPanel = searchView.create((keyword, sortMode) -> {
            var list = eventService.searchAndSort(keyword, sortMode);
            cardsBox.getChildren().setAll(renderCardGrid(list));
        });
        searchPanel.setMaxWidth(800);

        // 建立 Hero Section (包含文字 Slogan 與浮動的 glassmorphic 搜尋面板)
        VBox heroBanner = new VBox(14);
        heroBanner.getStyleClass().add("hero-banner");
        heroBanner.setAlignment(Pos.CENTER);
        heroBanner.setMinHeight(200);
        heroBanner.setPadding(new Insets(20, 20, 20, 20));

        Label title = new Label("Discover. Engage. Excel.");
        title.getStyleClass().add("hero-title");

        Label subtitle = new Label("Your YunTech Journey Starts Here.");
        subtitle.getStyleClass().add("hero-subtitle");

        heroBanner.getChildren().addAll(title, subtitle, searchPanel);

        // 預設載入活動
        var list = eventService.searchAndSort("", SortMode.EVENT_TIME_ASC);
        cardsBox.getChildren().setAll(renderCardGrid(list));

        // 使用 ScrollPane 讓整個頁面（包含 Hero 與卡片）可以滾動
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.getStyleClass().add("main-scroll-pane");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox scrollContent = new VBox(20);
        scrollContent.setStyle("-fx-background-color: #f8fafc;");
        scrollContent.getChildren().addAll(heroBanner, cardsBox);
        scroll.setContent(scrollContent);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        centerArea.getChildren().setAll(scroll);
    }

    private GridPane renderCardGrid(ObservableList<Event> list) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("activity-grid");
        grid.setAlignment(Pos.TOP_CENTER);

        if (list.isEmpty()) {
            Label noEvents = new Label("沒有找到符合條件的活動");
            noEvents.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748b; -fx-padding: 40;");
            grid.add(noEvents, 0, 0);
            return grid;
        }

        int columns = 3;
        for (int i = 0; i < list.size(); i++) {
            Event event = list.get(i);
            VBox card = ActivityCardView.create(
                    event, loginService, regService, this::showEventDetail, this::handleRegister
            );
            int row = i / columns;
            int col = i % columns;
            grid.add(card, col, row);
        }
        return grid;
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

        if (navbarView != null) {
            navbarView.setActiveMenu(2);
        }

        if (loginService.isOrganizer()) {
            showOrganizerManagement(loginService.getUserId());
        } else {
            // 標題橫幅
            VBox headerBanner = new VBox();
            headerBanner.getStyleClass().add("hero-banner");
            headerBanner.setAlignment(Pos.CENTER);
            headerBanner.setMinHeight(130);

            VBox headerContent = new VBox(8);
            headerContent.setAlignment(Pos.CENTER);

            Label title = new Label("Student Dashboard");
            title.getStyleClass().add("hero-title");
            title.setStyle("-fx-font-size: 32px;");

            Label subtitle = new Label("My Registered Activities");
            subtitle.getStyleClass().add("hero-subtitle");

            headerContent.getChildren().addAll(title, subtitle);
            headerBanner.getChildren().add(headerContent);

            VBox tableBox = registrationView.createTable(
                    regService, eventService, loginService.getUserId());
            
            // 使用滾動包裝
            ScrollPane scroll = new ScrollPane(tableBox);
            scroll.setFitToWidth(true);
            scroll.getStyleClass().add("main-scroll-pane");
            VBox.setVgrow(scroll, Priority.ALWAYS);

            centerArea.getChildren().setAll(headerBanner, scroll);
        }
    }

    // ================= 主辦單位活動管理 =================
    private void showOrganizerManagement(String organizerId) {
        eventService.refreshStatus();

        if (navbarView != null) {
            navbarView.setActiveMenu(2);
        }

        // 標題橫幅
        VBox headerBanner = new VBox();
        headerBanner.getStyleClass().add("hero-banner");
        headerBanner.setAlignment(Pos.CENTER);
        headerBanner.setMinHeight(130);

        VBox headerContent = new VBox(8);
        headerContent.setAlignment(Pos.CENTER);

        Label title = new Label("Organizer Management");
        title.getStyleClass().add("hero-title");
        title.setStyle("-fx-font-size: 32px;");

        Label subtitle = new Label("Create, edit and manage hosted activities");
        subtitle.getStyleClass().add("hero-subtitle");

        headerContent.getChildren().addAll(title, subtitle);
        headerBanner.getChildren().add(headerContent);

        Button createBtn = new Button("+ 建立新活動");
        createBtn.getStyleClass().add("btn-primary");
        createBtn.setOnAction(e -> {
            if (!(loginService.getCurrentUser() instanceof Organizer organizer)) return;
            EventFormView.showCreate(organizer, data -> {
                Event created = eventService.addEvent(
                        data.name, data.location, data.eventTime,
                        data.regStart, data.regEnd, data.unit,
                        data.contact, data.content, data.limit,
                        data.organizerName, data.imageFile
                );
                organizer.trackHostedEvent(created.getId());
                showAlert("建立成功", "活動「" + data.name + "」已建立！");
                showOrganizerManagement(organizerId);
            });
        });

        VBox header = new VBox(16, createBtn);
        header.getStyleClass().add("admin-header");
        header.setPadding(new Insets(20, 32, 0, 32));

        var table = AdminEventTableView.create(
                eventService.getEvents(),
                organizerId,
                (event, isEdit) -> handleEditOrDelete(event, isEdit, organizerId),
                this::showEventDetail
        );

        VBox tableBox = new VBox(table);
        tableBox.getStyleClass().add("table-wrapper");
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox contentBox = new VBox(header, tableBox);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("main-scroll-pane");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        centerArea.getChildren().setAll(headerBanner, scroll);
    }

    private void handleEditOrDelete(Event event, boolean isEdit, String organizerId) {
        if (!eventService.isOrganizerOf(event, organizerId)) {
            showAlert("權限不足", "您只能管理自己主辦的活動！");
            return;
        }

        if (isEdit) {
            if (!(loginService.getCurrentUser() instanceof Organizer organizer)) return;
            EventFormView.showEdit(event, organizer, data -> {
                event.setName(data.name);
                event.setLocation(data.location);
                event.setEventTime(data.eventTime);
                event.setRegStart(data.regStart);
                event.setRegEnd(data.regEnd);
                event.setUnit(data.unit);
                event.setContent(data.content);
                event.setLimit(data.limit);
                event.setOrganizerName(data.organizerName);
                eventService.updateEvent(event, data.imageFile);
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
        if (navbarView != null) {
            navbarView.setActiveMenu(3);
        }
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

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
