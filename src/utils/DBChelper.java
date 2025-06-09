package utils;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBChelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/CarPartsDB";
    private static final String USER = "root";
    private static final String PASS = "sql5000";

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
