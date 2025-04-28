import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.sql.SQLException;
import java.util.List;

public class AddStudentController {

    @FXML
    private TextField cinField, nameField, emailField, phoneField;

    @FXML
    private ComboBox<String> formationComboBox, niveauComboBox;

    @FXML
    private RadioButton maleRadio, femaleRadio;

    private ToggleGroup genderGroup;

    @FXML
    public void initialize() {
        // Initialize the ToggleGroup for gender
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);

        // Load available formations from the database
        loadFormations();

        // Set options for niveau
        niveauComboBox.setItems(FXCollections.observableArrayList("1", "2", "3"));
    }

    private void loadFormations() {
        try {
            List<String> formations = EtudiantDAO.getAllFormations();
            formationComboBox.setItems(FXCollections.observableArrayList(formations));
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des formations : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddStudent() {
        try {
            // Get input values
            String cin = cinField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String formation = formationComboBox.getValue();
            String niveau = niveauComboBox.getValue();
            String gender = (maleRadio.isSelected()) ? "Homme" : (femaleRadio.isSelected() ? "Femme" : null);

            // Validate inputs
            if (cin.isEmpty() || name.isEmpty() || formation == null || niveau == null || gender == null) {
                showError("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Combine formation and niveau
            String fullFormation = formation +" "+ niveau;

            // Create a new student object
            Etudiant newStudent = new Etudiant(cin, name, phone, email, fullFormation, gender);

            // Add the student to the database
            EtudiantDAO.addEtudiant(newStudent);

            // Show success message
            showSuccess("etudiant ajoute avec succes !");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ajout de l'etudiant : " + e.getMessage());
        }
    }
    

    @FXML
    private void handleCancel() {
        try {
            // Load the student management screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("student_management.fxml"));
            Parent studentManagementRoot = loader.load();

            // Get the current scene and replace the center content with the student management layout
            BorderPane rootLayout = (BorderPane) cinField.getScene().getRoot();
            rootLayout.setCenter(studentManagementRoot);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du retour a la gestion des etudiants : " + e.getMessage());
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
        alert.setTitle("Succes");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}