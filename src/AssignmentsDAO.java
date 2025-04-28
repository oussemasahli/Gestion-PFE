import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentsDAO {

    public static void assignStudentToProject(String studentId, String projectId) throws SQLException {
        System.out.println("Checking if assignment exists: Student ID = " + studentId + ", Project ID = " + projectId);
        String checkSql = "SELECT * FROM assignments WHERE student_id = ? AND project_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, studentId);
            checkStmt.setString(2, projectId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Assignment already exists.");
                return;
            }
        }

        System.out.println("Inserting assignment into database.");
        String insertSql = "INSERT INTO assignments (student_id, project_id) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, studentId);
            insertStmt.setString(2, projectId);
            insertStmt.executeUpdate();
            System.out.println("Assignment inserted successfully.");
        }
    }

    public static void removeAssignment(String studentId, String projectId) throws SQLException {
        System.out.println("Deleting assignment: Student ID = " + studentId + ", Project ID = " + projectId);
        String sql = "DELETE FROM assignments WHERE student_id = ? AND project_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, projectId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Assignment deleted successfully.");
            } else {
                System.out.println("No assignment found to delete.");
            }
        }
    }

    // Obtenir toutes les assignations d'etudiants a des projets
    public static List<String> getAllAssignments() throws SQLException {
        List<String> assignments = new ArrayList<>();
        String sql = "SELECT student_id, project_id FROM assignments";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String projectId = rs.getString("project_id");
                assignments.add("L'etudiant " + studentId + " est assigne au projet " + projectId);
            }
        }

        return assignments;
    }
}
