import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.sql.SQLException;
import java.util.List;

public class UpdateStudentController {

    @FXML
    private TextField cinField, nameField, emailField, phoneField;

    @FXML
    private ComboBox<String> formationComboBox, niveauComboBox;

    @FXML
    private RadioButton maleRadio, femaleRadio;

    private ToggleGroup genderGroup;

    private Etudiant selectedStudent;

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

    public void setStudentData(Etudiant student) {
        this.selectedStudent = student;

        // Populate fields with the selected student's data
        cinField.setText(student.getId());
        nameField.setText(student.getNom());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getNumeroTel());

        // Split the formation into name and niveau if possible
        String[] formationParts = student.getFormation().split(" - Niveau ");
        if (formationParts.length == 2) {
            formationComboBox.setValue(formationParts[0]);
            niveauComboBox.setValue(formationParts[1]);
        }

        if ("Homme".equalsIgnoreCase(student.getSexe())) {
            maleRadio.setSelected(true);
        } else if ("Femme".equalsIgnoreCase(student.getSexe())) {
            femaleRadio.setSelected(true);
        }
    }

    @FXML
    private void handleUpdateStudent() {
        try {
            // Get updated values
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
            String fullFormation = formation + " - Niveau " + niveau;

            // Update the student object
            selectedStudent.setNom(name);
            selectedStudent.setEmail(email);
            selectedStudent.setNumeroTel(phone);
            selectedStudent.setFormation(fullFormation);
            selectedStudent.setSexe(gender);

            // Update the student in the database
            EtudiantDAO.updateEtudiant(selectedStudent);

            // Show success message
            showSuccess("etudiant mis a jour avec succes !");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la mise a jour de l'etudiant : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        try {
            // Load the View Student screen (viewStudent.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("viewStudent.fxml"));
            Parent viewStudentRoot = loader.load();

            // Get the current scene and replace the center content with the View Student layout
            BorderPane rootLayout = (BorderPane) cinField.getScene().getRoot();
            rootLayout.setCenter(viewStudentRoot);
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