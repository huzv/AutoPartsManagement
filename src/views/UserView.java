package views;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import accessors.PartsDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.ImageManager;

public class UserView {
    private VBox view;
    private ObservableList<CartItem> cartItems;
    private Label cartCountLabel;
    private ListView<InventoryItem> inventoryList;
    
    // Cart item class
    public static class CartItem {
        private String id;
        private String name;
        private String category;
        private double price;
        private int quantity;
        
        public CartItem(String id, String name, String category, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.quantity = quantity;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public double getTotal() { return price * quantity; }
        
        // Setters
        public void setQuantity(int quantity) { this.quantity = quantity; }
        
        @Override
        public String toString() {
            return String.format("%s - $%.2f x %d = $%.2f", name, price, quantity, getTotal());
        }
    }
    
    // Inventory item class
    public static class InventoryItem {
        private String id;
        private String name;
        private String category;
        private double price;
        private int stock;
        private String description;
        private String imageUrl;
        
        public InventoryItem(String id, String name, String category, double price, int stock, String description, String imageUrl) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
            this.description = description;
            this.imageUrl = imageUrl;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
        public String getDescription() { return description; }
        public String getImageUrl() { return imageUrl; }
        
        @Override
        public String toString() {
            return String.format("%s - %s - $%.2f (Stock: %d)", name, category, price, stock);
        }
    }
    
    public UserView() {
        this.cartItems = FXCollections.observableArrayList();
        initializeView();
    }
    
    private void initializeView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        view.getStyleClass().add("content-area");
        
        // Header with title and cart
        HBox header = createHeader();
        view.getChildren().add(header);
        
        // Search and filter section
        HBox searchSection = createSearchSection();
        view.getChildren().add(searchSection);
        
        // Inventory section
        VBox inventorySection = createInventorySection();
        view.getChildren().add(inventorySection);
        loadInventoryFromDatabase();
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        
        // Title
        Label titleLabel = new Label("Platinum Autoparts - Customer Portal");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.getStyleClass().add("content-title");
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Shopping cart button
        Button cartButton = createCartButton();
        
        header.getChildren().addAll(titleLabel, spacer, cartButton);
        return header;
    }
    
    private Button createCartButton() {
        Button cartButton = new Button();
        cartButton.getStyleClass().add("primary-button");
        
        // Create cart icon with count
        HBox cartContent = new HBox(5);
        cartContent.setAlignment(Pos.CENTER);
        
        try {
            // Load shopping cart image
            Image cartImage = new Image(getClass().getResourceAsStream("/shopping-cart.png"));
            ImageView cartIcon = new ImageView(cartImage);
            cartIcon.setFitWidth(24);
            cartIcon.setFitHeight(24);
            cartContent.getChildren().add(cartIcon);
        } catch (Exception e) {
            // Fallback if image not found
            Label cartIcon = new Label("🛒");
            cartIcon.setFont(Font.font(18));
            cartContent.getChildren().add(cartIcon);
        }
        
        cartCountLabel = new Label("0");
        cartCountLabel.getStyleClass().add("cart-count");
        cartCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        cartCountLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 2 6;");
        
        cartContent.getChildren().add(cartCountLabel);
        cartButton.setGraphic(cartContent);
        cartButton.setText("Cart");
        
        cartButton.setOnAction(e -> showCartWindow());
        
        return cartButton;
    }
    
    private HBox createSearchSection() {
        HBox searchSection = new HBox(10);
        searchSection.setAlignment(Pos.CENTER_LEFT);
        searchSection.getStyleClass().add("filter-bar");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search parts by name or ID...");
        searchField.getStyleClass().add("form-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Category");
        categoryFilter.getItems().addAll("All", "Brakes", "Engine", "Ignition", "Transmission", "Electrical");
        categoryFilter.setValue("All");
        
        ComboBox<String> sortBy = new ComboBox<>();
        sortBy.setPromptText("Sort by");
        sortBy.getItems().addAll("Name", "Price (Low to High)", "Price (High to Low)", "Category");
        sortBy.setValue("Name");
        
        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("action-button");
        searchButton.setOnAction(e -> {
            // TODO: Filter inventory based on search criteria
        });
        
        searchSection.getChildren().addAll(searchField, categoryFilter, sortBy, searchButton);
        return searchSection;
    }
    
    private VBox createInventorySection() {
        VBox inventorySection = new VBox(10);
        
        Label inventoryTitle = new Label("Available Parts");
        inventoryTitle.getStyleClass().add("section-title");
        inventoryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Create inventory list
        inventoryList = new ListView<>();
        inventoryList.setPrefHeight(400);
        inventoryList.getStyleClass().add("table-view");
        
        // Custom cell factory for inventory items
        inventoryList.setCellFactory(listView -> new ListCell<InventoryItem>() {
            @Override
            protected void updateItem(InventoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(createInventoryItemView(item));
                }
            }
        });
        
