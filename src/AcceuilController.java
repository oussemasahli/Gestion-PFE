import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class AcceuilController {

    @FXML
    private Label totalStudentsLabel;

    @FXML
    private Label totalJuriesLabel;

    @FXML
    private Label totalProjectsLabel;

    @FXML
    private Label totalSupervisorsLabel;

    @FXML
    public void initialize() {
        try {
            // Fetch and display statistics
            totalStudentsLabel.setText(String.valueOf(getTotalStudents()));
            totalJuriesLabel.setText(String.valueOf(getTotalJuries()));
            totalProjectsLabel.setText(String.valueOf(getTotalProjects()));
            totalSupervisorsLabel.setText(String.valueOf(getTotalSupervisors()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getTotalStudents() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        try (var conn = DatabaseHelper.connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getTotalJuries() throws SQLException {
        String sql = "SELECT COUNT(*) FROM juries";
        try (var conn = DatabaseHelper.connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getTotalProjects() throws SQLException {
        String sql = "SELECT COUNT(*) FROM projects";
        try (var conn = DatabaseHelper.connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getTotalSupervisors() throws SQLException {
        String sql = "SELECT COUNT(*) FROM supervisors";
        try (var conn = DatabaseHelper.connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}