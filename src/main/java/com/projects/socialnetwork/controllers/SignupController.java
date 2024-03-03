package com.projects.socialnetwork.controllers;

import com.projects.socialnetwork.services.NetworkService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignupController {

    public TextField firstNameTextField;
    public TextField lastNameTextField;
    public TextField usernameTextField;
    public TextField emailTextField;
    public PasswordField passwordTextField;

    private NetworkService service;

    public void setService(NetworkService service) {
        this.service = service;
    }

    public void handleSignup(ActionEvent event) {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        try {
            service.addUser(firstName,lastName,username,email,password);
            goToLogin(event);
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loginLoader = new FXMLLoader();
            loginLoader.setLocation(getClass().getResource("/com/projects/socialnetwork/views/login-view.fxml"));
            loginLoader.load();
            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(loginLoader.getRoot()));
            LoginController loginController = loginLoader.getController();
            loginController.setService(service);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
