
package views;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * SuppliersView shows supplier management UI with filtering and CRUD operations.
 * Backend calls are commented as TODOs where database integration should occur.
 */
public class SuppliersView {

    public Node getView() {
        // Main container
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("content-area");

        // Title
        Label title = new Label("Supplier Management");
        title.getStyleClass().add("content-title");
        root.getChildren().add(title);

        // Filter bar: search + date range
        TextField searchField = new TextField();
        searchField.setPromptText("Search suppliers by name or contact...");
        searchField.getStyleClass().add("form-field");
        
        DatePicker fromDate = new DatePicker();
        fromDate.setPromptText("From Date");
        DatePicker toDate = new DatePicker();
        toDate.setPromptText("To Date");
        
        Button applyFilter = new Button("Apply Filters");
        applyFilter.getStyleClass().add("action-button");
        applyFilter.setOnAction(e -> {
            // TODO: fetch filtered supplier list from DB using searchField.getText(), fromDate.getValue(), toDate.getValue()
        });

        Button clearFilter = new Button("Clear");
        clearFilter.getStyleClass().add("secondary-button");
        clearFilter.setOnAction(e -> {
            searchField.clear();
            fromDate.setValue(null);
            toDate.setValue(null);
            // TODO: refresh full supplier list
        });

        HBox filterBar = new HBox(10, searchField, fromDate, toDate, applyFilter, clearFilter);
        filterBar.getStyleClass().add("filter-bar");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        root.getChildren().add(filterBar);

        // CRUD button toolbar
        Button addBtn = new Button("Add Supplier");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> {
            // TODO: show dialog to add new supplier, then refresh list
        });

        Button editBtn = new Button("Edit Selected");
        editBtn.getStyleClass().add("secondary-button");
        editBtn.setOnAction(e -> {
            // TODO: get selected supplier and show edit dialog
        });

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("secondary-button");
        deleteBtn.setOnAction(e -> {
            // TODO: delete selected supplier from DB and refresh list
        });

        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.getStyleClass().add("action-button");
        viewDetailsBtn.setOnAction(e -> {
            // TODO: show detailed supplier information dialog
        });

        ToolBar toolbar = new ToolBar(addBtn, editBtn, deleteBtn, viewDetailsBtn);
        root.getChildren().add(toolbar);

        // Supplier list view
        ListView<String> supplierListView = new ListView<>(FXCollections.observableArrayList(
            // TODO: replace placeholders with actual supplier data from DB
            "AutoParts Inc. - Contact: John Smith - Phone: +1-555-0123",
            "Quality Motors Supply - Contact: Sarah Johnson - Phone: +1-555-0456",
            "Premier Auto Components - Contact: Mike Davis - Phone: +1-555-0789",
            "Reliable Parts Co. - Contact: Lisa Wilson - Phone: +1-555-0321"
        ));
        supplierListView.setPrefHeight(400);
        supplierListView.getStyleClass().add("table-view");
        root.getChildren().add(supplierListView);

        return root;
    }
}