package views;

import javafx.scene.Node;

public class ViewManager {
    private final DashboardView dashboardView;
    private final InventoryView inventoryView;
    private final SuppliersView suppliersView;
    private final CategoriesView categoriesView;
    private final AboutView aboutView;

    public ViewManager() {
        this.dashboardView = new DashboardView();
        this.inventoryView = new InventoryView();
        this.suppliersView = new SuppliersView();
        this.categoriesView = new CategoriesView();
        this.aboutView = new AboutView();
    }

    public Node getView(String viewName) {
        switch (viewName) {
            case "Dashboard":
                return dashboardView.getView();
            case "Inventory":
                return inventoryView.getView();
            case "Suppliers & Customers":
                return suppliersView.getView();
            case "Categories":
                return categoriesView.getView();
            case "About":
                return aboutView.getView();
            default:
                return dashboardView.getView();
        }
    }
}
