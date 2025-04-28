import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetDAO {

    // Add a project to the database
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

    // Update an existing project in the database
    public static void updateProjet(Projet projet) throws SQLException {
        String sql = "UPDATE projects SET title = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projet.getTitre());
            pstmt.setString(2, projet.getDescription());
            pstmt.setString(3, projet.getId());
            pstmt.executeUpdate();
        }
    }

    // Get a project by ID from the database
    public static Projet getProjetById(String projectId) throws SQLException {
        String sql = "SELECT * FROM projects WHERE id = ?";
        Projet projet = null;

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                projet = new Projet(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("description")
                );
            }
        }
        return projet;
    }

    // Get all projects from the database
    public static List<Projet> getAllProjects() throws SQLException {
        List<Projet> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Projet projet = new Projet(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("description")
                );
                projects.add(projet);
            }
        }
        return projects;
    }

    // Delete a project from the database
    public static void deleteProjet(String projectId) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projectId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun projet trouvé avec l'ID : " + projectId);
            }
        }
    }

    // Generate the next project ID
    public static String generateNextProjectId() throws SQLException {
        String sql = "SELECT id FROM projects ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("id");
                int numericPart = Integer.parseInt(lastId.substring(1)); // Extract the numeric part
                return String.format("P%03d", numericPart + 1); // Increment and format as P001, P002, etc.
            } else {
                return "P001"; // Default to P001 if no projects exist
            }
        }
    }
}
