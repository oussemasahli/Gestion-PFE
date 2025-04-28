import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class DeleteStudentController {

    @FXML
    private TextField cinField;

    @FXML
    private void handleDeleteStudent() {
        try {
            // Get the CIN from the input field
            String cin = cinField.getText();
            if (cin.isEmpty()) {
                showError("Veuillez entrer le CIN de l'etudiant.");
                return;
            }

            // Check if the student exists
            Etudiant student = EtudiantDAO.getEtudiantById(cin);
            if (student == null) {
                showError("Aucun etudiant trouve avec le CIN : " + cin);
                return;
            }

            // Delete the student
            EtudiantDAO.deleteEtudiant(cin);

            // Show success message
            showSuccess("etudiant supprime avec succes !");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la suppression de l'etudiant : " + e.getMessage());
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