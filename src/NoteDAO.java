import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    // Check if a note with the same student_id, project_id, and encadrant_id exists
    public static boolean noteExists(String studentId, String projectId, String encadrantId) throws SQLException {
        String sql = "SELECT * FROM notes WHERE id_etudiant = ? AND id_projet = ? AND id_encadrant = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, projectId);
            pstmt.setString(3, encadrantId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Return true if a record exists
        }
    }

    // Add a new note to the database
    public static boolean ajouterNote(Note note) throws SQLException {
        if (noteExists(note.getIdEtudiant(), note.getIdProjet(), note.getIdEncadrant())) {
            System.out.println("A note with the same student, project, and encadrant already exists.");
            return false; // Prevent duplicate entries
        }

        String sql = "INSERT INTO notes (id_encadrant, id_etudiant, id_projet, note_travail, note_rapport, note_presentation, appreciation) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, note.getIdEncadrant());
            pstmt.setString(2, note.getIdEtudiant());
            pstmt.setString(3, note.getIdProjet());
            pstmt.setBigDecimal(4, note.getNoteTravail());
            pstmt.setBigDecimal(5, note.getNoteRapport());
            pstmt.setBigDecimal(6, note.getNotePresentation());
            pstmt.setString(7, note.getAppreciation());
            int result = pstmt.executeUpdate();
            return result > 0; // Return true if insertion was successful
        }
    }

    // Get all notes for a specific student
    public static List<Note> getNotesByStudentId(String studentId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE id_etudiant = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getInt("id"));
                note.setIdEncadrant(rs.getString("id_encadrant"));
                note.setIdEtudiant(rs.getString("id_etudiant"));
                note.setIdProjet(rs.getString("id_projet")); // Corrected column name
                note.setNoteTravail(rs.getBigDecimal("note_travail"));
                note.setNoteRapport(rs.getBigDecimal("note_rapport"));
                note.setNotePresentation(rs.getBigDecimal("note_presentation"));
                note.setAppreciation(rs.getString("appreciation"));
                notes.add(note);
            }
        }
        return notes;
    }

    // Update an existing note
    public static void updateNote(Note note) throws SQLException {
        String sql = "UPDATE notes SET id_projet = ?, id_encadrant = ?, note_travail = ?, note_rapport = ?, note_presentation = ?, appreciation = ? WHERE id_etudiant = ? AND id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, note.getIdProjet());
            pstmt.setString(2, note.getIdEncadrant());
            pstmt.setBigDecimal(3, note.getNoteTravail());
            pstmt.setBigDecimal(4, note.getNoteRapport());
            pstmt.setBigDecimal(5, note.getNotePresentation());
            pstmt.setString(6, note.getAppreciation());
            pstmt.setString(7, note.getIdEtudiant());
            pstmt.setInt(8, note.getId()); // Use the note's ID for the update
            pstmt.executeUpdate();
        }
    }

    // Delete a note for a specific student and encadrant
    public static void deleteNote(String studentId, String encadrantId) throws SQLException {
        String sql = "DELETE FROM notes WHERE id_etudiant = ? AND id_encadrant = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, encadrantId);
            pstmt.executeUpdate();
        }
    }

    // Get all notes in the database
    public static List<Note> getAllNotes() throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getInt("id"));
                note.setIdEncadrant(rs.getString("id_encadrant"));
                note.setIdEtudiant(rs.getString("id_etudiant"));
                note.setIdProjet(rs.getString("id_projet")); // Corrected column name
                note.setNoteTravail(rs.getBigDecimal("note_travail"));
                note.setNoteRapport(rs.getBigDecimal("note_rapport"));
                note.setNotePresentation(rs.getBigDecimal("note_presentation"));
                note.setAppreciation(rs.getString("appreciation"));
                notes.add(note);
            }
        }
        return notes;
    }

    // Get all notes for a specific encadrant
    public static List<Note> getNotesByEncadrantId(String encadrantId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE id_encadrant = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, encadrantId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getInt("id"));
                note.setIdEncadrant(rs.getString("id_encadrant"));
                note.setIdEtudiant(rs.getString("id_etudiant"));
                note.setIdProjet(rs.getString("id_projet")); // Corrected column name
                note.setNoteTravail(rs.getBigDecimal("note_travail"));
                note.setNoteRapport(rs.getBigDecimal("note_rapport"));
                note.setNotePresentation(rs.getBigDecimal("note_presentation"));
                note.setAppreciation(rs.getString("appreciation"));
                notes.add(note);
            }
        }
        return notes;
    }
}
