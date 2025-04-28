import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EncadrantDAO {

    // Ajouter un nouvel encadrant dans la base de donnees
    public static void addEncadrant(Encadrant encadrant) throws SQLException {
        String checkSql = "SELECT * FROM supervisors WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, encadrant.getId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                System.out.println("L'encadrant avec l'ID " + encadrant.getId() + " existe deja.");
                return;
            }
        }

        String sql = "INSERT INTO supervisors (id, name, availability, charge_encadrement) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, encadrant.getId());
            pstmt.setString(2, encadrant.getNom());
            pstmt.setInt(3, encadrant.getDisponibilite());
            pstmt.setInt(4, encadrant.getChargeEncadrement());
            pstmt.executeUpdate();
            System.out.println("Encadrant ajoute avec succes : " + encadrant.getNom());
        }
    }

    // Ajouter un nouveau projet dans la base de donnees
    public static void addProjet(Projet projet) throws SQLException {
        String sql = "INSERT INTO projects (id, title, description) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projet.getId());
            pstmt.setString(2, projet.getTitre());
            pstmt.setString(3, projet.getDescription());
            pstmt.executeUpdate();
        }
    }

    // Mettre a jour un encadrant existant
    public static void updateEncadrant(Encadrant encadrant) throws SQLException {
        String sql = "UPDATE supervisors SET name = ?, availability = ?, charge_encadrement = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, encadrant.getNom());
            pstmt.setInt(2, encadrant.getDisponibilite());
            pstmt.setInt(3, encadrant.getChargeEncadrement());
            pstmt.setString(4, encadrant.getId());
            pstmt.executeUpdate();
        }
    }

    // Recuperer un encadrant par ID
    public static Encadrant getEncadrantById(String supervisorId) throws SQLException {
        String sql = "SELECT * FROM supervisors WHERE id = ?";
        Encadrant encadrant = null;

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supervisorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                encadrant = new Encadrant(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("availability"),
                        rs.getInt("charge_encadrement")
                );
            }
        }
        return encadrant;
    }

    // Recuperer tous les encadrants
    public static List<Encadrant> getAllEncadrants() throws SQLException {
        List<Encadrant> encadrants = new ArrayList<>();
        String sql = "SELECT * FROM supervisors";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Encadrant encadrant = new Encadrant(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("availability"),
                        rs.getInt("charge_encadrement")
                );
                encadrants.add(encadrant);
            }
        }
        return encadrants;
    }

    // Recuperer les encadrants disponibles
    public static List<Encadrant> getAvailableEncadrants() throws SQLException {
        List<Encadrant> encadrants = new ArrayList<>();
        String sql = "SELECT * FROM supervisors WHERE availability > 0";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Encadrant encadrant = new Encadrant(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("availability"),
                        rs.getInt("charge_encadrement")
                );
                encadrants.add(encadrant);
            }
        }
        return encadrants;
    }

    // Supprimer un encadrant par ID
    public static void deleteEncadrant(String encadrantId) throws SQLException {
        String sql = "DELETE FROM supervisors WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, encadrantId);
            pstmt.executeUpdate();
            System.out.println("Encadrant deleted successfully: " + encadrantId);
        }
    }
}
