package views;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * InventoryView shows parts management UI with filtering and CRUD operations.
 * Backend integration points are marked with TODO comments.
 */
public class InventoryView {

    public Node getView() {
        // Root container
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("content-area");

        // Title
        Label title = new Label("Parts Management");
        title.getStyleClass().add("content-title");
        root.getChildren().add(title);

        // Filter bar: search, category, sort
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or ID...");
        searchField.getStyleClass().add("form-field");

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Category");
        categoryCombo.getItems().add("All");
        // TODO: load categories from DB and add to categoryCombo
        categoryCombo.setValue("All");

        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.setPromptText("Sort by");
        sortCombo.getItems().addAll("ID", "Name", "Category", "Qty");
        sortCombo.setValue("ID");

        Button applyFilterBtn = new Button("Apply Filters");
        applyFilterBtn.getStyleClass().add("action-button");
        applyFilterBtn.setOnAction(e -> {
            // TODO: Query DB for parts matching searchField, categoryCombo, sortCombo
        });

        HBox filterBar = new HBox(10, searchField, categoryCombo, sortCombo, applyFilterBtn);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.getStyleClass().add("filter-bar");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        root.getChildren().add(filterBar);

        // CRUD toolbar
        Button addBtn = new Button("Add Part");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> {
            // TODO: Show dialog to add a new part, then refresh list
        });

        Button editBtn = new Button("Edit Part");
        editBtn.getStyleClass().add("secondary-button");
        editBtn.setOnAction(e -> {
            // TODO: Get selected part from list and show edit dialog
        });

        Button deleteBtn = new Button("Delete Part");
        deleteBtn.getStyleClass().add("secondary-button");
        deleteBtn.setOnAction(e -> {
            // TODO: Delete selected part from DB, then refresh list
        });

        ToolBar crudBar = new ToolBar(addBtn, editBtn, deleteBtn);
        root.getChildren().add(crudBar);

        // List of parts
        ListView<String> partsList = new ListView<>();
        partsList.setPrefHeight(400);
        partsList.getStyleClass().add("table-view");
        partsList.setItems(FXCollections.observableArrayList(
            // TODO: replace with actual part descriptions: "ID - Name (Category) [Qty]"
            "101 - Brake Pad (Brakes) [45]",
            "102 - Oil Filter (Engine) [25]",
            "103 - Spark Plug (Ignition) [30]"
        ));
        root.getChildren().add(partsList);

        return root;
    }
}