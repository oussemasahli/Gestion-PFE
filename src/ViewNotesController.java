import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public class ViewNotesController {

    @FXML
    private TableView<Note> notesTable;

    @FXML
    private TableColumn<Note, String> projectIdColumn;
    @FXML
    private TableColumn<Note, String> noteTravailColumn, noteRapportColumn, notePresentationColumn, appreciationColumn;

    @FXML
    private TextField encadrantIdField, noteTravailField, noteRapportField, notePresentationField, appreciationField;

    @FXML
    private ComboBox<String> juryComboBox;

    @FXML
    private ComboBox<String> projectComboBox;

    private ObservableList<Note> notesList = FXCollections.observableArrayList();

    private String studentId;

    private Map<String, String> juryMap = new HashMap<>(); // Map jury names to IDs

    private Map<String, String> projectMap = new HashMap<>(); // Map project names to IDs

    public void setStudentId(String studentId) {
        this.studentId = studentId;
        loadNotes();
    }

    @FXML
    public void initialize() {
        // Initialize Table Columns
        projectIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdProjet()));
        noteTravailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNoteTravail().toString()));
        noteRapportColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNoteRapport().toString()));
        notePresentationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNotePresentation().toString()));
        appreciationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAppreciation()));

        // Populate the ComboBoxes
        loadProjects();
        loadJuries();

        // Add listener to populate text fields when a note is selected
        notesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populateFields(newValue));
    }

    private void loadNotes() {
        try {
            List<Note> notes = NoteDAO.getNotesByStudentId(studentId);
            notesList.clear();
            notesList.addAll(notes);
            notesTable.setItems(notesList);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error", "Erreur lors du chargement des notes : " + e.getMessage());
        }
    }

    private void loadJuries() {
        try {
            List<Jury> juries = JuryDAO.getAllJuries(); // Fetch all juries from the database
            for (Jury jury : juries) {
                juryMap.put(jury.getName(), jury.getId()); // Map jury name to ID
                juryComboBox.getItems().add(jury.getName()); // Add jury name to ComboBox
            }
        } catch (SQLException e) {
            showError("Error", "Erreur lors du chargement des jurys : " + e.getMessage());
        }
    }

    private void loadProjects() {
        try {
            List<Projet> projects = ProjetDAO.getAllProjects(); // Fetch all projects from the database
            for (Projet project : projects) {
                projectMap.put(project.getTitre(), project.getId()); // Map project title to ID
                projectComboBox.getItems().add(project.getTitre()); // Add project title to ComboBox
            }
        } catch (SQLException e) {
            showError("Error", "Erreur lors du chargement des projets : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddNote() {
        try {
            // Get input values
            String projectName = projectComboBox.getValue(); // Get selected project name
            String juryName = juryComboBox.getValue(); // Get selected jury name
            String travailStr = noteTravailField.getText().trim();
            String rapportStr = noteRapportField.getText().trim();
            String presentationStr = notePresentationField.getText().trim();
            String appreciation = appreciationField.getText().trim();

            // Validate inputs
            if (projectName == null || juryName == null || travailStr.isEmpty() || rapportStr.isEmpty() || presentationStr.isEmpty() || appreciation.isEmpty()) {
                showError("Invalid Input", "All fields are required.");
                return;
            }

            // Get the project ID and jury ID from the selected names
            String projectId = projectMap.get(projectName);
            String juryId = juryMap.get(juryName);

            // Check if the combination of student_id, project_id, and jury_id is unique
            if (NoteDAO.noteExists(studentId, projectId, juryId)) {
                showError("Duplicate Note", "A note with the same student, project, and jury already exists.");
                return;
            }

            // Parse numeric values
            BigDecimal noteTravail = parseBigDecimal(travailStr, "Travail");
            BigDecimal noteRapport = parseBigDecimal(rapportStr, "Rapport");
            BigDecimal notePresentation = parseBigDecimal(presentationStr, "Presentation");

            if (noteTravail == null || noteRapport == null || notePresentation == null) {
                return; // Error already shown in parseBigDecimal
            }

            // Create a new Note object
            Note note = new Note();
            note.setIdEtudiant(studentId);
            note.setIdProjet(projectId);
            note.setIdEncadrant(juryId); // Use jury ID instead of encadrant ID
            note.setNoteTravail(noteTravail);
            note.setNoteRapport(noteRapport);
            note.setNotePresentation(notePresentation);
            note.setAppreciation(appreciation);

            // Save the note to the database
            if (NoteDAO.ajouterNote(note)) {
                loadNotes(); // Refresh the table
                showSuccess("Note added successfully!");
                clearFields(); // Clear input fields
            } else {
                showError("Error", "Failed to add the note.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "An unexpected error occurred while adding the note.");
        }
    }

    @FXML
    private void handleModifyNote() {
        try {
            // Get input values
            String projectName = projectComboBox.getValue(); // Get selected project name
            String juryName = juryComboBox.getValue(); // Get selected jury name
            String travailStr = noteTravailField.getText().trim();
            String rapportStr = noteRapportField.getText().trim();
            String presentationStr = notePresentationField.getText().trim();
            String appreciation = appreciationField.getText().trim();

            // Validate inputs
            if (projectName == null || juryName == null || travailStr.isEmpty() || rapportStr.isEmpty() || presentationStr.isEmpty() || appreciation.isEmpty()) {
                showError("Invalid Input", "All fields are required.");
                return;
            }

            // Get the project ID and jury ID from the selected names
            String projectId = projectMap.get(projectName);
            String juryId = juryMap.get(juryName);

            // Check if the selected note exists
            Note selectedNote = notesTable.getSelectionModel().getSelectedItem();
            if (selectedNote == null) {
                showError("No Selection", "Please select a note to modify.");
                return;
            }

            // Check if the new combination of student_id, project_id, and jury_id is unique
            if (!selectedNote.getIdProjet().equals(projectId) || !selectedNote.getIdEncadrant().equals(juryId)) {
                if (NoteDAO.noteExists(studentId, projectId, juryId)) {
                    showError("Duplicate Note", "A note with the same student, project, and jury already exists.");
                    return;
                }
            }

            // Parse numeric values
            BigDecimal noteTravail = parseBigDecimal(travailStr, "Travail");
            BigDecimal noteRapport = parseBigDecimal(rapportStr, "Rapport");
            BigDecimal notePresentation = parseBigDecimal(presentationStr, "Presentation");

            if (noteTravail == null || noteRapport == null || notePresentation == null) {
                return; // Error already shown in parseBigDecimal
            }

            // Update the selected note
            selectedNote.setIdProjet(projectId);
            selectedNote.setIdEncadrant(juryId);
            selectedNote.setNoteTravail(noteTravail);
            selectedNote.setNoteRapport(noteRapport);
            selectedNote.setNotePresentation(notePresentation);
            selectedNote.setAppreciation(appreciation);

            // Save the updated note to the database
            NoteDAO.updateNote(selectedNote);
            loadNotes(); // Refresh the table
            showSuccess("Note modified successfully!");
            clearFields(); // Clear input fields
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "An error occurred while modifying the note.");
        }
    }

    private BigDecimal parseBigDecimal(String value, String fieldName) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            showError("Invalid Input", "Please enter a valid number for " + fieldName + ".");
            return null;
        }
    }

    private void populateFields(Note note) {
        if (note != null) {
            projectComboBox.setValue(getProjectNameById(note.getIdProjet())); // Select project by ID
            juryComboBox.setValue(getJuryNameById(note.getIdEncadrant())); // Select jury by ID
            noteTravailField.setText(note.getNoteTravail().toString());
            noteRapportField.setText(note.getNoteRapport().toString());
            notePresentationField.setText(note.getNotePresentation().toString());
            appreciationField.setText(note.getAppreciation());
        } else {
            clearFields(); // Clear fields if no note is selected
        }
    }

    private String getProjectNameById(String projectId) {
        for (Map.Entry<String, String> entry : projectMap.entrySet()) {
            if (entry.getValue().equals(projectId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getJuryNameById(String juryId) {
        for (Map.Entry<String, String> entry : juryMap.entrySet()) {
            if (entry.getValue().equals(juryId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void clearFields() {
        try {
            if (projectComboBox != null) {
                projectComboBox.getSelectionModel().clearSelection();
            }
            if (juryComboBox != null) {
                juryComboBox.getSelectionModel().clearSelection();
            }
            if (noteTravailField != null) {
                noteTravailField.clear();
            }
            if (noteRapportField != null) {
                noteRapportField.clear();
            }
            if (notePresentationField != null) {
                notePresentationField.clear();
            }
            if (appreciationField != null) {
                appreciationField.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "An error occurred while clearing the input fields.");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("viewStudent.fxml"));
            Parent viewStudentRoot = loader.load();

            BorderPane rootLayout = (BorderPane) notesTable.getScene().getRoot();
            rootLayout.setCenter(viewStudentRoot);
        } catch (Exception e) {
            showError("Error", "Erreur lors du retour a la gestion des etudiants : " + e.getMessage());
        }
    }
}