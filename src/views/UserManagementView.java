package views;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import authentication.UserAuthentication;
import utils.AlertHelper;
import utils.DatabaseManager;

import java.sql.*;
import java.time.format.DateTimeFormatter;

public class UserManagementView {
    private VBox view;
    private TableView<UserData> userTable;
    private ObservableList<UserData> userData;
    private UserAuthentication userAuth;
    private DatabaseManager dbManager;
    
    public UserManagementView() {
        this.userAuth = new UserAuthentication();
        this.dbManager = new DatabaseManager();
        this.userData = FXCollections.observableArrayList();
        initializeView();
        loadUserData();
    }
    
    private void initializeView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("User Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.getStyleClass().add("content-title");
        
        // Action buttons bar
        HBox actionBar = createActionBar();
        
        // User table
        userTable = createUserTable();
        
        // Details panel
        VBox detailsPanel = createDetailsPanel();
        
        view.getChildren().addAll(titleLabel, actionBar, userTable, detailsPanel);
    }
    
    private HBox createActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        actionBar.setPadding(new Insets(0, 0, 10, 0));
        
        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("action-button");
        refreshButton.setOnAction(e -> loadUserData());
        
        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.getStyleClass().add("secondary-button");
        deleteUserButton.setOnAction(e -> deleteSelectedUser());
        
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.getStyleClass().add("action-button");
        changePasswordButton.setOnAction(e -> showChangePasswordDialog());
        
        Button changeRoleButton = new Button("Change Role");
        changeRoleButton.getStyleClass().add("action-button");
        changeRoleButton.setOnAction(e -> showChangeRoleDialog());
        
        Button toggleStatusButton = new Button("Toggle Active Status");
        toggleStatusButton.getStyleClass().add("secondary-button");
        toggleStatusButton.setOnAction(e -> toggleUserStatus());
        
        actionBar.getChildren().addAll(
            refreshButton, deleteUserButton, changePasswordButton, 
            changeRoleButton, toggleStatusButton
        );
        
