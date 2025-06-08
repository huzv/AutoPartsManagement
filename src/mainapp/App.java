package mainapp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import views.Sidebar;
import views.ViewManager;

public class App extends Application {
    private BorderPane rootLayout;
    private Sidebar sidebar;
    private StackPane contentArea;
    private ViewManager viewManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Platinum System");

        rootLayout = new BorderPane();
        rootLayout.getStyleClass().add("main-border-pane");

        sidebar = new Sidebar(this);
        rootLayout.setLeft(sidebar.getView());
        
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        rootLayout.setCenter(contentArea);

        viewManager = new ViewManager();

        navigateToView("Dashboard");

        Scene scene = new Scene(rootLayout, 1300, 850);
        scene.getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void navigateToView(String viewName) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(viewManager.getView(viewName));
        sidebar.setActiveButton(viewName);
    }
}
