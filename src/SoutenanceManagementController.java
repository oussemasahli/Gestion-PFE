import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SoutenanceManagementController {

    @FXML
    private TableView<Soutenance> soutenanceTable;

    @FXML
    private TableColumn<Soutenance, String> idColumn, studentColumn, juryColumn, salleColumn;

    @FXML
    private TableColumn<Soutenance, String> dateColumn;

    @FXML
    private TableColumn<Soutenance, String> projectColumn;

    @FXML
    private TableColumn<Soutenance, String> supervisorColumn;

    @FXML
    private TableColumn<Soutenance, Void> actionsColumn;

    @FXML
    private TextField salleField, dateField;
    @FXML
    private TextField filterIdField, filterStudentField, filterJuryField, filterSalleField, filterDateField;
    @FXML
    private TextField filterProjectField; // New filter field for project

    @FXML
    private ComboBox<String> studentComboBox;

    @FXML
    private ComboBox<String> juryComboBox;

    @FXML
    private ComboBox<String> projectComboBox;

    @FXML
    private ComboBox<String> encadrantComboBox;

    @FXML
    private ComboBox<String> filterEncadrantComboBox;

    @FXML
    private Button addSoutenanceButton, updateSoutenanceButton, cancelUpdateButton, refreshButton;

    private ObservableList<Soutenance> soutenanceList = FXCollections.observableArrayList();
    private FilteredList<Soutenance> filteredSoutenances;
    private ObservableList<Etudiant> allStudents = FXCollections.observableArrayList();
    private ObservableList<Jury> allJuries = FXCollections.observableArrayList();
    private ObservableList<Projet> allProjects = FXCollections.observableArrayList();
    private ObservableList<Encadrant> allEncadrants = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize Table Columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("etudiantName"));
        juryColumn.setCellValueFactory(new PropertyValueFactory<>("juryName"));
        salleColumn.setCellValueFactory(new PropertyValueFactory<>("salle"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        projectColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));

        // New column binding for supervisor
        supervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisorName"));

        // Initialize FilteredList and bind it to the TableView
        filteredSoutenances = new FilteredList<>(soutenanceList, p -> true);
        soutenanceTable.setItems(filteredSoutenances);

        // Add action buttons to the Actions column
        addActionsToTable();

        // Load all soutenances into the table
        loadSoutenances();

        // Load students, juries, and projects into their ComboBoxes
        loadStudentsIntoComboBox();
        loadJuriesIntoComboBox();
        loadProjectsIntoComboBox();
        loadEncadrantsIntoComboBox(); // Load encadrants into ComboBox

        loadEncadrantsIntoComboBox();
        loadEncadrantsIntoFilterComboBox(); // Load encadrants into filter ComboBox

        // Add listeners to filter fields
        filterIdField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterStudentField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterJuryField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterSalleField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterDateField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterProjectField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterEncadrantComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        // Set up button actions
        addSoutenanceButton.setOnAction(event -> addSoutenance());
        cancelUpdateButton.setOnAction(event -> cancelUpdateMode());
    }

    private void applyFilters() {
        filteredSoutenances.setPredicate(soutenance -> {

            // Filter by ID
            if (!filterIdField.getText().isEmpty() && !soutenance.getId().toLowerCase().contains(filterIdField.getText().toLowerCase())) {
                return false;
            }

            // Filter by Student (Name or ID)
            if (!filterStudentField.getText().isEmpty()) {
                String filterText = filterStudentField.getText().toLowerCase();
                if (soutenance.getEtudiant() == null || 
                    (!soutenance.getEtudiantName().toLowerCase().contains(filterText) && 
                     !soutenance.getEtudiant().getId().toLowerCase().contains(filterText))) {
                    return false;
                }
            }

            // Filter by Jury
            if (!filterJuryField.getText().isEmpty()) {
                String filterText = filterJuryField.getText().toLowerCase();
                if (soutenance.getJury() == null || 
                    !soutenance.getJuryName().toLowerCase().contains(filterText)) {
                    return false;
                }
            }

            // Filter by Salle
            if (!filterSalleField.getText().isEmpty() && !soutenance.getSalle().toLowerCase().contains(filterSalleField.getText().toLowerCase())) {
                return false;
            }

            // Filter by Date
            if (!filterDateField.getText().isEmpty() && !soutenance.getFormattedDate().toLowerCase().contains(filterDateField.getText().toLowerCase())) {
                return false;
            }

            // Filter by Project Name
            if (!filterProjectField.getText().isEmpty()) {
                String filterText = filterProjectField.getText().toLowerCase();
                if (!soutenance.getProjectName().toLowerCase().contains(filterText)) {
                    return false;
                }
            }

            // Filter by Encadrant
            String selectedEncadrant = filterEncadrantComboBox.getValue();
            if (selectedEncadrant != null && !selectedEncadrant.equals("Tous")) {
                if (soutenance.getEncadrant() == null || 
                    !soutenance.getEncadrant().getNom().equalsIgnoreCase(selectedEncadrant)) {
                    return false;
                }
            }

            return true; // Include the soutenance if all filters pass
        });
    }

    private void loadSoutenances() {
        try {
            List<Soutenance> soutenances = SoutenanceDAO.getAllSoutenances();
            System.out.println("Fetched soutenances: " + soutenances.size()); // Debugging statement
            soutenanceList.clear();
            soutenanceList.addAll(soutenances);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des soutenances : " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for debugging
        }
    }

    private void loadStudentsIntoComboBox() {
        try {
            // Fetch all students from the database
            List<Etudiant> students = EtudiantDAO.getAllEtudiants();
            allStudents.setAll(students);

            // Populate the ComboBox with student names
            ObservableList<String> studentNames = FXCollections.observableArrayList();
            for (Etudiant student : students) {
                studentNames.add(student.getNom());
            }
            studentComboBox.setItems(studentNames);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des etudiants : " + e.getMessage());
        }
    }

    private void loadJuriesIntoComboBox() {
        try {
            // Fetch all juries from the database
            List<Jury> juries = JuryDAO.getAllJuries();
            allJuries.setAll(juries);

            // Populate the ComboBox with jury names
            ObservableList<String> juryNames = FXCollections.observableArrayList();
            for (Jury jury : juries) {
                juryNames.add(jury.getName());
            }
            juryComboBox.setItems(juryNames);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des jurys : " + e.getMessage());
        }
    }

    private void loadProjectsIntoComboBox() {
        try {
            // Fetch all projects from the database
            List<Projet> projects = ProjetDAO.getAllProjects();
            allProjects.setAll(projects);

            // Populate the ComboBox with project titles
            ObservableList<String> projectTitles = FXCollections.observableArrayList();
            for (Projet project : projects) {
                projectTitles.add(project.getTitre()); // Use project titles instead of IDs
            }
            projectComboBox.setItems(projectTitles);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des projets : " + e.getMessage());
        }
    }

    private void loadEncadrantsIntoComboBox() {
        try {
            // Fetch all available encadrants from the database
            List<Encadrant> encadrants = EncadrantDAO.getAvailableEncadrants();
            allEncadrants.setAll(encadrants);

            // Populate the ComboBox with encadrant names
            ObservableList<String> encadrantNames = FXCollections.observableArrayList();
            for (Encadrant encadrant : encadrants) {
                encadrantNames.add(encadrant.getNom());
            }
            encadrantComboBox.setItems(encadrantNames);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des encadrants : " + e.getMessage());
        }
    }

    private void loadEncadrantsIntoFilterComboBox() {
        try {
            // Fetch all supervisors from the database
            List<Encadrant> encadrants = EncadrantDAO.getAllEncadrants();

            // Populate the ComboBox with supervisor names
            ObservableList<String> encadrantNames = FXCollections.observableArrayList();
            encadrantNames.add("Tous"); // Add "Tous" for no filtering
            for (Encadrant encadrant : encadrants) {
                encadrantNames.add(encadrant.getNom());
            }
            filterEncadrantComboBox.setItems(encadrantNames);
            filterEncadrantComboBox.setValue("Tous"); // Default value
        } catch (SQLException e) {
            showError("Erreur lors du chargement des encadrants : " + e.getMessage());
        }
    }

    private void addSoutenance() {
        try {
            // Validate input fields
            if (studentComboBox.getValue() == null || juryComboBox.getValue() == null || projectComboBox.getValue() == null || salleField.getText().isEmpty() || encadrantComboBox.getValue() == null) {
                showError("Veuillez remplir tous les champs obligatoires !");
                return;
            }

            // Fetch the selected Encadrant
            String selectedEncadrantName = encadrantComboBox.getValue();
            Encadrant encadrant = allEncadrants.stream()
                    .filter(e -> e.getNom().equals(selectedEncadrantName))
                    .findFirst()
                    .orElse(null);

            if (encadrant == null) {
                showError("Aucun encadrant trouve avec le nom : " + selectedEncadrantName);
                return;
            }

            // Create a new Soutenance object
            Soutenance soutenance = new Soutenance();
            soutenance.setId(SoutenanceDAO.generateNextSoutenanceId());
            soutenance.setEtudiant(getSelectedEtudiant());
            soutenance.setJury(getSelectedJury());
            soutenance.setDate(getSelectedDate());
            soutenance.setProjectId(getSelectedProject().getId());
            soutenance.setSalle(salleField.getText());
            soutenance.setEncadrant(encadrant);

            // Add the soutenance to the database
            SoutenanceDAO.addSoutenance(soutenance);

            // Refresh the table
            loadSoutenances();

            // Clear input fields
            resetInputFields();

            showInfo("Soutenance ajoutee avec succès !");
        } catch (SQLException e) {
            showError("Erreur : " + e.getMessage());
        } catch (Exception e) {
            showError("Erreur lors de l'ajout de la soutenance : " + e.getMessage());
        }
    }

    private void resetInputFields() {
        // Clear ComboBox selections
        studentComboBox.getSelectionModel().clearSelection();
        juryComboBox.getSelectionModel().clearSelection();
        projectComboBox.getSelectionModel().clearSelection();
        encadrantComboBox.getSelectionModel().clearSelection();

        // Clear TextFields
        salleField.clear();
        dateField.clear();
    }

    private void addActionsToTable() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                updateButton.setOnAction(event -> {
                    Soutenance soutenance = getTableView().getItems().get(getIndex());
                    if (soutenance != null) {
                        handleUpdateSoutenance(soutenance);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Soutenance soutenance = getTableView().getItems().get(getIndex());
                    if (soutenance != null) {
                        handleDeleteSoutenance(soutenance);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null); // Clear the cell if it's empty
                } else {
                    HBox buttons = new HBox(10, updateButton, deleteButton);
                    buttons.setStyle("-fx-alignment: center;");
                    setGraphic(buttons); // Set the buttons as the graphic
                }
            }
        });
    }

    private void handleUpdateSoutenance(Soutenance soutenance) {
        try {
            // Debugging: Print the soutenance details
            System.out.println("Updating Soutenance: " + soutenance.getId());
            System.out.println("Project ID: " + soutenance.getProjectId());

            // Find the project by ID and set the title in the ComboBox
            Projet project = ProjetDAO.getProjetById(soutenance.getProjectId());
            if (project != null) {
                projectComboBox.setValue(project.getTitre()); // Set the project title
            } else {
                projectComboBox.setValue(null);
            }

            // Populate the other input fields
            studentComboBox.setValue(soutenance.getEtudiant().getNom());
            juryComboBox.setValue(soutenance.getJury().getName());
            salleField.setText(soutenance.getSalle());
            dateField.setText(soutenance.getFormattedDate());
            encadrantComboBox.setValue(soutenance.getEncadrant() != null ? soutenance.getEncadrant().getNom() : null);

            // Show the update button and cancel button, hide the add button
            addSoutenanceButton.setVisible(false);
            updateSoutenanceButton.setVisible(true);
            cancelUpdateButton.setVisible(true);

            // Set the action for the update button
            updateSoutenanceButton.setOnAction(event -> {
                try {
                    // Validate the updated data
                    if (studentComboBox.getValue() == null || juryComboBox.getValue() == null || projectComboBox.getValue() == null || salleField.getText().isEmpty() || encadrantComboBox.getValue() == null) {
                        showError("Veuillez remplir tous les champs obligatoires !");
                        return;
                    }

                    // Fetch the updated Encadrant
                    String selectedEncadrantName = encadrantComboBox.getValue();
                    Encadrant encadrant = allEncadrants.stream()
                            .filter(e -> e.getNom().equals(selectedEncadrantName))
                            .findFirst()
                            .orElse(null);

                    if (encadrant == null) {
                        showError("Aucun encadrant trouvé avec le nom : " + selectedEncadrantName);
                        return;
                    }

                    // Update the soutenance object
                    soutenance.setEtudiant(getSelectedEtudiant());
                    soutenance.setJury(getSelectedJury());
                    soutenance.setDate(getSelectedDate());
                    soutenance.setProjectId(getSelectedProject().getId());
                    soutenance.setSalle(salleField.getText());
                    soutenance.setEncadrant(encadrant);

                    // Update the soutenance in the database
                    SoutenanceDAO.updateSoutenance(soutenance);

                    // Refresh the table
                    loadSoutenances();

                    // Reset to add mode
                    resetToAddMode();

                    showInfo("Soutenance mise à jour avec succès !");
                } catch (Exception e) {
                    showError("Erreur lors de la mise à jour de la soutenance : " + e.getMessage());
                }
            });
        } catch (Exception e) {
            showError("Erreur lors de la sélection de la soutenance : " + e.getMessage());
        }
    }

    private void updateSoutenance() {
        // This method is intentionally left empty because the action is set dynamically in handleUpdateSoutenance.
    }

    private void cancelUpdateMode() {
        resetToAddMode();
    }

    private void resetToAddMode() {
        // Clear input fields
        resetInputFields();

        // Show the add button, hide the update and cancel buttons
        addSoutenanceButton.setVisible(true);
        updateSoutenanceButton.setVisible(false);
        cancelUpdateButton.setVisible(false);
    }

    private void handleDeleteSoutenance(Soutenance soutenance) {
        try {
            SoutenanceDAO.deleteSoutenance(soutenance.getId());
            loadSoutenances(); // Reload the data
            soutenanceTable.refresh(); // Refresh the TableView
            showInfo("Soutenance supprimee avec succes !");
        } catch (Exception e) {
            showError("Erreur lors de la suppression de la soutenance : " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void clearFilters() {
        filterIdField.clear();
        filterStudentField.clear();
        filterJuryField.clear();
        filterSalleField.clear();
        filterDateField.clear();
        filterProjectField.clear();
        filterEncadrantComboBox.setValue("Tous"); // Reset to "Tous"
        applyFilters(); // Reapply filters to show all items
    }

    private Etudiant getSelectedEtudiant() throws SQLException {
        String selectedStudentName = studentComboBox.getValue();
        if (selectedStudentName == null) {
            throw new IllegalArgumentException("Aucun etudiant selectionne !");
        }
        return allStudents.stream()
                .filter(student -> student.getNom().equals(selectedStudentName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("etudiant introuvable : " + selectedStudentName));
    }

    private Jury getSelectedJury() throws SQLException {
        String selectedJuryName = juryComboBox.getValue();
        if (selectedJuryName == null) {
            throw new IllegalArgumentException("Aucun jury selectionne !");
        }
        return allJuries.stream()
                .filter(jury -> jury.getName().equals(selectedJuryName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Jury introuvable : " + selectedJuryName));
    }

    private Date getSelectedDate() throws IllegalArgumentException {
        String dateText = dateField.getText();
        if (dateText == null || dateText.isEmpty()) {
            throw new IllegalArgumentException("Aucune date selectionnee !");
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.parse(dateText);
        } catch (Exception e) {
            throw new IllegalArgumentException("Format de date invalide. Utilisez 'yyyy-MM-dd HH:mm:ss'.");
        }
    }

    private Projet getSelectedProject() throws SQLException {
        String selectedProjectTitle = projectComboBox.getValue();
        if (selectedProjectTitle == null) {
            throw new IllegalArgumentException("Aucun projet selectionne !");
        }

        // Debugging: Print the selected project title and all available projects
        System.out.println("Selected Project Title: " + selectedProjectTitle);
        System.out.println("Available Projects:");
        for (Projet project : allProjects) {
            System.out.println("- " + project.getTitre());
        }

        return allProjects.stream()
                .filter(project -> project.getTitre().equalsIgnoreCase(selectedProjectTitle))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable : " + selectedProjectTitle));
    }
}