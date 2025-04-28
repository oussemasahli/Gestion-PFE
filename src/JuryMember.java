import java.sql.*;

public class JuryMember {
    private String juryId;
    private String encadrantId;
    private String role;

    // Constructor
    public JuryMember(String juryId, String encadrantId, String role) {
        this.juryId = juryId;
        this.encadrantId = encadrantId;
        this.role = role;
    }

    // Getters and Setters
    public String getJuryId() {
        return juryId;
    }

    public void setJuryId(String juryId) {
        this.juryId = juryId;
    }

    public String getEncadrantId() {
        return encadrantId;
    }

    public void setEncadrantId(String encadrantId) {
        this.encadrantId = encadrantId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Save the jury member to the database
    public void saveToDatabase() throws SQLException {
        String query = "INSERT INTO jury_members (jury_id, encadrant_id, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.juryId);
            stmt.setString(2, this.encadrantId);
            stmt.setString(3, this.role);
            stmt.executeUpdate();
            System.out.println("Encadrant with ID " + this.encadrantId + " added to jury " + this.juryId + " as " + this.role);
        }
    }
}
