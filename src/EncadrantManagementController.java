import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class EncadrantManagementController {

    @FXML
    private TableView<Encadrant> encadrantTable;

    @FXML
    private TableColumn<Encadrant, String> idColumn, nameColumn;

    @FXML
    private TableColumn<Encadrant, Integer> availabilityColumn, chargeColumn;

    @FXML
    private TextField filterField, idField, nameField, availabilityField, chargeField;

    private ObservableList<Encadrant> encadrantList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind columns to Encadrant properties
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        availabilityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDisponibilite()));
        chargeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getChargeEncadrement()));

        // Load encadrants into the table
        loadEncadrants();

        // Add listener to populate fields when an encadrant is selected
        encadrantTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populateFields(newValue));

        // Add listener to filterField for dynamic filtering
        filterField.textProperty().addListener((observable, oldValue, newValue) -> filterEncadrants(newValue));
    }

    private void loadEncadrants() {
        try {
            List<Encadrant> encadrants = EncadrantDAO.getAllEncadrants();
            encadrantList.clear();
            encadrantList.addAll(encadrants);
            encadrantTable.setItems(encadrantList);
        } catch (SQLException e) {
            showError("Error", "Failed to load encadrants: " + e.getMessage());
        }
    }

    private void populateFields(Encadrant encadrant) {
        if (encadrant != null) {
            idField.setText(encadrant.getId());
            nameField.setText(encadrant.getNom());
            availabilityField.setText(String.valueOf(encadrant.getDisponibilite()));
            chargeField.setText(String.valueOf(encadrant.getChargeEncadrement()));
        } else {
            clearFields();
        }
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        availabilityField.clear();
        chargeField.clear();
    }

    @FXML
    private void handleFilter() {
        String filterText = filterField.getText().trim().toLowerCase();
        if (filterText.isEmpty()) {
            encadrantTable.setItems(encadrantList);
        } else {
            List<Encadrant> filteredList = encadrantList.stream()
                    .filter(e -> e.getNom().toLowerCase().contains(filterText))
                    .collect(Collectors.toList());
            encadrantTable.setItems(FXCollections.observableArrayList(filteredList));
        }
    }

    private void filterEncadrants(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            encadrantTable.setItems(encadrantList); // Show all encadrants if filter is empty
        } else {
            String lowerCaseFilter = filterText.toLowerCase();
            List<Encadrant> filteredList = encadrantList.stream()
                    .filter(e -> e.getNom().toLowerCase().contains(lowerCaseFilter))
                    .collect(Collectors.toList());
            encadrantTable.setItems(FXCollections.observableArrayList(filteredList));
        }
    }

    @FXML
    private void handleAdd() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            int availability = Integer.parseInt(availabilityField.getText().trim());
            int charge = Integer.parseInt(chargeField.getText().trim());

            Encadrant encadrant = new Encadrant(id, name, availability, charge);
            encadrant.saveToDatabase();
            loadEncadrants();
            showSuccess("Encadrant added successfully!");
            clearFields();
        } catch (Exception e) {
            showError("Error", "Failed to add encadrant: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        try {
            Encadrant selectedEncadrant = encadrantTable.getSelectionModel().getSelectedItem();
            if (selectedEncadrant == null) {
                showError("No Selection", "Please select an encadrant to update.");
                return;
            }

            String name = nameField.getText().trim();
            int availability = Integer.parseInt(availabilityField.getText().trim());
            int charge = Integer.parseInt(chargeField.getText().trim());

            selectedEncadrant.setNom(name);
            selectedEncadrant.setDisponibilite(availability);
            selectedEncadrant.setChargeEncadrement(charge);
            selectedEncadrant.updateInDatabase();
            loadEncadrants();
            showSuccess("Encadrant updated successfully!");
        } catch (Exception e) {
            showError("Error", "Failed to update encadrant: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemove() {
        try {
            Encadrant selectedEncadrant = encadrantTable.getSelectionModel().getSelectedItem();
            if (selectedEncadrant == null) {
                showError("No Selection", "Please select an encadrant to remove.");
                return;
            }

            EncadrantDAO.deleteEncadrant(selectedEncadrant.getId());
            loadEncadrants();
            showSuccess("Encadrant removed successfully!");
        } catch (SQLException e) {
            showError("Error", "Failed to remove encadrant: " + e.getMessage());
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
}