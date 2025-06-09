package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import mainapp.App;
import authentication.UserAuthentication;
import utils.AlertHelper;

public class LoginView {
    private VBox view;
    private App app;
    private UserAuthentication userAuth;
    private boolean isLoginMode = true;
    
    // Login fields
    private TextField loginUsernameField;
    private PasswordField loginPasswordField;
    
    // Registration fields
    private TextField regUsernameField;
    private TextField regEmailField;
    private PasswordField regPasswordField;
    private PasswordField regConfirmPasswordField;
    
    // Containers
    private VBox loginContainer;
    private VBox registerContainer;
    private Button toggleModeButton;
    private Label titleLabel;

    public LoginView(App app) {
        this.app = app;
        this.userAuth = app.getUserAuth();
        initializeView();
    }

    private void initializeView() {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(50));
        view.getStyleClass().add("login-container");

        // Title
        titleLabel = new Label("Login to Platinum System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.getStyleClass().add("login-title");

        // Create login form
        createLoginForm();
        
        // Create registration form
        createRegistrationForm();
        
        // Toggle button
        toggleModeButton = new Button("Don't have an account? Sign Up");
        toggleModeButton.getStyleClass().add("toggle-button");
        toggleModeButton.setOnAction(e -> toggleMode());

        view.getChildren().addAll(
            titleLabel,
            loginContainer,
            registerContainer,
            toggleModeButton
        );
        
        // then immediately hide the register form
        registerContainer.setVisible(false);
        registerContainer.setManaged(false);
    }

    private void createLoginForm() {
        loginContainer = new VBox(15);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setMaxWidth(400);
        loginContainer.getStyleClass().add("form-container");

        // Username field
        Label usernameLabel = new Label("Username:");
        usernameLabel.getStyleClass().add("form-label");
        loginUsernameField = new TextField();
        loginUsernameField.setPromptText("Enter your username");
        loginUsernameField.getStyleClass().add("form-field");

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("form-label");
        loginPasswordField = new PasswordField();
        loginPasswordField.setPromptText("Enter your password");
        loginPasswordField.getStyleClass().add("form-field");

        // Login button
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setOnAction(e -> {
            try {
                handleLogin();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        
        // Enter key support
        loginPasswordField.setOnAction(e -> {
            try {
                handleLogin();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        loginContainer.getChildren().addAll(
            usernameLabel, loginUsernameField,
            passwordLabel, loginPasswordField,
            loginButton
        );
    }

    private void createRegistrationForm() {
        registerContainer = new VBox(15);
        registerContainer.setAlignment(Pos.CENTER);
        registerContainer.setMaxWidth(400);
        registerContainer.getStyleClass().add("form-container");

        // Username field
        Label regUsernameLabel = new Label("Username:");
        regUsernameLabel.getStyleClass().add("form-label");
        regUsernameField = new TextField();
        regUsernameField.setPromptText("Choose a username");
        regUsernameField.getStyleClass().add("form-field");

        // Email field
        Label regEmailLabel = new Label("Email:");
        regEmailLabel.getStyleClass().add("form-label");
        regEmailField = new TextField();
        regEmailField.setPromptText("Enter your email");
        regEmailField.getStyleClass().add("form-field");

        // Password field
        Label regPasswordLabel = new Label("Password:");
        regPasswordLabel.getStyleClass().add("form-label");
        regPasswordField = new PasswordField();
        regPasswordField.setPromptText("Create a password");
        regPasswordField.getStyleClass().add("form-field");

        // Confirm password field
        Label regConfirmPasswordLabel = new Label("Confirm Password:");
        regConfirmPasswordLabel.getStyleClass().add("form-label");
        regConfirmPasswordField = new PasswordField();
        regConfirmPasswordField.setPromptText("Confirm your password");
        regConfirmPasswordField.getStyleClass().add("form-field");

        // Password requirements info
        Label passwordInfo = new Label("Password must be at least 8 characters with uppercase, lowercase, number, and special character");
        passwordInfo.getStyleClass().add("info-text");
        passwordInfo.setWrapText(true);

        // Register button
        Button registerButton = new Button("Sign Up");
        registerButton.getStyleClass().add("primary-button");
        registerButton.setOnAction(e -> handleRegistration());

        registerContainer.getChildren().addAll(
            regUsernameLabel, regUsernameField,
            regEmailLabel, regEmailField,
            regPasswordLabel, regPasswordField,
            regConfirmPasswordLabel, regConfirmPasswordField,
            passwordInfo, registerButton
        );
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        
        if (isLoginMode) {
            // Switch to login mode
            titleLabel.setText("Login to Platinum System");
            view.getChildren().clear();
            view.getChildren().addAll(titleLabel, loginContainer, toggleModeButton);
            toggleModeButton.setText("Don't have an account? Sign Up");
            
            registerContainer.setVisible(false);
            registerContainer.setManaged(false);
            loginContainer.setVisible(true);
            loginContainer.setManaged(true);
        } else {
            // Switch to registration mode
            titleLabel.setText("Create Account");
            view.getChildren().clear();
            view.getChildren().addAll(titleLabel, registerContainer, toggleModeButton);
            toggleModeButton.setText("Already have an account? Login");
            
            loginContainer.setVisible(false);
            loginContainer.setManaged(false);
            registerContainer.setVisible(true);
            registerContainer.setManaged(true);
        }
        
        // Clear all fields when switching modes
        clearFields();
    }

    private void handleLogin() throws Exception {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertHelper.showError("Input Error", "Please enter both username and password");
            return;
        }

        if (userAuth.authenticateUser(username, password)) {
            app.onLoginSuccess(username);
        } else {
            AlertHelper.showError("Login Failed", "Invalid username or password");
            loginPasswordField.clear();
        }
    }

    private void handleRegistration() {
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText();
        String confirmPassword = regConfirmPasswordField.getText();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            AlertHelper.showError("Input Error", "Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            AlertHelper.showError("Password Error", "Passwords do not match");
            regConfirmPasswordField.clear();
            return;
        }

        if (!isValidEmail(email)) {
            AlertHelper.showError("Email Error", "Please enter a valid email address");
            return;
        }

        if (userAuth.registerUser(username, email, password)) {
            AlertHelper.showAlert(AlertType.INFORMATION,"Success", "Account created successfully! You can now login.");
            toggleMode(); // Switch back to login mode
        }
        // Error handling is done in UserAuthentication class
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    private void clearFields() {
        loginUsernameField.clear();
        loginPasswordField.clear();
        regUsernameField.clear();
        regEmailField.clear();
        regPasswordField.clear();
        regConfirmPasswordField.clear();
    }

    public Parent getView() {
        return view;
    }
}