        inventorySection.getChildren().addAll(inventoryTitle, inventoryList);
        return inventorySection;
    }
    

    private HBox createInventoryItemView(InventoryItem item) {
        HBox itemBox = new HBox(15);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setPadding(new Insets(10));
        itemBox.getStyleClass().add("inventory-item");
        
        // Product image
        ImageView productImage = ImageManager.createProductImageView(item, 80, 80);
        productImage.getStyleClass().add("product-image");
        
        // Item details
        VBox details = new VBox(5);
        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label categoryLabel = new Label("Category: " + item.getCategory());
        categoryLabel.getStyleClass().add("text-muted");
        
        Label descriptionLabel = new Label(item.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.getStyleClass().add("text-muted");
        descriptionLabel.setMaxWidth(300); // Limit width for better layout
        
        details.getChildren().addAll(nameLabel, categoryLabel, descriptionLabel);
        HBox.setHgrow(details, Priority.ALWAYS);
        
        // Price and stock
        VBox priceStock = new VBox(5);
        priceStock.setAlignment(Pos.CENTER_RIGHT);
        
        Label priceLabel = new Label(String.format("$%.2f", item.getPrice()));
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        priceLabel.getStyleClass().add("price-label");
        
        Label stockLabel = new Label(String.format("Stock: %d", item.getStock()));
        stockLabel.getStyleClass().add("text-muted");
        
        priceStock.getChildren().addAll(priceLabel, stockLabel);
        
        // Add to cart section
        HBox addToCartSection = new HBox(10);
        addToCartSection.setAlignment(Pos.CENTER_RIGHT);
        
        Spinner<Integer> quantitySpinner = new Spinner<>(1, item.getStock(), 1);
        quantitySpinner.setPrefWidth(70);
        quantitySpinner.setEditable(true);
        
        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.getStyleClass().add("secondary-button");
        addToCartBtn.setOnAction(e -> addToCart(item, quantitySpinner.getValue()));
        
        addToCartSection.getChildren().addAll(new Label("Qty:"), quantitySpinner, addToCartBtn);
        
        // Add all components including the image
        itemBox.getChildren().addAll(productImage, details, priceStock, addToCartSection);
        return itemBox;
    }
    
    private void addToCart(InventoryItem item, int quantity) {
        // Check if item already exists in cart
        CartItem existingItem = cartItems.stream()
            .filter(cartItem -> cartItem.getId().equals(item.getId()))
            .findFirst()
            .orElse(null);
            
        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Add new item
            cartItems.add(new CartItem(item.getId(), item.getName(), item.getCategory(), item.getPrice(), quantity));
        }
        
        updateCartCount();
        
        // Show confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Added to Cart");
        alert.setHeaderText(null);
        alert.setContentText(String.format("%d x %s added to cart!", quantity, item.getName()));
        alert.showAndWait();
    }
    
    private void updateCartCount() {
        int totalItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        cartCountLabel.setText(String.valueOf(totalItems));
    }
    
    private void showCartWindow() {
        Stage cartStage = new Stage();
        cartStage.setTitle("Shopping Cart");
        cartStage.initModality(Modality.APPLICATION_MODAL);
        cartStage.setWidth(700);
        cartStage.setHeight(600);
        
        VBox cartLayout = new VBox(20);
        cartLayout.setPadding(new Insets(20));
        cartLayout.getStyleClass().add("content-area");
        
        // Cart title
        Label cartTitle = new Label("Shopping Cart");
        cartTitle.getStyleClass().add("content-title");
        cartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Cart items list
        ListView<CartItem> cartListView = new ListView<>(cartItems);
        cartListView.setPrefHeight(250);
        cartListView.getStyleClass().add("table-view");
        
        cartListView.setCellFactory(listView -> new ListCell<CartItem>() {
            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(createCartItemView(item, cartListView));
                }
            }
        });
        
        // Payment section
        VBox paymentSection = createPaymentSection();
        
        // Total section
        HBox totalSection = createTotalSection();
        
        // Action buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        
        Button continueShoppingBtn = new Button("Continue Shopping");
        continueShoppingBtn.getStyleClass().add("secondary-button");
        continueShoppingBtn.setOnAction(e -> cartStage.close());
        
        Button checkoutBtn = new Button("Proceed to Checkout");
        checkoutBtn.getStyleClass().add("primary-button");
        checkoutBtn.setOnAction(e -> {
            // TODO: Process checkout
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Placed");
            alert.setHeaderText(null);
            alert.setContentText("Your order has been placed successfully!");
            alert.showAndWait();
            cartItems.clear();
            updateCartCount();
            cartStage.close();
        });
        
        actionButtons.getChildren().addAll(continueShoppingBtn, checkoutBtn);
        
        cartLayout.getChildren().addAll(cartTitle, cartListView, paymentSection, totalSection, actionButtons);
        
        Scene cartScene = new Scene(cartLayout);
        cartStage.setScene(cartScene);
        cartStage.show();
    }

    // In your UserView constructor or initialization method
