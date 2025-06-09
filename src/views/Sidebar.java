package views;

import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import mainapp.App;

public class Sidebar {
    private final VBox sidebarPane;
    private Button lastActiveButton = null;
    private final App appRef;
    private final String role;

    /**
     * Sidebar shows buttons based on user role.
     * @param appRef reference to main App for navigation
     * @param role user's role ('admin' or 'user')
     */
    public Sidebar(App appRef, String role) {
        this.appRef = appRef;
        this.role = (role != null ? role.toLowerCase() : "user");
        this.sidebarPane = createSidebar();
        setActiveButton(getDefaultView());
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);

        Label titleLabel = new Label("Platinum Autoparts");
        titleLabel.getStyleClass().add("sidebar-title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);

        sidebar.getChildren().add(titleLabel);

        if ("admin".equals(role)) {
            // Admin sees all views.
            addButton(sidebar, "Dashboard");
            addButton(sidebar, "Inventory");
            addButton(sidebar, "Suppliers");
            addButton(sidebar, "Categories");
            addButton(sidebar, "Customers & Orders");
            addButton(sidebar, "User Management");
        } else {
            // Regular users see only their view.
            addButton(sidebar, "UserView");
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);
        addButton(sidebar, "Settings");
        addButton(sidebar, "About");
        addButton(sidebar, "Logout");

        return sidebar;
    }

    /**
     * Returns the default view name for this role
     */
    private String getDefaultView() {
        return "admin".equals(role) ? "Dashboard" : "UserView";
    }
    private void addButton(VBox container, String viewName) {
        Button btn = new Button(viewName);
        btn.getStyleClass().add("sidebar-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            if ("Logout".equalsIgnoreCase(viewName)) {
                // Confirm logout
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Logout");
                alert.setHeaderText("Are you sure you want to log out?");
                alert.setContentText("Press OK to log out or Cancel to stay logged in.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    appRef.logout();
                }
            } else {
                appRef.navigateToView(viewName);
            }
        });
        container.getChildren().add(btn);
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