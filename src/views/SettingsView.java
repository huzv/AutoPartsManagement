package views;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.DatabaseManager;
import utils.AlertHelper;
import utils.UserSession;
import authentication.UserAuthentication;

import java.sql.*;

public class SettingsView {
    private VBox root;
    private TextField emailField;
    private RadioButton lightTheme;
    private RadioButton darkTheme;
    private DatabaseManager dbManager;
    private UserAuthentication userAuth;
    private PasswordField currentPw;
    private PasswordField newPw;
    private PasswordField confirmPw;
    
    public SettingsView() throws Exception {
        this.dbManager = new DatabaseManager();
        this.userAuth = new UserAuthentication();
        initializeView();
        loadUserSettings();
    }
    
    private void initializeView() {
        root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("content-area");

        Label title = new Label("Settings");
        title.getStyleClass().add("content-title");
        root.getChildren().add(title);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        // Profile section
        grid.add(new Label("Email:"), 0, 0);
        emailField = new TextField();
        emailField.getStyleClass().add("form-field");
        grid.add(emailField, 1, 0);

        grid.add(new Label("Change Password:"), 0, 1);
        currentPw = new PasswordField();
        currentPw.setPromptText("Current Password");
        currentPw.getStyleClass().add("form-field");
        grid.add(currentPw, 1, 1);
        
        newPw = new PasswordField();
        newPw.setPromptText("New Password");
        newPw.getStyleClass().add("form-field");
        grid.add(newPw, 1, 2);
        
        confirmPw = new PasswordField();
        confirmPw.setPromptText("Confirm New Password");
        confirmPw.getStyleClass().add("form-field");
        grid.add(confirmPw, 1, 3);

        Button savePassword = new Button("Update Password");
        savePassword.getStyleClass().add("action-button");
        savePassword.setOnAction(e -> {
            try {
                updatePassword(currentPw.getText(), newPw.getText(), confirmPw.getText());
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        grid.add(savePassword, 1, 4);

        // Theme toggle
        grid.add(new Label("Theme:"), 0, 5);
        ToggleGroup themeGroup = new ToggleGroup();
        lightTheme = new RadioButton("Light");
        darkTheme = new RadioButton("Dark");
        lightTheme.setToggleGroup(themeGroup);
        darkTheme.setToggleGroup(themeGroup);
        lightTheme.setSelected(true); // Default to light theme
        
        // Add theme change listeners
        lightTheme.setOnAction(e -> applyTheme("light"));
        darkTheme.setOnAction(e -> applyTheme("dark"));
        
        HBox themeBox = new HBox(10, lightTheme, darkTheme);
        grid.add(themeBox, 1, 5);

        // Save all settings
        Button saveAll = new Button("Save Settings");
        saveAll.getStyleClass().add("primary-button");
        saveAll.setOnAction(e -> {
            try {
                saveAllSettings();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        grid.add(saveAll, 1, 6);

        root.getChildren().add(grid);
    }
    
    private void loadUserSettings() throws Exception {
        if (UserSession.getCurrentUser() != null) {
            try (Connection conn = dbManager.getConnection()) {
                String sql = "SELECT email, theme_preference FROM users WHERE user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, UserSession.getCurrentUser().getUserId());
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String dbEmail = rs.getString("email");
                            if (dbEmail != null && !dbEmail.isEmpty()) {
                                emailField.setText(dbEmail);
                                UserSession.getCurrentUser().setEmail(dbEmail);
                            }
                            String theme = rs.getString("theme_preference");
                            if ("dark".equals(theme)) {
                                darkTheme.setSelected(true);
                                applyTheme("dark");
                            } else {
                                lightTheme.setSelected(true);
                                applyTheme("light");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                AlertHelper.showError("Database Error", "Failed to load user settings: " + e.getMessage());
            }
        } else {
            // No user logged in
            emailField.setText("");
            lightTheme.setSelected(true);
        }
    }
    
    private void updatePassword(String currentPassword, String newPassword, String confirmPassword) throws Exception {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            AlertHelper.showError("Input Error", "Please fill in all password fields.");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            AlertHelper.showError("Password Error", "New passwords do not match.");
            return;
        }
        
        if (!isValidPassword(newPassword)) {
            AlertHelper.showError("Invalid Password", 
                "Password must be at least 8 characters with uppercase, lowercase, number, and special character.");
            return;
        }
        
        // Verify current password
        if (UserSession.getCurrentUser() != null) {
            try (Connection conn = dbManager.getConnection()) {
                String sql = "SELECT password_hash, salt FROM users WHERE user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, UserSession.getCurrentUser().getUserId());
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String storedHash = rs.getString("password_hash");
                            String salt = rs.getString("salt");
                            
                            String currentPasswordHash = userAuth.hashPasswordWithSalt(currentPassword, salt);
                            if (!currentPasswordHash.equals(storedHash)) {
                                AlertHelper.showError("Authentication Error", "Current password is incorrect.");
                                return;
                            }
                            
                            // Update password
                            String newSalt = userAuth.generateSalt();
                            String newPasswordHash = userAuth.hashPasswordWithSalt(newPassword, newSalt);
                            
                            String updateSql = "UPDATE users SET password_hash = ?, salt = ? WHERE user_id = ?";
                            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                                updatePstmt.setString(1, newPasswordHash);
                                updatePstmt.setString(2, newSalt);
                                updatePstmt.setInt(3, UserSession.getCurrentUser().getUserId());
                                
                                int result = updatePstmt.executeUpdate();
                                if (result > 0) {
                                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated successfully.");
                                    // Clear password fields
                                    currentPw.clear();
                                    newPw.clear();
                                    confirmPw.clear();
                                } else {
                                    AlertHelper.showError("Update Failed", "Failed to update password.");
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                AlertHelper.showError("Database Error", "Failed to update password: " + e.getMessage());
            }
        }
    }
    
    private void applyTheme(String theme) {
        Scene scene = root.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            if ("dark".equals(theme)) {
                scene.getStylesheets().add(getClass().getResource("/resources/dark-theme.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());
            }
        }
    }
    
    private void saveAllSettings() throws Exception {
        if (UserSession.getCurrentUser() != null) {
            String newEmail = emailField.getText().trim();
            
            // Basic email validation
            if (newEmail.isEmpty()) {
                AlertHelper.showError("Input Error", "Email cannot be empty.");
                return;
            }
            
            if (!isValidEmail(newEmail)) {
                AlertHelper.showError("Invalid Email", "Please enter a valid email address.");
                return;
            }
            
            try (Connection conn = dbManager.getConnection()) {
                String sql = "UPDATE users SET email = ?, theme_preference = ? WHERE user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newEmail);
                    pstmt.setString(2, darkTheme.isSelected() ? "dark" : "light");
                    pstmt.setInt(3, UserSession.getCurrentUser().getUserId());
                    
                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Settings saved successfully.");
                        // Update session user email
                        UserSession.getCurrentUser().setEmail(newEmail);
                    } else {
                        AlertHelper.showError("Save Failed", "Failed to save settings.");
                    }
                }
            } catch (SQLException e) {
                AlertHelper.showError("Database Error", "Failed to save settings: " + e.getMessage());
                System.err.println("Error saving settings: " + e.getMessage());
            }
        } else {
            AlertHelper.showError("Session Error", "No user session found. Please login again.");
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email regex pattern
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }
    
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    public Node getView() {
        return root;
    }
}