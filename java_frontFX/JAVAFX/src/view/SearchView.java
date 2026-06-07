package view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class SearchView {

    private TextField searchField;

    // onSearch: 傳回使用者輸入的關鍵字
    public VBox create(Consumer<String> onSearch) {

        VBox heroSearch = new VBox(15);
        heroSearch.setStyle("-fx-background-color: #f0fdf4; -fx-padding: 25 30; -fx-alignment: center;");

        Label title = new Label("🔍 活動查詢");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0a5338;");

        searchField = new TextField();
        searchField.setPromptText("搜尋活動名稱 / 地點...");
        searchField.setPrefWidth(350);
        searchField.setStyle("-fx-padding: 10; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #a7f3d0; -fx-font-size: 14px;");

        Button btn = new Button("搜尋");
        btn.setStyle(
                "-fx-background-color: #0a5338; -fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-font-weight: bold;" +
                "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");

        btn.setOnAction(e -> onSearch.accept(searchField.getText().trim()));

        // 按 Enter 也觸發搜尋
        searchField.setOnAction(e -> onSearch.accept(searchField.getText().trim()));

        HBox row = new HBox(12, searchField, btn);
        row.setAlignment(Pos.CENTER);

        heroSearch.getChildren().addAll(title, row);

        return heroSearch;
    }

    // 清空搜尋欄
    public void clear() {
        if (searchField != null) searchField.clear();
    }
}
