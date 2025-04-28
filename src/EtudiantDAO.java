import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EtudiantDAO {

    // Add a new student
    public static void addEtudiant(Etudiant etudiant) throws SQLException {
        String checkSql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, etudiant.getId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                // Throw an exception if the CIN already exists
                throw new SQLException("Un etudiant avec le CIN " + etudiant.getId() + " existe deja.");
            }
        }

        String sql = "INSERT INTO students (id, name, numero_tel, email, formation, sexe) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, etudiant.getId());
            pstmt.setString(2, etudiant.getNom());
            pstmt.setString(3, etudiant.getNumeroTel());
            pstmt.setString(4, etudiant.getEmail());
            pstmt.setString(5, etudiant.getFormation());
            pstmt.setString(6, etudiant.getSexe());
            pstmt.executeUpdate();
            System.out.println("etudiant ajoute avec succes : " + etudiant.getNom());
        }
    }

    // Update an existing student
    public static void updateEtudiant(Etudiant etudiant) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, numero_tel = ?, formation = ?, sexe = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, etudiant.getNom());
            pstmt.setString(2, etudiant.getEmail());
            pstmt.setString(3, etudiant.getNumeroTel());
            pstmt.setString(4, etudiant.getFormation());
            pstmt.setString(5, etudiant.getSexe());
            pstmt.setString(6, etudiant.getId());
            pstmt.executeUpdate();
        }
    }

    // Delete a student
    public static void deleteEtudiant(String id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            System.out.println("etudiant supprime avec succes (ID : " + id + ")");
        }
    }

    // Get a student by ID
    public static Etudiant getEtudiantById(String id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Etudiant etudiant = new Etudiant();
                etudiant.setId(rs.getString("id"));
                etudiant.setNom(rs.getString("name"));
                etudiant.setNumeroTel(rs.getString("numero_tel"));
                etudiant.setEmail(rs.getString("email"));
                etudiant.setFormation(rs.getString("formation"));
                etudiant.setSexe(rs.getString("sexe"));
                return etudiant;
            }
        }
        return null;
    }

    // Get all students
    public static List<Etudiant> getAllEtudiants() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Etudiant etudiant = new Etudiant();
                etudiant.setId(rs.getString("id"));
                etudiant.setNom(rs.getString("name"));
                etudiant.setNumeroTel(rs.getString("numero_tel"));
                etudiant.setEmail(rs.getString("email"));
                etudiant.setFormation(rs.getString("formation"));
                etudiant.setSexe(rs.getString("sexe"));
                etudiants.add(etudiant);
            }
        }

        return etudiants;
    }

    // Get notes for a student
    public static List<Note> getNotes(String studentId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE id_etudiant = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getInt("id"));
                note.setIdEtudiant(rs.getString("id_etudiant"));
                note.setIdEncadrant(rs.getString("id_encadrant"));
                note.setNoteTravail(rs.getBigDecimal("note_travail"));
                note.setNoteRapport(rs.getBigDecimal("note_rapport"));
                note.setNotePresentation(rs.getBigDecimal("note_presentation"));
                note.setAppreciation(rs.getString("appreciation"));
                notes.add(note);
            }
        }

        return notes;
    }

    // Get all formations
    public static List<String> getAllFormations() throws SQLException {
        List<String> formations = new ArrayList<>();
        String sql = "SELECT name FROM formations";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                formations.add(rs.getString("name"));
            }
        }

        return formations;
    }
}
