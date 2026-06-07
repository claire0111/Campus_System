package view;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

public class NavbarView {

    private Hyperlink menu2;
    private Hyperlink menu3;

    public HBox create(
            Runnable onList,
            Runnable onMy,
            Runnable onLogin) {

        // ================= NAVBAR =================
        HBox navbar = new HBox(25);
        navbar.setStyle(
                "-fx-background-color: rgb(214,249,214);" +
                        "-fx-padding: 10 20;" +
                        "-fx-alignment: center-left;" +
                        "-fx-min-height: 70px;"
        );

        // ================= BRAND =================
        HBox brandSection = new HBox(12);
        brandSection.setAlignment(Pos.CENTER_LEFT);

        try {
            String imagePath = getClass()
                    .getResource("/images/yuntech.png")
                    .toExternalForm();

            Image logoImage = new Image(imagePath);
            ImageView logoView = new ImageView(logoImage);

            logoView.setFitWidth(45);
            logoView.setFitHeight(45);

            Circle clipCircle = new Circle(22.5, 22.5, 22.5);
            logoView.setClip(clipCircle);

            brandSection.getChildren().add(logoView);

        } catch (Exception e) {
            System.out.println("提示：未讀取 logo");
        }

        Label brandLabel = new Label("校園活動管理系統");
        brandLabel.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-text-fill: rgb(8,100,60);" +
                        "-fx-font-weight: bold;"
        );

        brandSection.getChildren().add(brandLabel);

        // ================= MENU =================
        Hyperlink menu1 = new Hyperlink("活動列表");
        menu2 = new Hyperlink("我的報名");
        menu3 = new Hyperlink("登入");

        String menuStyle =
                "-fx-text-fill: #0a5338;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-underline: false;";

        menu1.setStyle(menuStyle);
        menu2.setStyle(menuStyle);
        menu3.setStyle(menuStyle);

        menu1.setOnAction(e -> onList.run());
        menu2.setOnAction(e -> onMy.run());
        menu3.setOnAction(e -> onLogin.run());

        // ================= SPACER =================
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox menuBox = new HBox(20, menu1, menu2, menu3);
        menuBox.setAlignment(Pos.CENTER_RIGHT);

        navbar.getChildren().addAll(brandSection, spacer, menuBox);

        return navbar;
    }

    // 更新 menu2 文字（我的報名 / 已管理清單）
    public void setMenu2Text(String text) {
        if (menu2 != null) menu2.setText(text);
    }

    // 更新 menu3 文字（登入 / 登出）
    public void setMenu3Text(String text) {
        if (menu3 != null) menu3.setText(text);
    }
}
