package views;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;

public class DashboardView {

    public Node getView() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(30));
    
        // Centered Title
        Label title = new Label("Dashboard Overview");
        title.getStyleClass().add("content-title");
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        dashboard.getChildren().add(titleBox);
    
        // Filter Bar with GridPane
        DatePicker fromDate = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker toDate = new DatePicker(LocalDate.now());
        Button applyFilter = new Button("Apply Filters");
        applyFilter.setOnAction(e -> refreshMetrics(fromDate.getValue(), toDate.getValue()));
        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(10);
        filterGrid.setVgap(5);
        filterGrid.add(new Label("From:"), 0, 0);
        filterGrid.add(fromDate, 1, 0);
        filterGrid.add(new Label("To:"), 2, 0);
        filterGrid.add(toDate, 3, 0);
        filterGrid.add(applyFilter, 4, 0);
        filterGrid.getStyleClass().add("filter-bar");
        dashboard.getChildren().add(filterGrid);
    
        // Summary Grid
        GridPane summary = new GridPane();
        summary.setHgap(30);
        summary.setVgap(30);
        summary.add(createCard("Total Products", fetchTotalProducts()), 0, 0);
        summary.add(createCard("Products Worth", formatCurrency(fetchProductsWorth())), 1, 0);
        PieChart categoriesChart = createCategoryPieChart();
        summary.add(categoriesChart, 2, 0);
        dashboard.getChildren().add(summary);
    
        // Revenue Line Chart
        LineChart<String, Number> revenueChart = createRevenueLineChart();
        VBox revenueBox = new VBox(8, new Label("Revenue Over Time"), revenueChart);
        revenueBox.getStyleClass().add("chart-container");
        dashboard.getChildren().add(revenueBox);
    
        // Fade Animation
        FadeTransition ft = new FadeTransition(Duration.millis(600), dashboard);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    
        // Scroll Pane
        ScrollPane scroll = new ScrollPane(dashboard);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    
        return scroll;
    }

    private VBox createCard(String titleText, String valueText) {
        VBox box = new VBox(4);
        box.getStyleClass().add("card");
        Label t = new Label(titleText.toUpperCase());
        t.getStyleClass().add("card-title");
        Label v = new Label(valueText);
        v.getStyleClass().add("card-value");
        box.getChildren().addAll(t, v);
        return box;
    }

    private PieChart createCategoryPieChart() {
        // TODO: Replace with real data fetch
        // Example backend call:
        // Map<String, Integer> data = DB.fetchCategoryCounts(fromDate, toDate);
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
            new PieChart.Data("Brakes", 45),
            new PieChart.Data("Engine", 25),
            new PieChart.Data("Ignition", 30)
        );
        PieChart chart = new PieChart(pieData);
        chart.setTitle("Products by Category");
        chart.setLabelsVisible(false);
        chart.getStyleClass().add("pie-chart");
        return chart;
    }

    private LineChart<String, Number> createRevenueLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenue");
        
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Revenue Trend");
        lineChart.setPrefSize(600, 400);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        // Example data with dates (replace with real data fetch)
        series.getData().add(new XYChart.Data<>("2023-01-01", 2000));
        series.getData().add(new XYChart.Data<>("2023-01-02", 4500));
        series.getData().add(new XYChart.Data<>("2023-01-03", 3200));
        series.getData().add(new XYChart.Data<>("2023-01-04", 5800));
        series.getData().add(new XYChart.Data<>("2023-01-05", 6100));
        lineChart.getData().add(series);
        lineChart.getStyleClass().add("line-chart");
        
        return lineChart;
    }

    // --- Backend fetch methods (to be implemented) ---

    /**
     * Fetches total number of products from the DB.
     */
    private String fetchTotalProducts() {
        // TODO: SELECT COUNT(*) FROM products;
        return "120"; // placeholder
    }

    /**
     * Fetches total worth of all products (sum of price*quantity).
     */
    private double fetchProductsWorth() {
        // TODO: SELECT SUM(price * stock) FROM products;
        return 45230.75; // placeholder
    }

    /**
     * Formats a double as currency (e.g. $45,230.75).
     */
    private String formatCurrency(double amount) {
        // TODO: use NumberFormat.getCurrencyInstance()
        return String.format("$%,.2f", amount);
    }

    /**
     * Refreshes metrics when filter is applied.
     */
    private void refreshMetrics(LocalDate from, LocalDate to) {
        // TODO: clear old data and re-fetch using date range
        System.out.println("Refreshing metrics from " + from + " to " + to);
    }
}