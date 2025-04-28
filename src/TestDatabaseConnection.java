import java.sql.*;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        // Informations de connexion a la base de donnees
        String url = "jdbc:mysql://localhost:3306/project_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";  // Votre nom d'utilisateur MySQL
        String password = "mbccbcmbc123";  // Votre mot de passe MySQL

        try {
            // Optionnel : Charger explicitement le driver MySQL
            // Class.forName("com.mysql.cj.jdbc.Driver");  // Cette ligne peut être omise avec les versions recentes du driver.

            // etablir la connexion
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion a la base de donnees reussie !");

            // Fermer la connexion
            conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();  // Afficher les details de l'erreur pour le debogage
        }
    }
}
