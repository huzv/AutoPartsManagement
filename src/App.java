import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class App extends Application {

    private static final String DB_URL =
        "jdbc:mysql://localhost:3306/auto_partsdb";
    private static final String USER = "root";
    private static final String PASS = "ayyskillz12";

    private final ObservableList<String> tableNames = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        loadTableNames();

        ListView<String> listView = new ListView<>(tableNames);
        listView.setPrefSize(300, 400);

        VBox root = new VBox(10, listView);
        root.setStyle("-fx-padding: 10;");

        primaryStage.setTitle("auto_partsdb Tables");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void loadTableNames() {
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
            showError("JDBC Driver Not Found", "Cannot load the Driver.");
        } catch (Exception ex) {
            showError("Database Error", ex.getMessage());
        }
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR, content);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
