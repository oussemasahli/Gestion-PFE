import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController {

    private static final String URL = "jdbc:mysql://localhost:3306/project_db";
    private static final String USER = "root";
    private static final String PASSWORD = "mbccbcmbc123";

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleSignUp() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Erreur", "Tous les champs sont obligatoires.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            addUserToDatabase(name, email, password);
            showSuccess("Succes", "Utilisateur ajoute avec succes !");
            navigateToMain(); // Navigate to main.fxml after successful sign-up
        } catch (SQLException e) {
            showError("Erreur", "Impossible d'ajouter l'utilisateur : " + e.getMessage());
        }
    }

    private void addUserToDatabase(String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // Store the password (consider hashing it for security)
            pstmt.executeUpdate();
        }
    }

    @FXML
    private void handleBack() {
        try {
            // Load the login.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent loginRoot = loader.load();

            // Switch to the login scene
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger la page de connexion.");
        }
    }

    private void navigateToMain() {
        try {
            // Load the main.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Parent mainRoot = loader.load();

            // Switch to the main scene
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(mainRoot));
            stage.setTitle("Main Screen");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger la page principale.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}