package views;

import javafx.scene.Node;

public class ViewManager {
    private final DashboardView dashboardView;
    private final InventoryView inventoryView;
    private final SuppliersView suppliersView;
    private final CategoriesView categoriesView;
    private final AboutView aboutView;
    private final UserManagementView userManagementView;
    private final UserView userView;
    private final SettingsView settingsView;
    private final CustomersView customersView;

    public ViewManager() throws Exception {
        this.dashboardView = new DashboardView();
        this.inventoryView = new InventoryView();
        this.suppliersView = new SuppliersView();
        this.categoriesView = new CategoriesView();
        this.aboutView = new AboutView();
        this.userManagementView = new UserManagementView();
        this.userView = new UserView();
        this.settingsView = new SettingsView();
        this.customersView = new CustomersView();
    }

    public Node getView(String viewName) {
        switch (viewName) {
            case "Dashboard":
                return dashboardView.getView();
            case "Inventory":
                return inventoryView.getView();
            case "Suppliers":
                return suppliersView.getView();
            case "Categories":
                return categoriesView.getView();
            case "User Management":
                return userManagementView.getView();
            case "UserView":
                return userView.getView();
            case "About":
                return aboutView.getView();
            case "Settings":
                return settingsView.getView();
            case "Customers & Orders":
                return customersView.getView();
            default:
                return dashboardView.getView();
        }
    }
}