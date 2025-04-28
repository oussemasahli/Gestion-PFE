import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashSet;
import java.util.Set;

public class AssignmentManagementController {

    @FXML
    private TableView<Assignment> assignmentTable;

    @FXML
    private TableColumn<Assignment, String> studentColumn;

    @FXML
    private TableColumn<Assignment, String> projectColumn;

    @FXML
    private ComboBox<String> studentComboBox;

    @FXML
    private ComboBox<String> projectComboBox;

    @FXML
    private TextField studentFilterField; // New filter field for Student ID

    @FXML
    private TextField projectFilterField; // New filter field for Project ID

    private ObservableList<Assignment> assignments = FXCollections.observableArrayList();
    private FilteredList<Assignment> filteredAssignments; // Filtered list for the TableView

    @FXML
    public void initialize() {
        // Set up table columns
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        projectColumn.setCellValueFactory(new PropertyValueFactory<>("projectId"));

        // Load data into the table
        loadAssignments();

        // Wrap the assignments list in a FilteredList
        filteredAssignments = new FilteredList<>(assignments, p -> true);
        assignmentTable.setItems(filteredAssignments);

        // Load students and projects into ComboBoxes
        loadStudents();
        loadProjects();
    }

    private void loadAssignments() {
        try {
            // Use AssignmentsDAO to fetch all assignments
            ObservableList<Assignment> assignmentList = FXCollections.observableArrayList();
            for (String assignment : AssignmentsDAO.getAllAssignments()) {
                String[] parts = assignment.split(" est assigne au projet ");
                assignmentList.add(new Assignment(parts[0].replace("L'etudiant ", ""), parts[1]));
            }
            assignments.setAll(assignmentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStudents() {
        try {
            // Extract unique student IDs from assignments
            ObservableList<String> studentIds = FXCollections.observableArrayList();
            for (String assignment : AssignmentsDAO.getAllAssignments()) {
                String studentId = assignment.split(" est assigne au projet ")[0].replace("L'etudiant ", "");
                if (!studentIds.contains(studentId)) {
                    studentIds.add(studentId);
                }
            }
            studentComboBox.setItems(studentIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProjects() {
        try {
            // Extract unique project IDs from assignments
            ObservableList<String> projectIds = FXCollections.observableArrayList();
            for (String assignment : AssignmentsDAO.getAllAssignments()) {
                String projectId = assignment.split(" est assigne au projet ")[1];
                if (!projectIds.contains(projectId)) {
                    projectIds.add(projectId);
                }
            }
            projectComboBox.setItems(projectIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFilterAssignments() {
        String studentFilter = studentFilterField.getText().toLowerCase();
        String projectFilter = projectFilterField.getText().toLowerCase();

        // Update the filter predicate
        filteredAssignments.setPredicate(assignment -> {
            boolean matchesStudent = studentFilter.isEmpty() || assignment.getStudentId().toLowerCase().contains(studentFilter);
            boolean matchesProject = projectFilter.isEmpty() || assignment.getProjectId().toLowerCase().contains(projectFilter);
            return matchesStudent && matchesProject;
        });
    }

    @FXML
    private void handleClearFilters() {
        studentFilterField.clear();
        projectFilterField.clear();
        filteredAssignments.setPredicate(p -> true); // Show all assignments
    }

    @FXML
    private void handleAddAssignment() {
        String studentId = studentComboBox.getValue();
        String projectId = projectComboBox.getValue();

        if (studentId == null || projectId == null) {
            System.out.println("Please select both a student and a project.");
            return;
        }

        try {
            System.out.println("Adding assignment: Student ID = " + studentId + ", Project ID = " + projectId);
            AssignmentsDAO.assignStudentToProject(studentId, projectId);
            loadAssignments(); // Refresh the table
            System.out.println("Assignment added successfully.");
        } catch (Exception e) {
            System.err.println("Error adding assignment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateAssignment() {
        Assignment selectedAssignment = assignmentTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) {
            System.out.println("Please select an assignment to update.");
            return;
        }

        String newStudentId = studentComboBox.getValue();
        String newProjectId = projectComboBox.getValue();

        if (newStudentId == null || newProjectId == null) {
            System.out.println("Please select both a student and a project.");
            return;
        }

        try {
            System.out.println("Updating assignment: Old Student ID = " + selectedAssignment.getStudentId() +
                    ", Old Project ID = " + selectedAssignment.getProjectId() +
                    ", New Student ID = " + newStudentId + ", New Project ID = " + newProjectId);

            // Delete the old assignment
            AssignmentsDAO.removeAssignment(selectedAssignment.getStudentId(), selectedAssignment.getProjectId());

            // Add the new assignment
            AssignmentsDAO.assignStudentToProject(newStudentId, newProjectId);

            loadAssignments(); // Refresh the table
            System.out.println("Assignment updated successfully.");
        } catch (Exception e) {
            System.err.println("Error updating assignment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteAssignment() {
        Assignment selectedAssignment = assignmentTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) {
            System.out.println("Please select an assignment to delete.");
            return;
        }

        try {
            System.out.println("Deleting assignment: Student ID = " + selectedAssignment.getStudentId() +
                    ", Project ID = " + selectedAssignment.getProjectId());
            AssignmentsDAO.removeAssignment(selectedAssignment.getStudentId(), selectedAssignment.getProjectId());
            loadAssignments(); // Refresh the table
            System.out.println("Assignment deleted successfully.");
        } catch (Exception e) {
            System.err.println("Error deleting assignment: " + e.getMessage());
            e.printStackTrace();
        }
    }
}