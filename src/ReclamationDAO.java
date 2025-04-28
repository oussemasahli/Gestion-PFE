import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationDAO {

    // Ajouter une nouvelle reclamation a la base de donnees
    public static void addReclamation(Reclamation reclamation) throws SQLException {
        // Verifier si la reclamation existe deja dans la base de donnees
        String checkSql = "SELECT * FROM reclamations WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, reclamation.getId());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Si la reclamation avec cet ID existe deja, afficher un message et retourner
                System.out.println("Reclamation avec l'ID " + reclamation.getId() + " existe deja.");
                return;  // Quitter la methode sans inserer
            }
        }
    
        // Si la reclamation n'existe pas, inserer la nouvelle reclamation dans la table `reclamations`
        String sql = "INSERT INTO reclamations (id, student_id, description, type, status, date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reclamation.getId());
            pstmt.setString(2, reclamation.getEtudiant().getId()); // Cle etrangere pour l'etudiant
            pstmt.setString(3, reclamation.getDescription());
            pstmt.setString(4, reclamation.getType());
            pstmt.setString(5, reclamation.getStatus());
            pstmt.setTimestamp(6, new Timestamp(reclamation.getDate().getTime())); // Enregistrer en tant que Timestamp
            pstmt.executeUpdate();
            System.out.println("Reclamation ajoutee avec succes : " + reclamation.getId());
        }
    }
    
    // Obtenir une reclamation par ID depuis la base de donnees
    public static Reclamation getReclamationById(String reclamationId) throws SQLException {
        String sql = "SELECT * FROM reclamations WHERE id = ?";
        Reclamation reclamation = null;

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reclamationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Etudiant etudiant = EtudiantDAO.getEtudiantById(rs.getString("student_id"));
                String description = rs.getString("description");
                String type = rs.getString("type");
                String status = rs.getString("status");
                Timestamp timestamp = rs.getTimestamp("date"); // Recuperer en tant que Timestamp
                reclamation = new Reclamation(reclamationId, etudiant, description, type, status, timestamp);
            }
        }
        return reclamation;
    }

    // Obtenir toutes les reclamations depuis la base de donnees
    public static List<Reclamation> getAllReclamations() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String sql = "SELECT * FROM reclamations";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Etudiant etudiant = EtudiantDAO.getEtudiantById(rs.getString("student_id"));
                String description = rs.getString("description");
                String type = rs.getString("type");
                String status = rs.getString("status");
                Timestamp timestamp = rs.getTimestamp("date"); // Recuperer en tant que Timestamp
                Reclamation reclamation = new Reclamation(
                        rs.getString("id"), etudiant, description, type, status, timestamp
                );
                reclamations.add(reclamation);
            }
        }
        return reclamations;
    }
}
