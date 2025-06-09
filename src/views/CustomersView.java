package views;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * CustomersView shows customer and order management with separate tables
 * for customers and orders, including CRUD operations for both.
 * Backend calls are commented as TODOs where database integration should occur.
 */
public class CustomersView {

    public Node getView() {
        // Main container
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("content-area");

        // Title
        Label title = new Label("Customers & Orders Management");
        title.getStyleClass().add("content-title");
        root.getChildren().add(title);

        // Filter bar for customers
        TextField customerSearchField = new TextField();
        customerSearchField.setPromptText("Search customers by name or email...");
        customerSearchField.getStyleClass().add("form-field");
        
        Button customerFilterBtn = new Button("Filter Customers");
        customerFilterBtn.getStyleClass().add("action-button");
        customerFilterBtn.setOnAction(e -> {
            // TODO: filter customer list based on search criteria
        });

        Button clearCustomerFilter = new Button("Clear");
        clearCustomerFilter.getStyleClass().add("secondary-button");
        clearCustomerFilter.setOnAction(e -> {
            customerSearchField.clear();
            // TODO: refresh full customer list
        });

        HBox customerFilterBar = new HBox(10, customerSearchField, customerFilterBtn, clearCustomerFilter);
        customerFilterBar.getStyleClass().add("filter-bar");
        HBox.setHgrow(customerSearchField, Priority.ALWAYS);
        root.getChildren().add(customerFilterBar);

        // Customer CRUD toolbar
        Button addCustomerBtn = new Button("Add Customer");
        addCustomerBtn.getStyleClass().add("primary-button");
        addCustomerBtn.setOnAction(e -> {
            // TODO: show dialog to add new customer, then refresh list
        });

        Button editCustomerBtn = new Button("Edit Customer");
        editCustomerBtn.getStyleClass().add("secondary-button");
        editCustomerBtn.setOnAction(e -> {
            // TODO: get selected customer and show edit dialog
        });

        Button deleteCustomerBtn = new Button("Delete Customer");
        deleteCustomerBtn.getStyleClass().add("secondary-button");
        deleteCustomerBtn.setOnAction(e -> {
            // TODO: delete selected customer from DB and refresh list
        });

        Button viewCustomerBtn = new Button("View Details");
        viewCustomerBtn.getStyleClass().add("action-button");
        viewCustomerBtn.setOnAction(e -> {
            // TODO: show detailed customer information
        });

        ToolBar customerToolbar = new ToolBar(addCustomerBtn, editCustomerBtn, deleteCustomerBtn, viewCustomerBtn);
        root.getChildren().add(customerToolbar);

        // Customer list section
        Label customerLabel = new Label("Customers");
        customerLabel.getStyleClass().add("section-title");
        root.getChildren().add(customerLabel);

        ListView<String> customerListView = new ListView<>(FXCollections.observableArrayList(
            // TODO: replace placeholders with actual customer data from DB
            "C001 - Robert Martinez - robert.martinez@email.com - Phone: +1-555-1001",
            "C002 - Emily Chen - emily.chen@email.com - Phone: +1-555-1002", 
            "C003 - David Thompson - david.thompson@email.com - Phone: +1-555-1003",
            "C004 - Jessica Rodriguez - jessica.rodriguez@email.com - Phone: +1-555-1004"
        ));
        customerListView.setPrefHeight(200);
        customerListView.getStyleClass().add("table-view");
        root.getChildren().add(customerListView);

        // Separator
        root.getChildren().add(new Separator());

        // Filter bar for orders
        TextField orderSearchField = new TextField();
        orderSearchField.setPromptText("Search orders by ID or customer...");
        orderSearchField.getStyleClass().add("form-field");

        ComboBox<String> orderStatusCombo = new ComboBox<>();
        orderStatusCombo.setPromptText("Order Status");
        orderStatusCombo.getItems().addAll("All", "Pending", "Processing", "Shipped", "Delivered", "Cancelled");
        orderStatusCombo.setValue("All");

        DatePicker orderFromDate = new DatePicker();
        orderFromDate.setPromptText("From Date");
        DatePicker orderToDate = new DatePicker();
        orderToDate.setPromptText("To Date");
        
        Button orderFilterBtn = new Button("Filter Orders");
        orderFilterBtn.getStyleClass().add("action-button");
        orderFilterBtn.setOnAction(e -> {
            // TODO: filter order list based on search criteria, status, and date range
        });

        Button clearOrderFilter = new Button("Clear");
        clearOrderFilter.getStyleClass().add("secondary-button");
        clearOrderFilter.setOnAction(e -> {
            orderSearchField.clear();
            orderStatusCombo.setValue("All");
            orderFromDate.setValue(null);
            orderToDate.setValue(null);
            // TODO: refresh full order list
        });

        HBox orderFilterBar = new HBox(10, orderSearchField, orderStatusCombo, orderFromDate, orderToDate, orderFilterBtn, clearOrderFilter);
        orderFilterBar.getStyleClass().add("filter-bar");
        HBox.setHgrow(orderSearchField, Priority.ALWAYS);
        root.getChildren().add(orderFilterBar);

        // Order CRUD toolbar
        Button addOrderBtn = new Button("Create Order");
        addOrderBtn.getStyleClass().add("primary-button");
        addOrderBtn.setOnAction(e -> {
            // TODO: show dialog to create new order, then refresh list
        });

        Button editOrderBtn = new Button("Edit Order");
        editOrderBtn.getStyleClass().add("secondary-button");
        editOrderBtn.setOnAction(e -> {
            // TODO: get selected order and show edit dialog
        });

        Button deleteOrderBtn = new Button("Cancel Order");
        deleteOrderBtn.getStyleClass().add("secondary-button");
        deleteOrderBtn.setOnAction(e -> {
            // TODO: cancel/delete selected order and refresh list
        });

        Button viewOrderBtn = new Button("View Order");
        viewOrderBtn.getStyleClass().add("action-button");
        viewOrderBtn.setOnAction(e -> {
            // TODO: show detailed order information with items
        });

        Button updateStatusBtn = new Button("Update Status");
        updateStatusBtn.getStyleClass().add("action-button");
        updateStatusBtn.setOnAction(e -> {
            // TODO: show dialog to update order status
        });

        ToolBar orderToolbar = new ToolBar(addOrderBtn, editOrderBtn, deleteOrderBtn, viewOrderBtn, updateStatusBtn);
        root.getChildren().add(orderToolbar);

        // Orders list section
        Label orderLabel = new Label("Orders");
        orderLabel.getStyleClass().add("section-title");
        root.getChildren().add(orderLabel);

        ListView<String> orderListView = new ListView<>(FXCollections.observableArrayList(
            // TODO: replace placeholders with actual order data from DB
            "ORD-001 - Robert Martinez - 2024-01-15 - $245.50 - Processing",
            "ORD-002 - Emily Chen - 2024-01-14 - $189.75 - Shipped",
            "ORD-003 - David Thompson - 2024-01-13 - $320.25 - Delivered",
            "ORD-004 - Jessica Rodriguez - 2024-01-12 - $156.00 - Pending",
            "ORD-005 - Robert Martinez - 2024-01-11 - $89.99 - Delivered"
        ));
        orderListView.setPrefHeight(250);
        orderListView.getStyleClass().add("table-view");
        root.getChildren().add(orderListView);

        return root;
    }
}