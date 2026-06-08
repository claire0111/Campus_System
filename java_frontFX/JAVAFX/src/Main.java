import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.EventService;
import service.LoginService;
import service.RegistrationService;
import view.MainView;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // 建立 Service
        LoginService loginService = new LoginService();
        EventService eventService = new EventService();
        RegistrationService regService = new RegistrationService();

        // 載入活動資料
        eventService.loadEvents("data/activity sheet.csv");

        // 建立主畫面
        MainView mainView = new MainView(eventService, loginService, regService);
        Scene scene = mainView.createScene(stage);

        stage.setTitle("校園活動管理系統");
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.setMaximized(false);
        stage.setWidth(1024);
        stage.setHeight(680);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}