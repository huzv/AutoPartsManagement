package views;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import mainapp.App;

public class Sidebar {
    private final VBox sidebarPane;
    private Button lastActiveButton = null;
    private final App appRef;

    public Sidebar(App appRef) {
        this.appRef = appRef;
        this.sidebarPane = createSidebar();
        setActiveButton("Dashboard");
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);

        Label titleLabel = new Label("Platinum Autoparts");
        titleLabel.getStyleClass().add("sidebar-title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);

        Button dashboardButton = createSidebarButton("Dashboard");
        Button inventoryButton = createSidebarButton("Inventory");
        Button suppliersButton = createSidebarButton("Suppliers & Customers");
        Button categoriesButton = createSidebarButton("Categories");
        Button aboutButton = createSidebarButton("About");

        dashboardButton.setOnAction(e -> appRef.navigateToView("Dashboard"));
        inventoryButton.setOnAction(e -> appRef.navigateToView("Inventory"));
        suppliersButton.setOnAction(e -> appRef.navigateToView("Suppliers & Customers"));
        categoriesButton.setOnAction(e -> appRef.navigateToView("Categories"));
        aboutButton.setOnAction(e -> appRef.navigateToView("About"));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
            titleLabel, dashboardButton, inventoryButton,
            suppliersButton, categoriesButton, spacer, aboutButton
        );

        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text.toUpperCase());
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    public void setActiveButton(String viewName) {
        if (lastActiveButton != null) {
            lastActiveButton.getStyleClass().remove("active");
        }

        for (Node node : sidebarPane.getChildren()) {
            if (node instanceof Button btn) {
                if (btn.getText().equalsIgnoreCase(viewName)) {
                    btn.getStyleClass().add("active");
                    lastActiveButton = btn;
                    break;
                }
            }
        }
    }

    public VBox getView() {
        return sidebarPane;
    }
}