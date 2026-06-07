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

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NavbarView {

    private static final String LOGO_TEXT = "校園活動管理系統";
    private static final String[] LOGO_PATHS = {
            "images/yuntech.png",
            "src/images/yuntech.png"
    };

    private Hyperlink menu2;
    private Hyperlink menu3;

    public HBox create(Runnable onList, Runnable onMy, Runnable onLogin) {

        HBox navbar = new HBox(25);
        navbar.getStyleClass().add("navbar");

        HBox brandSection = new HBox(14);
        brandSection.setAlignment(Pos.CENTER_LEFT);
        buildBrandSection(brandSection);

        Hyperlink menu1 = new Hyperlink("活動列表");
        menu2 = new Hyperlink("我的報名");
        menu3 = new Hyperlink("登入");

        for (Hyperlink link : new Hyperlink[]{menu1, menu2, menu3}) {
            link.getStyleClass().add("nav-link");
        }

        menu1.setOnAction(e -> onList.run());
        menu2.setOnAction(e -> onMy.run());
        menu3.setOnAction(e -> onLogin.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox menuBox = new HBox(8, menu1, menu2, menu3);
        menuBox.setAlignment(Pos.CENTER_RIGHT);

        navbar.getChildren().addAll(brandSection, spacer, menuBox);
        return navbar;
    }

    private void buildBrandSection(HBox brandSection) {
        ImageView logoView = loadLogoImage();

        if (logoView != null) {
            Label brandLabel = new Label(LOGO_TEXT);
            brandLabel.getStyleClass().add("brand-title");
            brandSection.getChildren().addAll(logoView, brandLabel);
        } else {
            Label brandLabel = new Label(LOGO_TEXT);
            brandLabel.getStyleClass().addAll("brand-title", "brand-title-only");
            brandSection.getChildren().add(brandLabel);
        }
    }

    private ImageView loadLogoImage() {
        for (String pathStr : LOGO_PATHS) {
            ImageView view = loadFromFile(Paths.get(pathStr));
            if (view != null) return view;
        }
        return loadFromClasspath();
    }

    private ImageView loadFromFile(Path path) {
        try {
            if (!Files.exists(path)) return null;
            Image image = new Image(path.toUri().toString(), false);
            if (image.isError() || image.getWidth() <= 0) return null;
            return createLogoView(image);
        } catch (Exception e) {
            return null;
        }
    }

    private ImageView loadFromClasspath() {
        try {
            URL url = getClass().getResource("/images/yuntech.png");
            if (url == null) return null;
            Image image = new Image(url.toExternalForm(), false);
            if (image.isError() || image.getWidth() <= 0) return null;
            return createLogoView(image);
        } catch (Exception e) {
            return null;
        }
    }

    private ImageView createLogoView(Image image) {
        ImageView logoView = new ImageView(image);
        logoView.setFitWidth(45);
        logoView.setFitHeight(45);
        logoView.setPreserveRatio(true);

        Circle clip = new Circle(22.5, 22.5, 22.5);
        logoView.setClip(clip);
        return logoView;
    }

    public void setMenu2Text(String text) {
        if (menu2 != null) menu2.setText(text);
    }

    public void setMenu3Text(String text) {
        if (menu3 != null) menu3.setText(text);
    }
}
