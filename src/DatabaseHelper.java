import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    // Methode pour etablir la connexion a la base de donnees
    public static Connection connect() throws SQLException {
        // S'assurer que le driver est charge
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC MySQL non trouve !");
            e.printStackTrace();
            return null;
        }

        // URL de connexion a la base de donnees
        String url = "jdbc:mysql://localhost:3306/project_db";  // Remplacez 'project_db' par le nom de votre base de donnees
        String user = "root";  // Remplacez par votre nom d'utilisateur MySQL
        String password = "mbccbcmbc123";  // Remplacez par votre mot de passe MySQL

        // etablir la connexion et la retourner
        return DriverManager.getConnection(url, user, password);
    }
}
