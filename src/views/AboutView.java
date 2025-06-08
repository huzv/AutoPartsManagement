package views;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AboutView {
    
    public Node getView() {
        VBox layout = new VBox(20);
        layout.getStyleClass().add("content-area");
        layout.setPadding(new Insets(20));

        Label title = new Label("About");
        title.getStyleClass().add("content-title");

        GridPane aboutGrid = new GridPane();
        aboutGrid.setHgap(10);
        aboutGrid.setVgap(10);
        aboutGrid.setPadding(new Insets(10));

        Label nameText = new Label("Platinum AutoParts");
        nameText.setFont(Font.font(null, FontWeight.BOLD, 16));

        Label phoneText = new Label("Phone: 0561234567");
        phoneText.setFont(Font.font(null, FontWeight.BOLD, 16));

        Label locationText = new Label("Location: Ramallah, Beitunia");
        locationText.setFont(Font.font(null, FontWeight.BOLD, 16));

        Label madeByText = new Label("Made by:");
        madeByText.setFont(Font.font(null, FontWeight.BOLD, 14));
        Label ameer = new Label("Ameer Mahmoud, 1230105");
        ameer.setFont(Font.font(null, FontWeight.BOLD, 12));
        Label mohammed = new Label("Mohammed Hmaid, 1230302");
        mohammed.setFont(Font.font(null, FontWeight.BOLD, 12));

        aboutGrid.add(phoneText, 0, 0, 2, 1);
        aboutGrid.add(locationText, 0, 1, 2, 1);
        
        Label viewLogLabel = new Label("Internal Logs:");
        viewLogLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        TextArea logDisplayArea = new TextArea();
        logDisplayArea.getStyleClass().add("table-view");
        logDisplayArea.setEditable(false);
        logDisplayArea.setWrapText(true);
        logDisplayArea.setPrefHeight(300);
        VBox.setVgrow(logDisplayArea, Priority.ALWAYS);

        Button refreshLogViewBtn = new Button("Refresh Log View");
        refreshLogViewBtn.getStyleClass().add("secondary-button");

        layout.getChildren().addAll(
                title,
                aboutGrid,
                new Separator(),
                viewLogLabel,
                logDisplayArea,
                refreshLogViewBtn, 
                madeByText, 
                ameer, 
                mohammed);

        return layout;
    }
}
