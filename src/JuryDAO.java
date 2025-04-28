import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JuryDAO {

    // Ajouter un jury a la base de donnees
    public static void addJury(Jury jury) throws SQLException {
        // Verifier si le jury existe deja
        String checkSql = "SELECT * FROM juries WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, jury.getId());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Si le jury existe deja, afficher un message et sortir
                System.out.println("Jury avec l'ID " + jury.getId() + " existe deja.");
                return;  // Quitter la methode sans inserer
            }
        }
    
        // Inserer le jury dans la table `juries`
        String sql = "INSERT INTO juries (id, name) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jury.getId());
            pstmt.setString(2, jury.getName());  // Inserer le nom ici
            pstmt.executeUpdate();
            System.out.println("Jury ajoute avec succes : " + jury.getId());
        }
    
        // Ajouter les membres du jury (encadrants)
        for (Encadrant membre : jury.getMembres()) {
            addJuryMember(jury.getId(), membre.getId(), membre.getRole());
        }
    }
    
    // Ajouter un membre au jury
    public static void addJuryMember(String juryId, String encadrantId, String role) throws SQLException {
        // Check if the jury member already exists
        String checkSql = "SELECT * FROM jury_members WHERE jury_id = ? AND encadrant_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, juryId);
            checkStmt.setString(2, encadrantId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                System.out.println("Encadrant with ID " + encadrantId + " is already assigned to jury " + juryId);
                return;
            }
        }

        // Insert the new jury member with the role
        String sql = "INSERT INTO jury_members (jury_id, encadrant_id, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, juryId);
            pstmt.setString(2, encadrantId);
            pstmt.setString(3, role); // Add the role
            pstmt.executeUpdate();
            System.out.println("Encadrant with ID " + encadrantId + " added to jury " + juryId + " as " + role);
        }
    }
    
    // Supprimer un membre du jury
    public static void removeJuryMember(String juryId, String encadrantId) throws SQLException {
        String sql = "DELETE FROM jury_members WHERE jury_id = ? AND encadrant_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, juryId);
            pstmt.setString(2, encadrantId);
            pstmt.executeUpdate();
            System.out.println("Encadrant with ID " + encadrantId + " removed from jury " + juryId);
        }
    }

    // Methode pour recuperer un jury par ID
    public static Jury getJuryById(String juryId) throws SQLException {
        Jury jury = null;
        String sql = "SELECT * FROM juries WHERE id = ?";
    
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, juryId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Recuperer a la fois 'id' et 'name' du resultat
                String juryName = rs.getString("name");
    
                // Creer l'objet Jury en utilisant 'id', 'name' et les membres (membres)
                jury = new Jury(rs.getString("id"), juryName, getJuryMembers(juryId));
            }
        }
        return jury;
    }
    

    // Recuperer tous les jurys (et leurs membres) depuis la base de donnees
    public static List<Jury> getAllJuries() throws SQLException {
        List<Jury> juries = new ArrayList<>();
        String sql = "SELECT * FROM juries";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String juryId = rs.getString("id");
                String juryName = rs.getString("name");
                juries.add(new Jury(juryId, juryName, new ArrayList<>()));
            }
        }
        return juries;
    }
    

    // Recuperer tous les membres d'un jury specifique
    public static List<Encadrant> getJuryMembers(String juryId) throws SQLException {
        List<Encadrant> members = new ArrayList<>();
        String sql = "SELECT e.id, e.name, jm.role FROM jury_members jm " +
                     "JOIN supervisors e ON jm.encadrant_id = e.id " +
                     "WHERE jm.jury_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, juryId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String encadrantId = rs.getString("id");
                String encadrantName = rs.getString("name");
                String role = rs.getString("role"); // Retrieve the role

                // Create an Encadrant object with the role
                Encadrant encadrant = new Encadrant(encadrantId, encadrantName, role);
                members.add(encadrant);
            }
        }
        return members;
    }

    // Mettre a jour un jury
    public static void updateJury(Jury jury) throws SQLException {
        String sql = "UPDATE juries SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jury.getName());
            pstmt.setString(2, jury.getId());
            pstmt.executeUpdate();
            System.out.println("Jury updated successfully: " + jury.getId());
        }
    }

    // Mettre a jour le role d'un membre du jury
    public static void updateJuryMemberRole(String juryId, String encadrantId, String newRole) throws SQLException {
        String sql = "UPDATE jury_members SET role = ? WHERE jury_id = ? AND encadrant_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newRole);
            pstmt.setString(2, juryId);
            pstmt.setString(3, encadrantId);
            pstmt.executeUpdate();
            System.out.println("Role updated for Encadrant ID " + encadrantId + " in Jury ID " + juryId + " to " + newRole);
        }
    }

    // Supprimer un jury
    public static void deleteJury(String juryId) throws SQLException {
        // First, delete all members of the jury
        String deleteMembersSql = "DELETE FROM jury_members WHERE jury_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteMembersSql)) {
            pstmt.setString(1, juryId);
            pstmt.executeUpdate();
            System.out.println("All members of jury " + juryId + " have been deleted.");
        }

        // Then, delete the jury itself
        String deleteJurySql = "DELETE FROM juries WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteJurySql)) {
            pstmt.setString(1, juryId);
            pstmt.executeUpdate();
            System.out.println("Jury with ID " + juryId + " has been deleted.");
        }
    }
}
