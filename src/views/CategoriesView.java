package views;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CategoriesView {
    
    public Node getView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(10));
        Label title = new Label("Category Management");
        title.getStyleClass().add("content-title");

        ListView<String> categoryListView = new ListView<>();
        
        HBox searchBar = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search categories...");
        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("action-button");
        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("secondary-button");
        searchBar.getChildren().addAll(searchField, searchBtn, clearBtn);

        HBox buttonBar = new HBox(10);
        Button addCategoryButton = new Button("Add New Category");
        addCategoryButton.getStyleClass().add("action-button");

        Button editCategoryButton = new Button("Edit Category");
        editCategoryButton.getStyleClass().add("secondary-button");

        Button removeCategoryButton = new Button("Remove Category");
        removeCategoryButton.getStyleClass().add("secondary-button");
        buttonBar.getChildren().addAll(addCategoryButton, editCategoryButton, removeCategoryButton);

        layout.getChildren().addAll(title, searchBar, categoryListView, buttonBar);
        return layout;
    }
}
