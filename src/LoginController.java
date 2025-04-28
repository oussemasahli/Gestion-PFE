import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField inputField; // Can be email or name

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String input = inputField.getText();
        String password = passwordField.getText();

        if (DatabaseService.authenticateUser(input, password)) {
            System.out.println("Login successful!");

            try {
                // Load the main.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
                Parent root = loader.load();

                // Pass the username to the MainController
                MainController mainController = loader.getController();

                // Switch to the main scene
                Stage stage = (Stage) inputField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Main Screen");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid email/name or password.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleSignUpNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sign_up.fxml"));
            Parent signUpRoot = loader.load();

            Stage stage = (Stage) inputField.getScene().getWindow();
            stage.setScene(new Scene(signUpRoot));
            stage.setTitle("Creer un compte");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger la page d'inscription.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

