package view;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Event;
import service.EventService;
import service.RegistrationService;
import service.LoginService;

import java.util.function.Consumer;

public class EventView {

    // 建立完整活動表格（含報名按鈕與雙擊詳細）
    public VBox createTable(
            EventService eventService,
            LoginService loginService,
            RegistrationService regService
    ) {

        ObservableList<Event> events = eventService.getEvents();

        TableView<Event> table = EventTableView.create(
                events,
                // 點擊「報名」按鈕
                event -> {
                    if (!loginService.isLoggedIn()) {
                        showAlert("請先登入", "請先登入後再進行報名！");
                        return;
                    }
                    if (!"student".equals(loginService.getRole())) {
                        showAlert("權限不足", "只有學生可以報名活動！");
                        return;
                    }
                    String studentId = loginService.getCurrentUser().getId();
                    boolean ok = regService.register(studentId, event.getName());
                    if (ok) {
                        showAlert("報名成功", "您已成功報名：" + event.getName());
                    } else {
                        showAlert("重複報名", "您已報名過此活動！");
                    }
                },
                // 雙擊顯示詳細
                event -> EventDialogView.show(event)
        );

        VBox.setVgrow(table, Priority.ALWAYS);
        VBox box = new VBox(table);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }

    private void showAlert(String title, String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
