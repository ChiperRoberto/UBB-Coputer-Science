package com.projects.socialnetwork.controllers;

import com.projects.socialnetwork.models.User;
import com.projects.socialnetwork.services.NetworkService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    public TextField usernameTextField;

    public PasswordField passwordTextField;
    public Button loginButton;

    private NetworkService service;

    public void setService(NetworkService service) {
        this.service = service;
    }

    public void handleLogin(ActionEvent event) {
        String loginUsername = usernameTextField.getText();
        String loginPassword = passwordTextField.getText();
        User loggedInUser = null;
        try {
            loggedInUser = service.login(loginUsername, loginPassword);

            loadMainView(loggedInUser, event);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadMainView(User loggedInUser, ActionEvent event) throws IOException {
        FXMLLoader homeLoader = new FXMLLoader();
        homeLoader.setLocation(getClass().getResource("/com/projects/socialnetwork/views/home-view.fxml"));

        Parent homeLayout = homeLoader.load();
        Scene scene = new Scene(homeLayout);

        HomeController homeController = homeLoader.getController();
        homeController.setLoggedInUser(loggedInUser);
        homeController.setService(service);

        Stage homeStage = new Stage();
        homeStage.setScene(scene);
        homeStage.show();

        // Minimize the login window
        Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        loginStage.setIconified(true);
    }

    public void handleGoToSignup(ActionEvent event) {
        try {
            FXMLLoader signupLoader = new FXMLLoader();
            signupLoader.setLocation(getClass().getResource("/com/projects/socialnetwork/views/signup-view.fxml"));
            Stage signupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            AnchorPane signupLayout = signupLoader.load();
            Scene scene = new Scene(signupLayout);
            signupStage.setScene(scene);

            SignupController signupController = signupLoader.getController();
            signupController.setService(service);

            signupStage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
