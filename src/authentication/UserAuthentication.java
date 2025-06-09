package authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import utils.DatabaseManager;
import utils.AlertHelper;

public class UserAuthentication {
    private static final int SALT_LENGTH = 16;
    private DatabaseManager dbManager;
    
    public UserAuthentication() {
        dbManager = new DatabaseManager();
        initializeUsersTable();
    }
    
    private void initializeUsersTable() {
        try (Connection conn = dbManager.getConnection()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    salt VARCHAR(255) NOT NULL,
                    role ENUM('admin', 'employee', 'manager') DEFAULT 'employee',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_login TIMESTAMP NULL,
                    is_active BOOLEAN DEFAULT TRUE
                )
            """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }
        } catch (Exception e) {
            AlertHelper.showError("Database Error", "Failed to initialize users table: " + e.getMessage());
        }
    }
    
    public boolean registerUser(String username, String email, String password) {
        if (!isValidPassword(password)) {
            AlertHelper.showError("Invalid Password", "Password must be at least 8 characters long and contain uppercase, lowercase, number, and special character");
            return false;
        }
        
        try (Connection conn = dbManager.getConnection()) {
            if (userExists(conn, username, email)) {
                AlertHelper.showError("User Exists", "Username or email already exists");
                return false;
            }
            
            // Generate salt and the hash for password.
            String salt = generateSalt();
            String passwordHash = hashPasswordWithSalt(password, salt);
            
            String insertSQL = "INSERT INTO users (username, email, password_hash, salt) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                pstmt.setString(3, passwordHash);
                pstmt.setString(4, salt);
                
                int result = pstmt.executeUpdate();
                return result > 0;
            }
        } catch (Exception e) {
            AlertHelper.showError("Registration Error", "Failed to register user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean authenticateUser(String username, String password) {
        try (Connection conn = dbManager.getConnection()) {
            String selectSQL = "SELECT password_hash, salt FROM users WHERE username = ? AND is_active = TRUE";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, username);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("password_hash");
                        String salt = rs.getString("salt");
                        
                        String providedHash = hashPasswordWithSalt(password, salt);
                        
                        if (storedHash.equals(providedHash)) {
                            updateLastLogin(conn, username);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            AlertHelper.showError("Authentication Error", "Login failed: " + e.getMessage());
        }
        return false;
    }
    
    /**
 * Fetches the role for a given username.
 * @param username the login name
 * @return the role ("admin" or "user"), or null if not found
     * @throws Exception 
 */
public String getUserRole(String username) throws Exception {
    String sql = "SELECT role FROM users WHERE username = ?";
        Connection conn = dbManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
    
    private boolean userExists(Connection conn, String username, String email) throws SQLException {
        String selectSQL = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    private void updateLastLogin(Connection conn, String username) throws SQLException {
        String updateSQL = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }
    
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    public String hashPasswordWithSalt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + salt;
            byte[] hashedBytes = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
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
    
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (!authenticateUser(username, oldPassword)) {
            AlertHelper.showError("Authentication Failed", "Current password is incorrect");
            return false;
        }
        
        if (!isValidPassword(newPassword)) {
            AlertHelper.showError("Invalid Password", "New password does not meet requirements");
            return false;
        }
        
        try (Connection conn = dbManager.getConnection()) {
            String salt = generateSalt();
            String passwordHash = hashPasswordWithSalt(newPassword, salt);
            
            String updateSQL = "UPDATE users SET password_hash = ?, salt = ? WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setString(1, passwordHash);
                pstmt.setString(2, salt);
                pstmt.setString(3, username);
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            AlertHelper.showError("Database Error", "Failed to change password: " + e.getMessage());
            return false;
        }
    }

    
}
