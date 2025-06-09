package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Handling Database operations.
public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpartsdb";
    private static final String USER = "root";
    private static final String PASS = "ayyskillz12";

    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SHOW TABLES")) {

                while (rs.next()) {
                    tableNames.add(rs.getString(1));
                }
            }
        } catch (ClassNotFoundException cnfe) {
            AlertHelper.showError("JDBC Driver Not Found", "Cannot load the Driver.");
        } catch (Exception ex) {
            AlertHelper.showError("Database Error", ex.getMessage());
        }
        return tableNames;
    }

    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, USER, PASS);
    }



    // Other operations.
}