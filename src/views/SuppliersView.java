package views;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class SuppliersView {
    
    public Node getView() {
        VBox suppliersLayout = new VBox(15);
        suppliersLayout.setPadding(new Insets(10));
        Label title = new Label("Suppliers & Customers Overview");
        title.getStyleClass().add("content-title");

        TableView<String> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(600);

        TableColumn<String, String> supIdCol = new TableColumn<>("Supplier ID");
        supIdCol.setCellValueFactory(new PropertyValueFactory<>("supplierID"));

        TableColumn<String, String> supNameCol = new TableColumn<>("Supplier Name");
        supNameCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        TableColumn<String, String> supAddressCol = new TableColumn<>("Address");
        supAddressCol.setCellValueFactory(new PropertyValueFactory<>("supplierAddress"));

        TableColumn<String, String> prodNameCol = new TableColumn<>("Part Name");
        prodNameCol.setCellValueFactory(new PropertyValueFactory<>("partName"));

        TableColumn<String, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<String, String> dateRecCol = new TableColumn<>("Date Received");
        dateRecCol.setCellValueFactory(new PropertyValueFactory<>("formattedDateReceived"));

        TableColumn<String, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("displayStatus"));

        table.getColumns().setAll(supIdCol, supNameCol, supAddressCol, prodNameCol, qtyCol, dateRecCol, statusCol);

        Button refreshButton = new Button("Refresh List");
        refreshButton.getStyleClass().add("action-button");

        suppliersLayout.getChildren().addAll(title, refreshButton, table);
        return suppliersLayout;
    }
}
