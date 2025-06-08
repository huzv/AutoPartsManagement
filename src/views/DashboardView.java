package views;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardView {
    
    public Node getView() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(20));
        
        Label title = new Label("Dashboard Overview");
        title.getStyleClass().add("content-title");
        dashboard.getChildren().add(title);

        GridPane summary = new GridPane();
        summary.setHgap(20);
        summary.setVgap(20);

        summary.add(createCard("Total Products", "Placeholder"), 0, 0);
        summary.add(createCard("Products Worth", "Placeholder"), 1, 0);

        dashboard.getChildren().add(summary);

        HBox recentBox = new HBox(30);
        recentBox.setPadding(new Insets(10, 0, 10, 0));

        recentBox.getChildren().add(createCard("Total Revenue", "Placeholder"));
        recentBox.getChildren().add(createCard("Total Suppliers", "Placeholder"));

        dashboard.getChildren().add(recentBox);

        ScrollPane scroll = new ScrollPane(dashboard);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return scroll;
    }

    private VBox createCard(String title, String value) {
        VBox box = new VBox(4);
        box.getStyleClass().add("card");
        Label t = new Label(title.toUpperCase());
        t.getStyleClass().add("card-title");
        Label v = new Label(value);
        v.getStyleClass().add("card-value");
        box.getChildren().addAll(t, v);
        return box;
    }
}