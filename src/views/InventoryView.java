package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.DatabaseManager;

public class InventoryView {
    private final ObservableList<String> tableNames = FXCollections.observableArrayList();
    private final DatabaseManager databaseManager;

    public InventoryView() {
        this.databaseManager = new DatabaseManager();
    }

    public Node getView() {
        VBox inventoryLayout = new VBox(15);
        inventoryLayout.setPadding(new Insets(20));
        inventoryLayout.getStyleClass().add("content-area");

        Label title = new Label("Parts Management");
        title.getStyleClass().add("content-title");
        inventoryLayout.getChildren().add(title);

        loadTableNames();
        ListView<String> listView = new ListView<>(tableNames);
        listView.setPrefSize(300, 400);
        VBox tableDisplayBox = new VBox(10, new Label("Database Tables:"), listView);
        tableDisplayBox.setStyle("-fx-padding: 10;");
        inventoryLayout.getChildren().add(tableDisplayBox);

        HBox controlsBar = createControlsBar();
        inventoryLayout.getChildren().add(controlsBar);

        TableView<String> table = createInventoryTable();
        inventoryLayout.getChildren().add(table);

        HBox buttonBar = createButtonBar();
        inventoryLayout.getChildren().add(buttonBar);

        return inventoryLayout;
    }

    private HBox createControlsBar() {
        HBox controlsBar = new HBox(15);
        controlsBar.setAlignment(Pos.CENTER_LEFT);

        Label searchLabel = new Label("Search:");
        TextField searchField = new TextField();
        searchField.setPromptText("Name or Part ID…");
        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("action-button");
        Button clearSearchBtn = new Button("Clear");
        clearSearchBtn.getStyleClass().add("secondary-button");

        Label categoryFilterLabel = new Label("Category:");
        ComboBox<String> categoryFilterCombo = new ComboBox<>();
        categoryFilterCombo.getItems().add("All");
        categoryFilterCombo.setValue("All");

        Label sortLabel = new Label("Sort by:");
        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("ID", "Name", "Category");
        sortCombo.setValue("ID");

        controlsBar.getChildren().addAll(
                searchLabel, searchField, searchBtn, clearSearchBtn,
                new Separator(Orientation.VERTICAL),
                new Separator(Orientation.VERTICAL),
                categoryFilterLabel, categoryFilterCombo,
                new Separator(Orientation.VERTICAL),
                sortLabel, sortCombo);

        return controlsBar;
    }

    private TableView<String> createInventoryTable() {
        TableView<String> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(450);

        TableColumn<String, String> idCol = new TableColumn<>("Part ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("partID"));

        TableColumn<String, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<String, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        TableColumn<String, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer qty, boolean empty) {
                super.updateItem(qty, empty);
                if (empty || qty == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(qty.toString());
                }
            }
        });

        table.getColumns().setAll(idCol, nameCol, categoryCol, qtyCol);
        return table;
    }

    private HBox createButtonBar() {
        HBox buttonBar = new HBox(10);
        buttonBar.getStyleClass().add("button-bar");

        Button addButton = new Button("Add Part");
        addButton.getStyleClass().add("action-button");

        Button editButton = new Button("Edit Part");
        editButton.getStyleClass().add("secondary-button");

        Button deleteButton = new Button("Delete Part");
        deleteButton.getStyleClass().add("secondary-button");
        deleteButton.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white;");

        buttonBar.getChildren().addAll(addButton, editButton, deleteButton);
        return buttonBar;
    }

    private void loadTableNames() {
        tableNames.clear();
        tableNames.addAll(databaseManager.getTableNames());
    }
}