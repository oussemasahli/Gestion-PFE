import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.sql.SQLException;
import java.util.List;

public class ViewStudentController {

    @FXML
    private TextField cinField, nameField, emailField, phoneField, formationField;

    @FXML
    private ComboBox<String> formationComboBox;

    @FXML
    private ComboBox<String> niveauComboBox;

    @FXML
    private RadioButton maleRadio, femaleRadio;

    private ToggleGroup genderGroup;

    @FXML
    private TableView<Etudiant> studentTable;

    @FXML
    private TableColumn<Etudiant, String> cinColumn, nameColumn, emailColumn, phoneColumn, formationColumn;

    @FXML
    private Button viewNotesButton, modifyButton;

    private ObservableList<Etudiant> studentList = FXCollections.observableArrayList();
    private FilteredList<Etudiant> filteredStudents;

    @FXML
    public void initialize() {
        // Initialize Table Columns for Students
        cinColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        phoneColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNumeroTel()));
        formationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormation()));

        // Initialize the ToggleGroup for gender
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);

        // Initialize FilteredList
        filteredStudents = new FilteredList<>(studentList, etudiant -> true);
        studentTable.setItems(filteredStudents);

        // Add filter listeners
        addFilterListeners();

        // Load All Students
        loadAllStudents();

        // Load Formations into the ComboBox
        loadFormations();

        // Populate Niveau ComboBox
        niveauComboBox.setItems(FXCollections.observableArrayList("1", "2", "3"));
    }

    private void addFilterListeners() {
        cinField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        nameField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        formationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        niveauComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        genderGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        filteredStudents.setPredicate(etudiant -> {
            // Filter by CIN
            if (!cinField.getText().isEmpty() && !etudiant.getId().toLowerCase().contains(cinField.getText().toLowerCase())) {
                return false;
            }

            // Filter by Name
            if (!nameField.getText().isEmpty() && !etudiant.getNom().toLowerCase().contains(nameField.getText().toLowerCase())) {
                return false;
            }

            // Filter by Email
            if (!emailField.getText().isEmpty() && (etudiant.getEmail() == null || !etudiant.getEmail().toLowerCase().contains(emailField.getText().toLowerCase()))) {
                return false;
            }

            // Filter by Phone
            if (!phoneField.getText().isEmpty() && (etudiant.getNumeroTel() == null || !etudiant.getNumeroTel().contains(phoneField.getText()))) {
                return false;
            }

            // Filter by Formation
            String selectedFormation = formationComboBox.getValue();
            if (selectedFormation != null && !selectedFormation.isEmpty() && 
                (etudiant.getFormation() == null || !etudiant.getFormation().toLowerCase().startsWith(selectedFormation.toLowerCase()))) {
                return false;
            }

            // Filter by Niveau
            String selectedNiveau = niveauComboBox.getValue();
            if (selectedNiveau != null && !selectedNiveau.isEmpty() && 
                (etudiant.getFormation() == null || !etudiant.getFormation().contains(" " + selectedNiveau))) {
                return false;
            }

            // Filter by Gender
            if (genderGroup.getSelectedToggle() != null) {
                String selectedGender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
                if (!selectedGender.equalsIgnoreCase(etudiant.getSexe())) {
                    return false;
                }
            }

            return true; // Include the student if all filters pass
        });
    }

    private void loadAllStudents() {
        try {
            List<Etudiant> students = EtudiantDAO.getAllEtudiants();
            studentList.clear();
            studentList.addAll(students);
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void loadFormations() {
        try {
            // Use the getAllFormations() method from EtudiantDAO
            List<String> formations = EtudiantDAO.getAllFormations();
            formationComboBox.getItems().addAll(formations); // Add formations to the ComboBox
        } catch (SQLException e) {
            showError("Erreur lors du chargement des formations : " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleClearFilters() {
        // Clear all filter fields
        cinField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        formationComboBox.setValue(null);
        niveauComboBox.setValue(null);
        genderGroup.selectToggle(null);

        // Reset the filter predicate to show all students
        filteredStudents.setPredicate(etudiant -> true);
    }

    @FXML
    private void handleBack() {
        try {
            // Load the previous screen (student_management.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("student_management.fxml"));
            Parent studentManagementRoot = loader.load();

            // Get the current scene and replace the center content with the previous layout
            BorderPane rootLayout = (BorderPane) studentTable.getScene().getRoot();
            rootLayout.setCenter(studentManagementRoot);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading the previous page: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewNotes() {
        Etudiant selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showError("Veuillez selectionner un etudiant.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("viewNotes.fxml"));
            Parent viewNotesRoot = loader.load();

            ViewNotesController controller = loader.getController();
            controller.setStudentId(selectedStudent.getId());

            BorderPane rootLayout = (BorderPane) studentTable.getScene().getRoot();
            rootLayout.setCenter(viewNotesRoot);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des notes : " + e.getMessage());
        }
    }

    @FXML
    private void handleModifyStudent() {
        Etudiant selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showError("Veuillez selectionner un etudiant.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("updateStudent.fxml"));
            Parent updateStudentRoot = loader.load();

            UpdateStudentController controller = loader.getController();
            controller.setStudentData(selectedStudent);

            BorderPane rootLayout = (BorderPane) studentTable.getScene().getRoot();
            rootLayout.setCenter(updateStudentRoot);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la page de modification : " + e.getMessage());
        }
    }
}