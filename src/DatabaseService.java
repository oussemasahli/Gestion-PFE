import java.sql.*;
public class DatabaseService {

    private static final String URL = "jdbc:mysql://localhost:3306/project_db";
    private static final String USER = "root";
    private static final String PASSWORD = "mbccbcmbc123";

    public static boolean authenticateUser(String input, String password) {
        String query = "SELECT * FROM users WHERE (email = ? OR name = ?) AND password = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, input); // Input can be email or name
            statement.setString(2, input);
            statement.setString(3, password);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if a match is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}