import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


import java.sql.SQLException;
import java.util.List;

public class StudentManagementController {

    @FXML
    private TabPane studentTabPane;

    @FXML
    private ObservableList<Etudiant> studentList = FXCollections.observableArrayList();

    

    @FXML
    private TextField formationField; // Ensure this matches the fx:id in the FXML file

    @FXML
    private RadioButton maleRadio, femaleRadio;

    @FXML
    private ComboBox<String> formationComboBox;

    @FXML
    private TableView<Etudiant> studentTable;

    @FXML
    private Button searchStudentsButton;

    @FXML
    private TextField cinField, nameField, emailField, phoneField;



    @FXML
    private void handleAddStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addStudent.fxml"));
            Parent addStudentRoot = loader.load();
            BorderPane rootLayout = (BorderPane) searchStudentsButton.getScene().getRoot();
            rootLayout.setCenter(addStudentRoot);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de l'ecran d'ajout d'etudiant : " + e.getMessage());
        }
    }

    @FXML
    private void handleViewStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("viewStudent.fxml"));
            Parent viewStudentRoot = loader.load();
            BorderPane rootLayout = (BorderPane) searchStudentsButton.getScene().getRoot();
            rootLayout.setCenter(viewStudentRoot);
        } catch (Exception e) {
            e.printStackTrace();
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdateStudent() {
        try {
            // Load the updateStudent.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("updateStudent.fxml"));
            Parent updateStudentRoot = loader.load();

            // Get the root layout and replace the center content
            BorderPane rootLayout = (BorderPane) searchStudentsButton.getScene().getRoot();
            rootLayout.setCenter(updateStudentRoot);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de l'ecran de modification de l'etudiant : " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoveStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("deleteStudent.fxml"));
            Parent deleteStudentRoot = loader.load();
            BorderPane rootLayout = (BorderPane) searchStudentsButton.getScene().getRoot();
            rootLayout.setCenter(deleteStudentRoot);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de l'ecran de suppression de l'etudiant : " + e.getMessage());
        }
    }

    @FXML
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}