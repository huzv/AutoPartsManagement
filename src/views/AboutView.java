package views;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * AboutView shows application information and logs refresh.
 */
public class AboutView {
    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("content-area");

        // Title
        Label title = new Label("About");
        title.getStyleClass().add("content-title");
        root.getChildren().add(title);

        // Description or version info
        Label desc = new Label("Platinum AutoParts System v1.0.0\nA comprehensive parts management application.");
        desc.setWrapText(true);
        root.getChildren().add(desc);

        // Team credits
        Label credits = new Label("Developed by Ameer Mahmoud (1230105) and Mohammed Hmaid (1230302)");
        credits.setWrapText(true);
        root.getChildren().add(credits);

        // Logs section
        Label logLabel = new Label("Internal Logs");
        logLabel.getStyleClass().add("content-title");
        root.getChildren().add(new Separator());
        root.getChildren().add(logLabel);

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(200);
        // TODO: Load logs from backend into logArea

        Button refreshBtn = new Button("Refresh Logs");
        refreshBtn.getStyleClass().add("secondary-button");
        refreshBtn.setOnAction(e -> {
            // TODO: Reload logs into logArea
        });

        root.getChildren().addAll(logArea, refreshBtn);
        return root;
    }
}
