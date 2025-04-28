import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SoutenanceDAO {

    public static String generateNextSoutenanceId() throws SQLException {
        String sql = "SELECT id FROM soutenances ORDER BY id DESC LIMIT 1"; // Get the highest ID
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("id"); // e.g., "ST003"
                int nextIdNumber = Integer.parseInt(lastId.substring(2)) + 1; // Extract the number and increment
                return String.format("ST%03d", nextIdNumber); // Format as "ST001", "ST002", etc.
            } else {
                return "ST001"; // Start with "ST001" if no IDs exist
            }
        }
    }

    // Methode pour ajouter une nouvelle soutenance a la base de donnees
    public static void addSoutenance(Soutenance soutenance) throws SQLException {
        // Verify if the soutenance already exists by checking the combination of student_id and jury_id
        String checkSql = "SELECT * FROM soutenances WHERE student_id = ? AND jury_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, soutenance.getEtudiant().getId()); // Verify if the student is already assigned to the jury
            checkStmt.setString(2, soutenance.getJury().getId()); // Verify if the jury is already assigned

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // If the combination of student_id and jury_id already exists, throw an exception
                throw new SQLException("La soutenance pour l'etudiant " + soutenance.getEtudiant().getId() +
                                       " avec le jury " + soutenance.getJury().getId() + " existe dejà.");
            }
        }

        // If the combination does not exist, insert the new soutenance
        String sql = "INSERT INTO soutenances (id, student_id, jury_id, project_id, date, salle, supervisor_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soutenance.getId()); // Set the soutenance ID
            pstmt.setString(2, soutenance.getEtudiant().getId()); // Foreign key for the student
            pstmt.setString(3, soutenance.getJury().getId()); // Foreign key for the jury
            pstmt.setString(4, soutenance.getProjectId()); // Foreign key for the project
            pstmt.setTimestamp(5, new Timestamp(soutenance.getDate().getTime())); // Date and time
            pstmt.setString(6, soutenance.getSalle()); // Salle
            pstmt.setString(7, soutenance.getEncadrant() != null ? soutenance.getEncadrant().getId() : null); // Foreign key for the supervisor
            pstmt.executeUpdate(); // Execute the insertion query
            System.out.println("Soutenance ajoutee avec succès pour l'etudiant " + soutenance.getEtudiant().getId() +
                               " et le jury " + soutenance.getJury().getId() + " avec le projet " + soutenance.getProjectId());
        }
    }
    
    // Methode pour recuperer les details d'une soutenance par ID de la base de donnees
    public static Soutenance getSoutenanceById(String soutenanceId) throws SQLException {
        String sql = "SELECT * FROM soutenances WHERE id = ?";
        Soutenance soutenance = null;

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soutenanceId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Etudiant etudiant = EtudiantDAO.getEtudiantById(rs.getString("student_id"));
                Jury jury = JuryDAO.getJuryById(rs.getString("jury_id"));
                Date date = rs.getTimestamp("date");
                String salle = rs.getString("salle");
                String projectId = rs.getString("project_id");
                String supervisorId = rs.getString("supervisor_id");

                // Fetch the Encadrant (supervisor) using EncadrantDAO
                Encadrant encadrant = null;
                if (supervisorId != null) {
                    encadrant = EncadrantDAO.getEncadrantById(supervisorId);
                }

                soutenance = new Soutenance(soutenanceId, etudiant, jury, date, salle, projectId, encadrant);
            }
        }
        return soutenance;
    }

    // Methode pour recuperer toutes les soutenances de la base de donnees
    public static List<Soutenance> getAllSoutenances() throws SQLException {
        List<Soutenance> soutenances = new ArrayList<>();
        String sql = "SELECT * FROM soutenances";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Etudiant etudiant = EtudiantDAO.getEtudiantById(rs.getString("student_id"));
                Jury jury = JuryDAO.getJuryById(rs.getString("jury_id"));
                Date date = rs.getTimestamp("date");
                String salle = rs.getString("salle");
                String projectId = rs.getString("project_id");
                String supervisorId = rs.getString("supervisor_id");

                // Fetch the Encadrant (supervisor) using EncadrantDAO
                Encadrant encadrant = null;
                if (supervisorId != null) {
                    encadrant = EncadrantDAO.getEncadrantById(supervisorId);
                }

                Soutenance soutenance = new Soutenance(rs.getString("id"), etudiant, jury, date, salle, projectId, encadrant);
                soutenances.add(soutenance);
            }
        }
        return soutenances;
    }

    public static List<Soutenance> getSoutenancesByStudentId(String studentId) throws SQLException {
        List<Soutenance> soutenances = new ArrayList<>();
        String sql = "SELECT * FROM soutenances WHERE student_id = ?";
    
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                Etudiant etudiant = EtudiantDAO.getEtudiantById(rs.getString("student_id"));
                Jury jury = JuryDAO.getJuryById(rs.getString("jury_id"));
                Date date = rs.getTimestamp("date");
                String salle = rs.getString("salle");
                String projectId = rs.getString("project_id"); // Fetch project_id
                Soutenance soutenance = new Soutenance(rs.getString("id"), etudiant, jury, date, salle, projectId);
                soutenances.add(soutenance);
            }
        }
        return soutenances;
    }

    public static void updateSoutenance(Soutenance soutenance) throws SQLException {
        String sql = "UPDATE soutenances SET student_id = ?, jury_id = ?, project_id = ?, date = ?, salle = ?, supervisor_id = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soutenance.getEtudiant().getId()); // Update student ID
            pstmt.setString(2, soutenance.getJury().getId()); // Update jury ID
            pstmt.setString(3, soutenance.getProjectId()); // Update project ID
            pstmt.setTimestamp(4, new Timestamp(soutenance.getDate().getTime())); // Update date
            pstmt.setString(5, soutenance.getSalle()); // Update salle
            pstmt.setString(6, soutenance.getEncadrant() != null ? soutenance.getEncadrant().getId() : null); // Update supervisor ID
            pstmt.setString(7, soutenance.getId()); // Specify the soutenance ID
            pstmt.executeUpdate(); // Execute the update query
            System.out.println("Soutenance mise à jour avec succès pour l'etudiant " + soutenance.getEtudiant().getId() +
                               " et le jury " + soutenance.getJury().getId() + " avec le projet " + soutenance.getProjectId());
        }
    }

    public static void deleteSoutenance(String soutenanceId) throws SQLException {
        String sql = "DELETE FROM soutenances WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soutenanceId);
            pstmt.executeUpdate();
        }
    }

    public static boolean soutenanceExists(String studentId, String juryId, String currentSoutenanceId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM soutenances WHERE student_id = ? AND jury_id = ? AND id != ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, juryId);
            pstmt.setString(3, currentSoutenanceId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if the count is greater than 0
            }
        }
        return false;
    }
}