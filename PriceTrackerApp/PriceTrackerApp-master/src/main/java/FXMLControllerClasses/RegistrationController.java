package FXMLControllerClasses;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationController {

    @FXML
    private TextField confirmPasswordTextfield;

    @FXML
    private TextField emailTextfield;

    @FXML
    private TextField nameTextfield;

    @FXML
    private TextField passwordTextfield;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private Button registerButton;

    @FXML
    private TextField userNameTextfield;

    @FXML
    void onRegister(ActionEvent event) {
        String username = userNameTextfield.getText().trim();
        String password = passwordTextfield.getText().trim();
        String confirmPassword = confirmPasswordTextfield.getText().trim();
        String name = nameTextfield.getText().trim();
        String email = emailTextfield.getText().trim();
        String phoneNumber = phoneNumberTextField.getText().trim();

        // Check if any field is empty
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            showAlert("Registration Error", "All fields are required");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showAlert("Registration Error", "Passwords do not match");
            return;
        }

        // Perform database registration
        if (registerUser(username, password, name, email, phoneNumber)) {
            showAlert("Registration Successful", "User registered successfully");
            openPage("/FXML/LoginFXML.fxml", registerButton);
        } else {
            showAlert("Registration Error", "Username already exists or an error occurred");
        }
    }

    private boolean registerUser(String username, String password, String name, String email, String phoneNumber) {
        // Use try-with-resources to ensure the resources (Connection, PreparedStatement) are closed
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "123456")) {

            // Check if the username already exists
            if (checkUsernameExists(connection, username)) {
                return false; // Username already exists
            }

            // Insert user information into the database
            String query = "INSERT INTO user_info (user_name, user_password, name, email, phone_number) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, name);
                preparedStatement.setString(4, email);
                preparedStatement.setString(5, phoneNumber);

                // Execute the update
                int rowsAffected = preparedStatement.executeUpdate();

                // Return true if at least one row is affected, indicating successful registration
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkUsernameExists(Connection connection, String username) throws SQLException {
        String query = "SELECT * FROM user_info WHERE user_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (var resultSet = preparedStatement.executeQuery()) {
                // Return true if the result set has any rows, indicating that the username already exists
                return resultSet.next();
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void openPage(String url, Button button) {
        try {
            // Load the FXML file for the main page
            FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
            Parent root = loader.load();

            // Create a new scene with the main page content
            Scene scene = new Scene(root);

            // Get the current stage and set its scene to the new scene
            Stage stage = (Stage) button.getScene().getWindow();
            stage.setScene(scene);

            // Show the main page
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