private void loadInventoryFromDatabase() {
    try {
        // Replace with your actual database connection
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/CarPartsDB", 
            "root", 
            "ayyskillz12"
        );
        
        PartsDAO partsDAO = new PartsDAO(conn);
        ObservableList<InventoryItem> inventoryFromDB = 
            FXCollections.observableArrayList(partsDAO.getAllParts());
        
        inventoryList.setItems(inventoryFromDB);
        
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle error - maybe show an alert or load sample data
    }
}
    
    private HBox createCartItemView(CartItem item, ListView<CartItem> listView) {
        HBox itemBox = new HBox(15);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setPadding(new Insets(10));
        
        // Item details
        VBox details = new VBox(5);
        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label categoryLabel = new Label("Category: " + item.getCategory());
        categoryLabel.getStyleClass().add("text-muted");
        
        details.getChildren().addAll(nameLabel, categoryLabel);
        HBox.setHgrow(details, Priority.ALWAYS);
        
        // Quantity and price
        VBox quantityPrice = new VBox(5);
        quantityPrice.setAlignment(Pos.CENTER_RIGHT);
        
        HBox quantityBox = new HBox(5);
        quantityBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label qtyLabel = new Label("Qty:");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 99, item.getQuantity());
        quantitySpinner.setPrefWidth(60);
        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            item.setQuantity(newVal);
            listView.refresh();
        });
        
        quantityBox.getChildren().addAll(qtyLabel, quantitySpinner);
        
        Label priceLabel = new Label(String.format("$%.2f", item.getTotal()));
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        quantityPrice.getChildren().addAll(quantityBox, priceLabel);
        
        // Remove button
        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("secondary-button");
        removeBtn.setOnAction(e -> {
            cartItems.remove(item);
            updateCartCount();
        });
        
        itemBox.getChildren().addAll(details, quantityPrice, removeBtn);
        return itemBox;
    }
    
    private VBox createPaymentSection() {
        VBox paymentSection = new VBox(10);
        
        Label paymentTitle = new Label("Payment Method");
        paymentTitle.getStyleClass().add("section-title");
        paymentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        ToggleGroup paymentGroup = new ToggleGroup();
        
        RadioButton creditCardRadio = new RadioButton("Credit Card");
        creditCardRadio.setToggleGroup(paymentGroup);
        creditCardRadio.setSelected(true);
        
        RadioButton debitCardRadio = new RadioButton("Debit Card");
        debitCardRadio.setToggleGroup(paymentGroup);
        
        RadioButton paypalRadio = new RadioButton("PayPal");
        paypalRadio.setToggleGroup(paymentGroup);
        
        RadioButton cashRadio = new RadioButton("Cash on Delivery");
        cashRadio.setToggleGroup(paymentGroup);
        
        VBox paymentOptions = new VBox(5);
        paymentOptions.getChildren().addAll(creditCardRadio, debitCardRadio, paypalRadio, cashRadio);
        
        paymentSection.getChildren().addAll(paymentTitle, paymentOptions);
        return paymentSection;
    }
    
    private HBox createTotalSection() {
        HBox totalSection = new HBox();
        totalSection.setAlignment(Pos.CENTER_RIGHT);
        totalSection.setPadding(new Insets(10));
        totalSection.getStyleClass().add("total-section");
        
        VBox totals = new VBox(5);
        totals.setAlignment(Pos.CENTER_RIGHT);
        
        double subtotal = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        double tax = subtotal * 0.08; // 8% tax
        double shipping = subtotal > 50 ? 0 : 10; // Free shipping over $50
        double total = subtotal + tax + shipping;
        
        Label subtotalLabel = new Label(String.format("Subtotal: $%.2f", subtotal));
        Label taxLabel = new Label(String.format("Tax (8%%): $%.2f", tax));
        Label shippingLabel = new Label(String.format("Shipping: $%.2f", shipping));
        Label totalLabel = new Label(String.format("Total: $%.2f", total));
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.getStyleClass().add("total-label");
        
        totals.getChildren().addAll(subtotalLabel, taxLabel, shippingLabel, new Separator(), totalLabel);
        
        totalSection.getChildren().add(totals);
        return totalSection;
    }
    
    public Node getView() {
        return view;
    }
}