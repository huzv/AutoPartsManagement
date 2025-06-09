package utils;

public class UserSession {
    private static User currentUser;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static void clearSession() {
        currentUser = null;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    // Inner User class to hold user information
    public static class User {
        private int userId;
        private String username;
        private String email;
        private String role;
        private String themePreference;
        
        
        public User(int userId, String username, String email, String role, String themePreference) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.role = role;
            this.themePreference = themePreference == null ? "light" : themePreference;
        }
        
        // Getters
        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getThemePreference() { return themePreference; }
        
        // Setters
        public void setEmail(String email) { this.email = email; }
        public void setRole(String role) { this.role = role; }
          public void setThemePreference(String theme) { this.themePreference = theme; }
    }
}