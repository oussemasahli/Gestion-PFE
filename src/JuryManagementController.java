import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JuryManagementController {

    @FXML
    private TableView<Jury> juryTable;

    @FXML
    private TableColumn<Jury, String> juryIdColumn, juryNameColumn;

    @FXML
    private TextField juryIdField, juryNameField;

    @FXML
    private TableView<Encadrant> memberTable;

    @FXML
    private TableColumn<Encadrant, String> memberIdColumn, memberNameColumn, memberRoleColumn; // Added memberRoleColumn

    @FXML
    private TextField encadrantIdField; // TextField for Encadrant ID

    @FXML
    private ComboBox<String> roleComboBox; // ComboBox for selecting the role

    @FXML
    private ComboBox<Encadrant> encadrantComboBox; // ComboBox for selecting Encadrant

    private ObservableList<Jury> juryList = FXCollections.observableArrayList();
    private ObservableList<Encadrant> memberList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind the columns to the Jury properties
        juryIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        juryNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        // Bind the columns to the Encadrant properties
        memberIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        memberNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        memberRoleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole())); // Bind role

        // Load juries into the table
        loadJuries();

        // Load encadrants into the ComboBox
        loadEncadrants();

        // Populate the roleComboBox with roles
        roleComboBox.setItems(FXCollections.observableArrayList("President", "Examinateur", "Rapporteur"));
    }

    private void loadJuries() {
        try {
            List<Jury> juries = JuryDAO.getAllJuries();
            juryList.clear();
            juryList.addAll(juries);
            juryTable.setItems(juryList);

           
        } catch (SQLException e) {
            showError("Error", "Failed to load juries: " + e.getMessage());
        }
    }

    private void loadEncadrants() {
        try {
            List<Encadrant> encadrants = EncadrantDAO.getAllEncadrants(); // Fetch all encadrants
            ObservableList<Encadrant> encadrantList = FXCollections.observableArrayList(encadrants);

            // Set the items in the ComboBox
            encadrantComboBox.setItems(encadrantList);

            // Use a custom cell factory to display the name of the Encadrant
            encadrantComboBox.setCellFactory(comboBox -> new ListCell<Encadrant>() {
                @Override
                protected void updateItem(Encadrant encadrant, boolean empty) {
                    super.updateItem(encadrant, empty);
                    if (empty || encadrant == null) {
                        setText(null);
                    } else {
                        setText(encadrant.getNom()); // Display the name of the Encadrant
                    }
                }
            });

            // Set the displayed value in the ComboBox
            encadrantComboBox.setButtonCell(new ListCell<Encadrant>() {
                @Override
                protected void updateItem(Encadrant encadrant, boolean empty) {
                    super.updateItem(encadrant, empty);
                    if (empty || encadrant == null) {
                        setText(null);
                    } else {
                        setText(encadrant.getNom()); // Display the name of the Encadrant
                    }
                }
            });
        } catch (SQLException e) {
            showError("Error", "Failed to load encadrants: " + e.getMessage());
        }
    }

    private void populateFields(Jury jury) {
        if (jury != null) {
            juryIdField.setText(jury.getId());
            juryNameField.setText(jury.getName());
        } else {
            clearFields();
        }
    }

    private void clearFields() {
        juryIdField.clear();
        juryNameField.clear();
    }

    @FXML
    private void handleAddJury() {
        try {
            String juryId = juryIdField.getText().trim();
            String juryName = juryNameField.getText().trim();

            if (juryId.isEmpty() || juryName.isEmpty()) {
                showError("Invalid Input", "All fields are required.");
                return;
            }

            Jury jury = new Jury(juryId, juryName, FXCollections.observableArrayList());
            jury.saveToDatabase();
            loadJuries();
            showSuccess("Jury added successfully!");
            clearFields();
        } catch (SQLException e) {
            showError("Error", "Failed to add jury: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateJury() {
        try {
            Jury selectedJury = juryTable.getSelectionModel().getSelectedItem();
            if (selectedJury == null) {
                showError("No Selection", "Please select a jury to update.");
                return;
            }

            String juryName = juryNameField.getText().trim();
            if (juryName.isEmpty()) {
                showError("Invalid Input", "Jury name is required.");
                return;
            }

            // Update the selected jury's name
            selectedJury.setName(juryName);

            // Call the update method in JuryDAO
            JuryDAO.updateJury(selectedJury);

            // Reload the table to reflect changes
            loadJuries();
            showSuccess("Jury updated successfully!");
        } catch (SQLException e) {
            showError("Error", "Failed to update jury: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewMembers() {
        try {
            Jury selectedJury = juryTable.getSelectionModel().getSelectedItem();
            if (selectedJury == null) {
                showError("No Selection", "Please select a jury to view members.");
                return;
            }

            // Retrieve members of the selected jury
            List<Encadrant> members = JuryDAO.getJuryMembers(selectedJury.getId());
            memberList.setAll(members); // Update the memberList

            // Refresh the table
            memberTable.setItems(memberList);
        } catch (SQLException e) {
            showError("Error", "Failed to load jury members: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoveMember() {
        try {
            Encadrant selectedMember = memberTable.getSelectionModel().getSelectedItem();
            if (selectedMember == null) {
                showError("No Selection", "Please select a member to remove.");
                return;
            }

            Jury selectedJury = juryTable.getSelectionModel().getSelectedItem();
            if (selectedJury == null) {
                showError("No Jury Selected", "Please select a jury first.");
                return;
            }

            JuryDAO.removeJuryMember(selectedJury.getId(), selectedMember.getId());
            memberList.remove(selectedMember);
            showSuccess("Member removed successfully!");
        } catch (SQLException e) {
            showError("Error", "Failed to remove member: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddMember() {
        try {
            Jury selectedJury = juryTable.getSelectionModel().getSelectedItem();
            if (selectedJury == null) {
                showError("No Selection", "Please select a jury to add members.");
                return;
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Member");
            dialog.setHeaderText("Add a Member to Jury " + selectedJury.getName());
            dialog.setContentText("Enter Encadrant ID:");

            // Get the encadrant ID from the user
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String encadrantId = result.get().trim();

                // Check if the encadrant exists
                if (EncadrantDAO.getEncadrantById(encadrantId) == null) {
                    showError("Invalid Encadrant", "The encadrant ID does not exist.");
                    return;
                }

                // Prompt for the role
                ChoiceDialog<String> roleDialog = new ChoiceDialog<>("Examinateur", "President", "Rapporteur");
                roleDialog.setTitle("Select Role");
                roleDialog.setHeaderText("Assign a Role to the Jury Member");
                roleDialog.setContentText("Choose a role:");
                Optional<String> roleResult = roleDialog.showAndWait();

                if (roleResult.isPresent()) {
                    String role = roleResult.get();

                    // Add the member to the jury with the selected role
                    JuryDAO.addJuryMember(selectedJury.getId(), encadrantId, role);
                    showSuccess("Member added successfully as " + role + "!");
                }
            }
        } catch (SQLException e) {
            showError("Error", "Failed to add member: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddMemberFromFields() {
        try {
            Jury selectedJury = juryTable.getSelectionModel().getSelectedItem();
            if (selectedJury == null) {
                showError("No Selection", "Please select a jury to add members.");
                return;
            }

            Encadrant selectedEncadrant = encadrantComboBox.getValue();
            if (selectedEncadrant == null) {
                showError("Invalid Input", "Please select an encadrant.");
                return;
            }

            String role = roleComboBox.getValue();
            if (role == null || role.isEmpty()) {
                showError("Invalid Input", "Please select a role for the jury member.");
                return;
            }

            // Add the member to the jury with the selected role
            JuryDAO.addJuryMember(selectedJury.getId(), selectedEncadrant.getId(), role);

            // Reload the members table
            handleViewMembers();

            // Clear the input fields
            encadrantComboBox.setValue(null);
            roleComboBox.setValue(null);

            showSuccess("Member added successfully as " + role + "!");
        } catch (SQLException e) {
            showError("Error", "Failed to add member: " + e.getMessage());
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