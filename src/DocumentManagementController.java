import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;

public class DocumentManagementController {

    @FXML
    private TextField typeField;

    @FXML
    private TextArea contentArea;

    @FXML
    private ComboBox<Projet> projectComboBox;

    @FXML
    private TableView<DocumentAdministratif> documentTable;

    @FXML
    private TableColumn<DocumentAdministratif, String> idColumn;

    @FXML
    private TableColumn<DocumentAdministratif, String> typeColumn;

    @FXML
    private TableColumn<DocumentAdministratif, String> dateColumn;

    @FXML
    private TableColumn<DocumentAdministratif, String> projectColumn;

    @FXML
    private Button addDocumentButton;

    @FXML
    private Button exportPdfButton;

    private ObservableList<DocumentAdministratif> documentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind table columns to DocumentAdministratif properties
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate().toString()));
        projectColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getProjet() != null ? cellData.getValue().getProjet().getTitre() : "Aucun"));

        // Load projects into ComboBox
        loadProjects();

        // Load documents into TableView
        loadDocuments();
    }

    private void loadProjects() {
        try {
            List<Projet> projects = ProjetDAO.getAllProjects();
            projectComboBox.setItems(FXCollections.observableArrayList(projects));
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les projets : " + e.getMessage());
        }
    }

    private void loadDocuments() {
        try {
            List<DocumentAdministratif> documents = DocumentAdministratifDAO.getAllDocuments();
            documentList.setAll(documents);
            documentTable.setItems(documentList);
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les documents : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddDocument() {
        try {
            String type = typeField.getText().trim();
            String content = contentArea.getText().trim();
            Projet selectedProject = projectComboBox.getValue();

            if (type.isEmpty() || content.isEmpty()) {
                showError("Erreur", "Veuillez remplir tous les champs obligatoires !");
                return;
            }

            DocumentAdministratif document = new DocumentAdministratif(
                    generateDocumentId(), type, content, selectedProject, new java.util.Date()
            );
            document.saveToDatabase();
            loadDocuments();
            clearFields();
            showInfo("Succes", "Document ajoute avec succes !");
        } catch (SQLException e) {
            showError("Erreur", "Impossible d'ajouter le document : " + e.getMessage());
        }
    }

    @FXML
    private void handleExportToPdf() {
        DocumentAdministratif selectedDocument = documentTable.getSelectionModel().getSelectedItem();
        if (selectedDocument == null) {
            showError("Erreur", "Veuillez sélectionner un document à exporter !");
            return;
        }

        try {
            PdfExporter.exportDocumentToPdf(selectedDocument);
            showInfo("Succès", "Document exporté en PDF avec succès !");
        } catch (Exception e) {
            showError("Erreur", "Impossible d'exporter le document : " + e.getMessage());
        }
    }

    private void clearFields() {
        typeField.clear();
        contentArea.clear();
        projectComboBox.setValue(null);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String generateDocumentId() {
        return "DOC-" + System.currentTimeMillis();
    }
}