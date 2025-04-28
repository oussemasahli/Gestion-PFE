import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML
    private BorderPane rootLayout;

    @FXML
    private AnchorPane navigationPane;

    @FXML
    public void initialize() {
        // Load the Acceuil view as the default view
        loadCenterContent("acceuil.fxml");
    }

    @FXML
    public void toggleNavigationPane() {
        boolean isVisible = navigationPane.isVisible();
        navigationPane.setVisible(!isVisible);
        navigationPane.setManaged(!isVisible); // Ensures layout adjusts when hidden
    }

    @FXML
    private void showAcceuil() {
        loadCenterContent("acceuil.fxml");
    }

    @FXML
    private void showStudentManagement() {
        loadCenterContent("student_management.fxml");
    }

    @FXML
    private void showJuryManagement() {
        loadCenterContent("jury_management.fxml");
    }

    @FXML
    private void showSoutenanceManagement() {
        loadCenterContent("soutenance_management.fxml");
    }

    @FXML
    private void showProjetManagement() {
        loadCenterContent("projet_management.fxml");
    }

    @FXML
    private void showAssignmentManagement() {
        loadCenterContent("AssignmentManagement.fxml");
    }

    @FXML
    private void showEncadrantManagement() {
        loadCenterContent("encadrant_management.fxml");
    }

    @FXML
    private void showDocumentManagement() {
        loadCenterContent("document_management.fxml");
    }

    private void loadCenterContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent content = loader.load();
            rootLayout.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}