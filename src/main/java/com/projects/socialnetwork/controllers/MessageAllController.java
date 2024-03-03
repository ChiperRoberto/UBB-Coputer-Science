package com.projects.socialnetwork.controllers;

import com.projects.socialnetwork.DTO.UserFriendDTO;
import com.projects.socialnetwork.models.User;
import com.projects.socialnetwork.services.NetworkService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public class MessageAllController {
    public TextArea messageAreaText;
    private NetworkService service;
    private User loggedInUser;

    private Set<User> usersToSendMessage = new HashSet<>();
    public ListView<UserFriendDTO> messageAllFriendsListView;
    public TextField toUsersTextField;
    private ObservableList<UserFriendDTO> friendsModel = FXCollections.observableArrayList();

    public void setControllerSettings(NetworkService service, User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.service = service;
        initModel();
    }

    private void initModel() {
        Iterable<UserFriendDTO> friends = service.getFriendsOfUser(loggedInUser.getId());
        List<UserFriendDTO> friendsList = StreamSupport.stream(friends.spliterator(), false)
                .toList();
        friendsModel.setAll(friendsList);

    }

    @FXML
    public void initialize() {
        toUsersTextField.setEditable(false);
        messageAllFriendsListView.setItems(friendsModel);
    }


    public void addUserToSendList(ActionEvent event) {
        try {
            UserFriendDTO selectedFriend = messageAllFriendsListView.getSelectionModel().getSelectedItem();
            if (selectedFriend != null) {
                usersToSendMessage.add(service.getUserByUsername(selectedFriend.getUsername()));

                String usersToSendMessage = this.usersToSendMessage.stream()
                        .map(User::getUsername)
                        .reduce("", (acc, username) -> acc + username + " ");

                toUsersTextField.setText(usersToSendMessage);

            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error adding user to send list");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void handleSendMessageToAllUsers(ActionEvent event) {
        String message = messageAreaText.getText();
        if (message != null && !message.isEmpty()) {
            List<User> usersToSendMessage = new ArrayList<>(this.usersToSendMessage);
            service.sendOneToManyMessage(loggedInUser, usersToSendMessage, message);
            messageAreaText.clear();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message sent");
            alert.setHeaderText("Message sent");
            alert.setContentText("Message sent to all users");
            alert.showAndWait();
        }

    }
}
