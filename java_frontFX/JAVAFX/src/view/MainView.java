package view;

import java.lang.classfile.Label;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.EventService;
import service.LoginService;
import service.RegistrationService;

public class MainView {
    private LoginService loginService = new LoginService();
    private EventService eventService = new EventService();
    private RegistrationService regService = new RegistrationService();

    private VBox root;
    private VBox centerArea;

    private Label tableTitle;
    private Hyperlink menu1, menu2, menu3;

    private EventView eventView;
    private RegistrationView registrationView;
    private LoginView loginView;

    public Scene createScene(Stage stage) {

        eventView = new EventView();
        registrationView = new RegistrationView();
        loginView = new LoginView();

        root = new VBox();

        root.getChildren().addAll(createNavbar(stage), createCenter());

        return new Scene(root, 1050, 700);
    }

    // ================= NAVBAR =================
    private HBox createNavbar(Stage stage) {

        menu1 = new Hyperlink("活動列表");
        menu2 = new Hyperlink("我的報名");
        menu3 = new Hyperlink("登入");

        menu1.setOnAction(e -> showEvents());
        menu2.setOnAction(e -> showMyRegistrations());
        menu3.setOnAction(e -> showLogin());

        HBox box = new HBox(20, menu1, menu2, menu3);
        return box;
    }

    // ================= CENTER =================
    private VBox createCenter() {
        centerArea = new VBox();
        tableTitle = new Label("活動列表");

        centerArea.getChildren().addAll(tableTitle, eventView.createTable(eventService));

        return centerArea;
    }

    private void showEvents() {
        tableTitle.setText("活動列表");
        centerArea.getChildren().setAll(tableTitle, eventView.createTable(eventService));
    }

    private void showMyRegistrations() {

        if (!loginService.isLoggedIn()) {
            new Alert(Alert.AlertType.WARNING, "請先登入").show();
            return;
        }

        tableTitle.setText("我的報名");

        centerArea.getChildren().setAll(
                tableTitle,
                registrationView.createTable(regService, loginService.getUserId())
        );
    }

    private void showLogin() {
        centerArea.getChildren().setAll(
                loginView.createLoginUI(
                        () -> showStudentLogin(),
                        () -> showTeacherLogin()
                )
        );
    }

    private void showStudentLogin() {
        centerArea.getChildren().setAll(
                loginView.createLoginForm("學生",
                        () -> {}, // submit
                        () -> showLogin()
                )
        );
    }

    private void showTeacherLogin() {
        centerArea.getChildren().setAll(
                loginView.createLoginForm("教職員",
                        () -> {},
                        () -> showLogin()
                )
        );
    }
}
