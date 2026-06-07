package view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;

public class LoginView {

    // 登入首頁：選擇身分
    public VBox createLoginUI(Runnable onStudent, Runnable onTeacher) {

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 60;");

        Label title = new Label("請選擇登入身分");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0a5338;");

        Button studentBtn = new Button("🎓 學生登入");
        Button teacherBtn = new Button("💼 教職員登入");

        String btnStyle =
                "-fx-font-size: 16px; -fx-font-weight: bold;" +
                "-fx-background-color: #0a5338; -fx-text-fill: white;" +
                "-fx-background-radius: 10; -fx-padding: 12 40; -fx-cursor: hand;";

        studentBtn.setStyle(btnStyle);
        teacherBtn.setStyle(btnStyle);

        studentBtn.setOnAction(e -> onStudent.run());
        teacherBtn.setOnAction(e -> onTeacher.run());

        root.getChildren().addAll(title, studentBtn, teacherBtn);
        return root;
    }

    // 登入表單：帳號密碼輸入
    // onSubmit: (id, password) -> void
    public VBox createLoginForm(String role,
                                BiConsumer<String, String> onSubmit,
                                Runnable onBack) {

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 60;");

        Label title = new Label(role + " 登入");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0a5338;");

        Label idLabel = new Label("帳號");
        idLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        TextField idField = new TextField();
        idField.setPromptText("請輸入學號 / 帳號");
        idField.setMaxWidth(300);

        Label pwdLabel = new Label("密碼");
        pwdLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText("請輸入密碼");
        pwdField.setMaxWidth(300);

        Button submit = new Button("確認登入");
        submit.setStyle(
                "-fx-font-size: 15px; -fx-font-weight: bold;" +
                "-fx-background-color: #0a5338; -fx-text-fill: white;" +
                "-fx-background-radius: 10; -fx-padding: 10 40; -fx-cursor: hand;");

        Button back = new Button("← 返回");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-size: 14px; -fx-cursor: hand;");

        submit.setOnAction(e -> onSubmit.accept(idField.getText().trim(), pwdField.getText()));
        back.setOnAction(e -> onBack.run());

        root.getChildren().addAll(title, idLabel, idField, pwdLabel, pwdField, submit, back);
        return root;
    }
}
