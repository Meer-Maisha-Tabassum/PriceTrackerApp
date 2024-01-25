package FXMLControllerClasses;

import HelperClasses.SharedDataModel;
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
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private TextField userNameTextfield;

    @FXML
    private TextField userPasswordTextfield;

    private String loginUserName;

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    @FXML
    void onLogin(ActionEvent event) {
        String username = userNameTextfield.getText().trim();
        String password = userPasswordTextfield.getText().trim();

        // Check if any field is empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Username and password are required");
            return;
        }

        // Perform database login
        if (validateUser(username, password)) {
            showAlert("Login Successful", "Welcome, " + username + "!");
            SharedDataModel.getInstance().setLoggedInUsername(username);
            openMainPage("/FXML/MainPage.fxml", loginButton);
        }
        else {
            showAlert("Login Error", "Invalid username or password");
        }
    }

    private boolean validateUser(String username, String password) {
        // Use try-with-resources to ensure the resources (Connection, PreparedStatement, ResultSet) are closed
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "123456")) {
            String query = "SELECT * FROM user_info WHERE user_name = ? AND user_password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Return true if the result set has any rows, indicating successful login
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    void onRegistration(ActionEvent event) {
        openPage("/FXML/RegistrationFXML.fxml", registerButton);
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
    private void openMainPage(String url, Button button) {
        try {
            // Load the FXML file for the main page
            FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
            Parent root = loader.load();

            MainPageController mainPageController = loader.getController();
//            mainPageController.initialize(null, null, loginUserName);

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
