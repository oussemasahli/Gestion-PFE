import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/projet_db"; // Replace with your database name
    private static final String USERNAME = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "mbccbcmbc123";    // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}