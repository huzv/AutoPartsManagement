package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.util.List;

// Showing Alerts & Dialogs.
public class AlertHelper {
    
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static void showSummaryWithIssuesDialog(String title, String summary, List<String> issues) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(summary);

        if (issues != null && !issues.isEmpty()) {
            alert.setAlertType(Alert.AlertType.WARNING);

            VBox dialogPaneContent = new VBox();
            dialogPaneContent.setSpacing(10);

            Label issuesLabel = new Label("The following issues were encountered:");

            TextArea textArea = new TextArea(String.join("\n", issues));
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefHeight(150);
            textArea.setMaxWidth(Double.MAX_VALUE);
            VBox.setVgrow(textArea, Priority.ALWAYS);

            ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(150);

            dialogPaneContent.getChildren().addAll(issuesLabel, scrollPane);

            alert.getDialogPane().setContent(dialogPaneContent);
            alert.getDialogPane().setPrefWidth(480);
        }
        alert.showAndWait();
    }
}
