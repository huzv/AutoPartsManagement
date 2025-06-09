package views;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * CategoriesView provides category management UI with search/filter
 * and CRUD operations. Backend integration is marked with TODOs.
 */
public class CategoriesView {
    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("content-area");

        // Title
        Label title = new Label("Category Management");
        title.getStyleClass().add("content-title");
        root.getChildren().add(title);

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search categories...");
        searchField.getStyleClass().add("form-field");
        
        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("action-button");
        searchBtn.setOnAction(e -> {
            // TODO: Query DB for categories matching searchField.getText()
        });

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("secondary-button");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            // TODO: Refresh full category list
        });

        HBox searchBar = new HBox(10, searchField, searchBtn, clearBtn);
        searchBar.getStyleClass().add("filter-bar");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        root.getChildren().add(searchBar);

        // CRUD toolbar
        Button addBtn = new Button("Add Category");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> {
            // TODO: Show dialog to add new category, then refresh list
        });

        Button editBtn = new Button("Edit Selected");
        editBtn.getStyleClass().add("secondary-button");
        editBtn.setOnAction(e -> {
            // TODO: Get selected category and show edit dialog
        });

        Button deleteBtn = new Button("Remove Selected");
        deleteBtn.getStyleClass().add("secondary-button");
        deleteBtn.setOnAction(e -> {
            // TODO: Delete selected category from DB, then refresh list
        });

        ToolBar toolbar = new ToolBar(addBtn, editBtn, deleteBtn);
        root.getChildren().add(toolbar);

        // Category list
        ListView<String> listView = new ListView<>(
            FXCollections.observableArrayList(
                // TODO: Populate with category names from DB
                "Brakes", "Engine", "Ignition"
            )
        );
        listView.setPrefHeight(400);
        listView.getStyleClass().add("table-view");
        root.getChildren().add(listView);

        return root;
    }
}