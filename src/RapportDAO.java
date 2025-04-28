import java.sql.*;

public class RapportDAO {

    // Save the report to the database
    public static void saveRapport(Rapport rapport) throws SQLException {
        // Verifier si le rapport existe deja pour la combinaison etudiant et projet
        String checkSql = "SELECT * FROM rapports WHERE student_id = ? AND project_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, rapport.getEtudiant().getId());
            checkStmt.setString(2, rapport.getProjet().getId());
            ResultSet rs = checkStmt.executeQuery();
        
            if (rs.next()) {
                // Si la combinaison student_id et project_id existe deja, afficher un message et sortir
                System.out.println("Le rapport pour l'etudiant " + rapport.getEtudiant().getId() + " et le projet " + rapport.getProjet().getId() + " existe deja.");
                return;  // Quitter la methode sans inserer
            }
        }
        
        // Si la combinaison n'existe pas, inserer le nouveau rapport
        String sql = "INSERT INTO rapports (id, student_id, project_id, soumis) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rapport.getId());
            pstmt.setString(2, rapport.getEtudiant().getId());
            pstmt.setString(3, rapport.getProjet().getId());
            pstmt.setBoolean(4, rapport.isSoumis());
            pstmt.executeUpdate();
            System.out.println("Rapport ajoute avec succes pour l'etudiant " + rapport.getEtudiant().getId() + " et le projet " + rapport.getProjet().getId());
        }
    }
    
    
}