        return actionBar;
    }
    
    private TableView<UserData> createUserTable() {
        TableView<UserData> table = new TableView<>();
        table.setPrefHeight(400);
        table.getStyleClass().add("table-view");
        
        // ID Column
        TableColumn<UserData, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        idColumn.setPrefWidth(60);
        
        // Username Column
        TableColumn<UserData, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setPrefWidth(120);
        
        // Email Column
        TableColumn<UserData, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(200);
        
        // Role Column
        TableColumn<UserData, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleColumn.setPrefWidth(100);
        
        // Created At Column
        TableColumn<UserData, String> createdColumn = new TableColumn<>("Created");
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdColumn.setPrefWidth(150);
        
        // Last Login Column
        TableColumn<UserData, String> lastLoginColumn = new TableColumn<>("Last Login");
        lastLoginColumn.setCellValueFactory(new PropertyValueFactory<>("lastLogin"));
        lastLoginColumn.setPrefWidth(150);
        
        // Status Column
        TableColumn<UserData, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(80);
        
        table.getColumns().addAll(
            idColumn, usernameColumn, emailColumn, roleColumn, 
            createdColumn, lastLoginColumn, statusColumn
        );
        
        table.setItems(userData);
        return table;
    }
    
    private VBox createDetailsPanel() {
        VBox detailsPanel = new VBox(10);
        detailsPanel.setPadding(new Insets(20));
        detailsPanel.getStyleClass().add("card");
        
        Label detailsTitle = new Label("User Details");
        detailsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Label instructionLabel = new Label("Select a user from the table above to view details and perform actions.");
        instructionLabel.getStyleClass().add("card-title");
        
        detailsPanel.getChildren().addAll(detailsTitle, instructionLabel);
        return detailsPanel;
    }
    
    private void loadUserData() {
        userData.clear();
        
        try (Connection conn = dbManager.getConnection()) {
            String sql = "SELECT user_id, username, email, role, created_at, last_login, is_active FROM users ORDER BY user_id";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                
                while (rs.next()) {
                    UserData user = new UserData(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role"),
                        formatTimestamp(rs.getTimestamp("created_at")),
                        formatTimestamp(rs.getTimestamp("last_login")),
                        rs.getBoolean("is_active") ? "Active" : "Inactive"
                    );
                    userData.add(user);
                }
            }
        } catch (Exception e) {
            AlertHelper.showError("Database Error", "Failed to load user data: " + e.getMessage());
        }
    }
    
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "Never";
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    private void deleteSelectedUser() {
        UserData selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertHelper.showError("No Selection", "Please select a user to delete.");
            return;
        }
        
        // Prevent deleting admin users
        if ("admin".equalsIgnoreCase(selectedUser.getRole())) {
            AlertHelper.showError("Cannot Delete", "Admin users cannot be deleted for security reasons.");
            return;
        }
        
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete User");
        confirmAlert.setContentText("Are you sure you want to delete user '" + selectedUser.getUsername() + "'? This action cannot be undone.");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try (Connection conn = dbManager.getConnection()) {
                String sql = "DELETE FROM users WHERE user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, selectedUser.getUserIdAsInt());
                    int result = pstmt.executeUpdate();
                    
                    if (result > 0) {
                        AlertHelper.showAlert(AlertType.INFORMATION, "Success", "User deleted successfully.");
                        loadUserData();
                    } else {
                        AlertHelper.showError("Delete Failed", "Failed to delete user.");
                    }
                }
            } catch (Exception e) {
                AlertHelper.showError("Database Error", "Failed to delete user: " + e.getMessage());
            }
        }
    }
    
    private void showChangePasswordDialog() {
        UserData selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertHelper.showError("No Selection", "Please select a user to change password.");
            return;
        }
        
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change password for user: " + selectedUser.getUsername());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.getStyleClass().add("form-field");
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");
        confirmPasswordField.getStyleClass().add("form-field");
        
        Label requirementsLabel = new Label("Password must be at least 8 characters with uppercase, lowercase, number, and special character");
        requirementsLabel.setWrapText(true);
        requirementsLabel.getStyleClass().add("card-title");
        
        grid.add(new Label("New Password:"), 0, 0);
        grid.add(newPasswordField, 1, 0);
        grid.add(new Label("Confirm Password:"), 0, 1);
        grid.add(confirmPasswordField, 1, 1);
        grid.add(requirementsLabel, 0, 2, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return newPasswordField.getText();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(newPassword -> {
            String confirmPassword = confirmPasswordField.getText();
            
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                AlertHelper.showError("Input Error", "Please fill in both password fields.");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                AlertHelper.showError("Password Error", "Passwords do not match.");
                return;
            }
            
            if (!isValidPassword(newPassword)) {
                AlertHelper.showError("Invalid Password", "Password does not meet requirements.");
                return;
            }
            
            updateUserPassword(selectedUser.getUserIdAsInt(), newPassword);
        });
    }
    
    private void updateUserPassword(int userId, String newPassword) {
        try (Connection conn = dbManager.getConnection()) {
            String salt = userAuth.generateSalt();
            String passwordHash = userAuth.hashPasswordWithSalt(newPassword, salt);
            
            String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, passwordHash);
                pstmt.setString(2, salt);
                pstmt.setInt(3, userId);
                
                int result = pstmt.executeUpdate();
                if (result > 0) {
                    AlertHelper.showAlert(AlertType.INFORMATION, "Success", "Password updated successfully.");
                } else {
                    AlertHelper.showError("Update Failed", "Failed to update password.");
                }
            }
        } catch (Exception e) {
            AlertHelper.showError("Database Error", "Failed to update password: " + e.getMessage());
        }
    }
    
    private void showChangeRoleDialog() {
        UserData selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertHelper.showError("No Selection", "Please select a user to change role.");
            return;
        }
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>(selectedUser.getRole(), "admin", "user");
        dialog.setTitle("Change User Role");
        dialog.setHeaderText("Change role for user: " + selectedUser.getUsername());
        dialog.setContentText("Select new role:");
        
        dialog.showAndWait().ifPresent(newRole -> {
            if (!newRole.equals(selectedUser.getRole())) {
                updateUserRole(selectedUser.getUserIdAsInt(), newRole);
            }
        });
    }
    
    private void updateUserRole(int userId, String newRole) {
        try (Connection conn = dbManager.getConnection()) {
            String sql = "UPDATE users SET role = ? WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newRole);
                pstmt.setInt(2, userId);
                
                int result = pstmt.executeUpdate();
                if (result > 0) {
                    AlertHelper.showAlert(AlertType.INFORMATION, "Success", "User role updated successfully.");
                    loadUserData();
                } else {
                    AlertHelper.showError("Update Failed", "Failed to update user role.");
                }
            }
        } catch (Exception e) {
            AlertHelper.showError("Database Error", "Failed to update user role: " + e.getMessage());
        }
    }
    
    private void toggleUserStatus() {
        UserData selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertHelper.showError("No Selection", "Please select a user to toggle status.");
            return;
        }
        
        // Prevent deactivating admin users
        if ("admin".equalsIgnoreCase(selectedUser.getRole()) && "Active".equals(selectedUser.getStatus())) {
            AlertHelper.showError("Cannot Deactivate", "Admin users cannot be deactivated for security reasons.");
            return;
        }
        
        boolean newStatus = !"Active".equals(selectedUser.getStatus());
        String statusText = newStatus ? "activate" : "deactivate";
        
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Status Change");
        confirmAlert.setHeaderText("Toggle User Status");
        confirmAlert.setContentText("Are you sure you want to " + statusText + " user '" + selectedUser.getUsername() + "'?");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try (Connection conn = dbManager.getConnection()) {
                String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setBoolean(1, newStatus);
                    pstmt.setInt(2, selectedUser.getUserIdAsInt());
                    
                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        AlertHelper.showAlert(AlertType.INFORMATION, "Success", 
                            "User " + (newStatus ? "activated" : "deactivated") + " successfully.");
                        loadUserData();
                    } else {
                        AlertHelper.showError("Update Failed", "Failed to update user status.");
                    }
                }
            } catch (Exception e) {
                AlertHelper.showError("Database Error", "Failed to update user status: " + e.getMessage());
            }
        }
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
        return view;
    }
    
    // Inner class for user data
    public static class UserData {
        private final SimpleStringProperty userId;
        private final SimpleStringProperty username;
        private final SimpleStringProperty email;
        private final SimpleStringProperty role;
        private final SimpleStringProperty createdAt;
        private final SimpleStringProperty lastLogin;
        private final SimpleStringProperty status;
        private final int userIdInt; // Store the actual int value
        
        public UserData(int userId, String username, String email, String role, 
                       String createdAt, String lastLogin, String status) {
            this.userIdInt = userId; // Store the int value
            this.userId = new SimpleStringProperty(String.valueOf(userId));
            this.username = new SimpleStringProperty(username);
            this.email = new SimpleStringProperty(email);
            this.role = new SimpleStringProperty(role);
            this.createdAt = new SimpleStringProperty(createdAt);
            this.lastLogin = new SimpleStringProperty(lastLogin);
            this.status = new SimpleStringProperty(status);
        }
        
        // Getters for TableView
        public String getUserId() { return userId.get(); }
        public int getUserIdAsInt() { return userIdInt; } // Add this method to return int
        public String getUsername() { return username.get(); }
        public String getEmail() { return email.get(); }
        public String getRole() { return role.get(); }
        public String getCreatedAt() { return createdAt.get(); }
        public String getLastLogin() { return lastLogin.get(); }
        public String getStatus() { return status.get(); }
        
        // Property getters for TableView binding
        public SimpleStringProperty userIdProperty() { return userId; }
        public SimpleStringProperty usernameProperty() { return username; }
        public SimpleStringProperty emailProperty() { return email; }
        public SimpleStringProperty roleProperty() { return role; }
        public SimpleStringProperty createdAtProperty() { return createdAt; }
        public SimpleStringProperty lastLoginProperty() { return lastLogin; }
        public SimpleStringProperty statusProperty() { return status; }
    }
}