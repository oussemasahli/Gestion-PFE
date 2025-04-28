import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.util.List;
import java.util.stream.Collectors;

public class DeleteStudentController {



    @FXML
    private ComboBox<String> studentComboBox;

    private ObservableList<String> studentList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadStudents();

        // Add a listener to the ComboBox editor for filtering
        studentComboBox.setEditable(true);
        studentComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            filterStudents(newValue);
        });
    }

    private void loadStudents() {
        try {
            // Fetch all students from the database
            List<Etudiant> students = EtudiantDAO.getAllEtudiants();

            // Populate the ComboBox with student names
            studentList.setAll(students.stream()
                    .map(student -> student.getNom() + " (CIN: " + student.getId() + ")")
                    .collect(Collectors.toList()));
            studentComboBox.setItems(studentList);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des étudiants : " + e.getMessage());
        }
    }

    private void filterStudents(String searchText) {
        // Filter the student list based on the search text
        ObservableList<String> filteredList = studentList.filtered(name -> name.toLowerCase().startsWith(searchText.toLowerCase()));
        studentComboBox.setItems(filteredList);

        // Keep the editor text intact
        studentComboBox.getEditor().setText(searchText);
        if (!filteredList.isEmpty()) {
            studentComboBox.show();
        }
    }


    @FXML
    private void handleDeleteStudent() {
        try {
            // Get the selected student from the ComboBox
            String selectedStudent = studentComboBox.getValue();
            if (selectedStudent == null || selectedStudent.isEmpty()) {
                showError("Veuillez sélectionner un étudiant.");
                return;
            }

            // Extract the CIN from the selected student
            String cin = selectedStudent.substring(selectedStudent.indexOf("CIN: ") + 5, selectedStudent.length() - 1);

            // Check if the student exists
            Etudiant student = EtudiantDAO.getEtudiantById(cin);
            if (student == null) {
                showError("Aucun étudiant trouvé avec le CIN : " + cin);
                return;
            }

            // Delete the student
            EtudiantDAO.deleteEtudiant(cin);

            // Show success message
            showSuccess("Étudiant supprimé avec succès !");
            loadStudents(); // Reload the student list
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la suppression de l'étudiant : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        try {
            // Load the student management screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("student_management.fxml"));
            Parent studentManagementRoot = loader.load();

            // Get the current scene and replace the center content with the student management layout
            BorderPane rootLayout = (BorderPane) studentComboBox.getScene().getRoot();
            rootLayout.setCenter(studentManagementRoot);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du retour à la gestion des étudiants : " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}