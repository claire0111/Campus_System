package view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.EventService;

import java.util.function.BiConsumer;

public class SearchView {

    private TextField searchField;
    private ComboBox<String> sortCombo;

    public VBox create(BiConsumer<String, EventService.SortMode> onSearch) {

        VBox heroSearch = new VBox(14);
        heroSearch.getStyleClass().add("search-panel");
        heroSearch.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label("🔍 活動查詢");
        title.getStyleClass().add("search-title");

        searchField = new TextField();
        searchField.setPromptText("搜尋活動名稱、地點、主辦單位、內容...");
        searchField.setPrefWidth(320);
        searchField.getStyleClass().add("search-field");

        sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll(
                "活動時間（近→遠）",
                "活動時間（遠→近）",
                "報名開始時間"
        );
        sortCombo.setValue("活動時間（近→遠）");
        sortCombo.getStyleClass().add("sort-combo");

        Button btn = new Button("搜尋");
        btn.getStyleClass().add("btn-primary");

        Runnable doSearch = () -> onSearch.accept(
                searchField.getText().trim(),
                getSelectedSortMode()
        );

        btn.setOnAction(e -> doSearch.run());
        searchField.setOnAction(e -> doSearch.run());
        sortCombo.setOnAction(e -> doSearch.run());

        HBox row = new HBox(12, searchField, sortCombo, btn);
        row.setAlignment(Pos.CENTER);

        heroSearch.getChildren().addAll(title, row);
        return heroSearch;
    }

    private EventService.SortMode getSelectedSortMode() {
        String selected = sortCombo.getValue();
        if ("活動時間（遠→近）".equals(selected)) return EventService.SortMode.EVENT_TIME_DESC;
        if ("報名開始時間".equals(selected)) return EventService.SortMode.REG_START_ASC;
        return EventService.SortMode.EVENT_TIME_ASC;
    }

    public void clear() {
        if (searchField != null) searchField.clear();
    }
}
