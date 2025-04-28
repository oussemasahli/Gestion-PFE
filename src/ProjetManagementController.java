import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.sql.SQLException;

public class ProjetManagementController {

    @FXML
    private TableView<Projet> projetTable;

    @FXML
    private TableColumn<Projet, String> idColumn, titleColumn, descriptionColumn;

    @FXML
    private TableColumn<Projet, Void> actionsColumn;

    @FXML
    private TextField titleField, descriptionField;
    @FXML
    private TextField filterIdField, filterTitleField;

    @FXML
    private Button addProjetButton, updateProjetButton, cancelUpdateButton;

    private ObservableList<Projet> projetList = FXCollections.observableArrayList();
    private FilteredList<Projet> filteredProjets;

    @FXML
    public void initialize() {
        // Wrap the projetList in a FilteredList
        filteredProjets = new FilteredList<>(projetList, p -> true);

        // Bind the FilteredList to the TableView
        projetTable.setItems(filteredProjets);

        // Initialize Table Columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Add action buttons to the Actions column
        addActionsToTable();

        // Load all projects into the table
        loadProjets();

        // Add listeners to filter fields
        filterIdField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterTitleField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        // Set up button actions
        addProjetButton.setOnAction(event -> addProjet());
        updateProjetButton.setOnAction(event -> updateProjet());
        cancelUpdateButton.setOnAction(event -> cancelUpdateMode());
    }

    private void loadProjets() {
        try {
            projetList.clear();
            projetList.addAll(ProjetDAO.getAllProjects());
            projetTable.refresh(); // Refresh the TableView to reapply the cell factory
            addActionsToTable(); // Reapply the CellFactory for the Actions column
        } catch (SQLException e) {
            showError("Erreur lors du chargement des projets : " + e.getMessage());
        }
    }

    private void addProjet() {
        try {
            // Generate the next project ID
            String id = ProjetDAO.generateNextProjectId();
            String titre = titleField.getText();
            String description = descriptionField.getText();

            // Validate required fields
            if (titre.isEmpty() || description.isEmpty()) {
                showError("Veuillez remplir tous les champs obligatoires !");
                return;
            }

            // Create and add the project
            Projet projet = new Projet(id, titre, description);
            ProjetDAO.addProjet(projet);

            // Reload the projects and reset the form
            loadProjets();
            clearFields();
            showInfo("Projet ajoute avec succes ! ID: " + id);
        } catch (SQLException e) {
            showError("Erreur lors de l'ajout du projet : " + e.getMessage());
        }
    }

    private void addActionsToTable() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttons = new HBox(10, updateButton, deleteButton);

            {
                // Style the buttons
                buttons.setStyle("-fx-alignment: center;");

                // Handle the "Update" action
                updateButton.setOnAction(event -> {
                    Projet projet = getTableView().getItems().get(getIndex());
                    if (projet != null) {
                        handleUpdateProjet(projet);
                    }
                });

                // Handle the "Delete" action
                deleteButton.setOnAction(event -> {
                    Projet projet = getTableView().getItems().get(getIndex());
                    if (projet != null) {
                        handleDeleteProjet(projet);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null); // Remove the buttons if the cell is empty
                } else {
                    setGraphic(buttons); // Reassign the buttons if the cell is not empty
                }
            }
        });
    }

    private void handleUpdateProjet(Projet projet) {
        // Populate the input fields with the selected project's data
        titleField.setText(projet.getTitre());
        descriptionField.setText(projet.getDescription());

        // Show the update button and cancel button, hide the add button
        addProjetButton.setVisible(false);
        updateProjetButton.setVisible(true);
        cancelUpdateButton.setVisible(true);

        // Set the action for the update button
        updateProjetButton.setOnAction(event -> {
            try {
                String titre = titleField.getText();
                String description = descriptionField.getText();

                // Validate required fields
                if (titre.isEmpty() || description.isEmpty()) {
                    showError("Veuillez remplir tous les champs obligatoires !");
                    return;
                }

                // Update the project
                projet.setTitre(titre);
                projet.setDescription(description);

                ProjetDAO.updateProjet(projet);
                loadProjets(); // Reload the projects
                projetTable.refresh(); // Refresh the TableView to reapply the cell factory
                resetToAddMode();
                showInfo("Projet mis à jour avec succes !");
            } catch (SQLException e) {
                showError("Erreur lors de la mise à jour du projet : " + e.getMessage());
            }
        });
    }

    private void handleDeleteProjet(Projet projet) {
        try {
            // Delete the project
            ProjetDAO.deleteProjet(projet.getId());

            // Reload the projects and refresh the table
            loadProjets();
            projetTable.refresh();
            showInfo("Projet supprime avec succes !");
        } catch (SQLException e) {
            showError("Erreur lors de la suppression du projet : " + e.getMessage());
        }
    }

    private void updateProjet() {
        // This method is intentionally left empty because the action is set dynamically in handleUpdateProjet.
    }

    private void cancelUpdateMode() {
        resetToAddMode();
    }

    private void resetToAddMode() {
        clearFields();
        addProjetButton.setVisible(true);
        updateProjetButton.setVisible(false);
        cancelUpdateButton.setVisible(false);
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
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
        // Clear all filter fields
        filterIdField.clear();
        filterTitleField.clear();

        // Reset the filtered list
        filteredProjets.setPredicate(p -> true);
    }

    private void applyFilters() {
        filteredProjets.setPredicate(projet -> {
            // Filter by ID
            if (!filterIdField.getText().isEmpty() && !projet.getId().toLowerCase().contains(filterIdField.getText().toLowerCase())) {
                return false;
            }

            // Filter by Title
            if (!filterTitleField.getText().isEmpty() && !projet.getTitre().toLowerCase().contains(filterTitleField.getText().toLowerCase())) {
                return false;
            }

            return true; // Include the project if all filters pass
        });
    }
}