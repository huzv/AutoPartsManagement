package mainapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import views.Sidebar;
import views.ViewManager;
import views.LoginView;
import authentication.UserAuthentication;
import utils.AlertHelper;
import utils.DatabaseManager;
import utils.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class App extends Application {
    private BorderPane rootLayout;
    private Sidebar sidebar;
    private StackPane contentArea;
    private ViewManager viewManager;
    private UserAuthentication userAuth;
    private DatabaseManager dbManager;
    private LoginView loginView;
    private boolean isLoggedIn = false;
    private String userRole;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Platinum System");
    
        // Initialize and test database connection
        dbManager = new DatabaseManager();
        try (Connection conn = dbManager.getConnection()) {
            if (conn == null || conn.isClosed()) {
                AlertHelper.showErrorAndExit("Failed to connect to the database. Please check your configuration.");
                return;
            }
        } catch (Exception e) {
            AlertHelper.showErrorAndExit("Database connection error: " + e.getMessage());
            return;
        }
    
        userAuth = new UserAuthentication();
        loginView = new LoginView(this);
    
        showLoginScreen();
    
        Scene scene = new Scene(loginView.getView(), 1300, 850);
        scene.getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showLoginScreen() {
        isLoggedIn = false;
    }

    /**
     * Called by LoginView when authentication succeeds.
     * Sets the UserSession and initializes the main layout.
     */
    public void onLoginSuccess(String username) throws Exception {
        // Load full user data and set session
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
               "SELECT user_id, email, role, theme_preference FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String email = rs.getString("email");
                    String role  = rs.getString("role");
                    String theme  = rs.getString("theme_preference");
                    UserSession.setCurrentUser(
                        new UserSession.User(userId, username, email, role, theme)
                    );
                }
            }
        }

        // Mark logged in and capture role
        isLoggedIn = true;
        userRole    = UserSession.getCurrentUser().getRole();

        initializeMainApplication();
    }

    private void initializeMainApplication() throws Exception {
        rootLayout = new BorderPane();
        rootLayout.getStyleClass().add("main-border-pane");
    
        // Sidebar
        sidebar = new Sidebar(this, userRole);
        rootLayout.setLeft(sidebar.getView());
    
        // Content area
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        rootLayout.setCenter(contentArea);
    
        // View manager
        viewManager = new ViewManager();
        if ("admin".equalsIgnoreCase(userRole)) {
            navigateToView("Dashboard");
        } else {
            contentArea.getChildren().add(viewManager.getView("UserView"));
            sidebar.setActiveButton("UserView");
        }
    
        // Build scene
        Scene scene = new Scene(rootLayout, 1300, 850);
    
        // Apply user's saved theme preference
        String theme = UserSession.getCurrentUser().getThemePreference();
        scene.getStylesheets().clear();
        if ("dark".equalsIgnoreCase(theme)) {
            scene.getStylesheets().add(
                getClass().getResource("/resources/dark-theme.css").toExternalForm()
            );
        } else {
            scene.getStylesheets().add(
                getClass().getResource("/resources/styles.css").toExternalForm()
            );
        }
    
        // Swap into the existing stage
        Stage stage = (Stage) loginView.getView().getScene().getWindow();
        stage.setScene(scene);
    }

    public void navigateToView(String viewName) {
        if (!isLoggedIn) return;
        contentArea.getChildren().clear();
        contentArea.getChildren().add(viewManager.getView(viewName));
        sidebar.setActiveButton(viewName);
    }

    public void logout() {
        isLoggedIn = false;
        UserSession.clearSession();
        loginView = new LoginView(this);
        Stage stage = (Stage) rootLayout.getScene().getWindow();
        Scene scene = new Scene(loginView.getView(), 1300, 850);
        scene.getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());
        stage.setScene(scene);
    }

    public UserAuthentication getUserAuth() {
        return userAuth;
    }
}
