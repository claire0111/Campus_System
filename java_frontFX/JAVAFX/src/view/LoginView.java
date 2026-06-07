package view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;

public class LoginView {

    public VBox createLoginUI(Runnable onStudent, Runnable onOrganizer) {

        VBox outer = new VBox();
        outer.getStyleClass().add("login-panel");
        outer.setAlignment(Pos.CENTER);

        VBox card = new VBox(18);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER);

        Label title = new Label("歡迎登入");
        title.getStyleClass().add("login-title");

        Label hint = new Label("請選擇您的身分");
        hint.getStyleClass().add("hint-text");

        Button studentBtn = new Button("🎓 學生登入");
        Button organizerBtn = new Button("🏢 主辦單位登入");

        studentBtn.getStyleClass().add("btn-primary");
        organizerBtn.getStyleClass().add("btn-primary");
        studentBtn.setMaxWidth(280);
        organizerBtn.setMaxWidth(280);

        studentBtn.setOnAction(e -> onStudent.run());
        organizerBtn.setOnAction(e -> onOrganizer.run());

        card.getChildren().addAll(title, hint, studentBtn, organizerBtn);
        outer.getChildren().add(card);
        return outer;
    }

    public VBox createLoginForm(String role,
                                BiConsumer<String, String> onSubmit,
                                Runnable onBack) {

        VBox outer = new VBox();
        outer.getStyleClass().add("login-panel");
        outer.setAlignment(Pos.CENTER);

        VBox card = new VBox(14);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER);

        Label title = new Label(role + " 登入");
        title.getStyleClass().add("login-title");

        Label idLabel = new Label("帳號");
        idLabel.getStyleClass().add("login-label");
        TextField idField = new TextField();
        idField.setPromptText("請輸入學號 / 帳號");
        idField.getStyleClass().add("login-field");

        Label pwdLabel = new Label("密碼");
        pwdLabel.getStyleClass().add("login-label");
        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText("請輸入密碼");
        pwdField.getStyleClass().add("login-field");

        Button submit = new Button("確認登入");
        submit.getStyleClass().add("btn-primary");
        submit.setMaxWidth(280);

        Button back = new Button("← 返回");
        back.getStyleClass().add("btn-outline");

        submit.setOnAction(e -> onSubmit.accept(idField.getText().trim(), pwdField.getText()));
        back.setOnAction(e -> onBack.run());

        card.getChildren().addAll(title, idLabel, idField, pwdLabel, pwdField, submit, back);
        outer.getChildren().add(card);
        return outer;
    }
}
