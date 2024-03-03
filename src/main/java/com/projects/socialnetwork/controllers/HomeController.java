package com.projects.socialnetwork.controllers;

import com.projects.socialnetwork.DTO.UserFriendDTO;
import com.projects.socialnetwork.models.Friendship;
import com.projects.socialnetwork.models.Message;
import com.projects.socialnetwork.models.User;
import com.projects.socialnetwork.repositories.paging.Page;
import com.projects.socialnetwork.repositories.paging.Pageable;
import com.projects.socialnetwork.repositories.paging.PageableImplementation;
import com.projects.socialnetwork.services.NetworkService;
import com.projects.socialnetwork.utils.observers.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HomeController implements Observer {
    public Label welcomeUserLabel;
    public TableView<UserFriendDTO> tableViewFriends;
    public TableView<User> tableViewAllUsers;
    public TableColumn<User, String> tableColumnUsername;
    public TableColumn<User, String> tableColumnEmail;
    public TableColumn<User, String> tableColumnFirstName;
    public TableColumn<User, String> tableColumnLastName;
    public TableColumn<UserFriendDTO, String> friendTableColumnUsername;
    public TableColumn<UserFriendDTO, String> friendTableColumnEmail;
    public TableColumn<UserFriendDTO, String> tableColumnFriendsSince;
    public ListView<Friendship> pendingRequestListView;
    public ListView<UserFriendDTO> messageFriendsListView;
    public ListView<Message> messagesListView;
    public TextField messageTextField;
    public Label pageNumberLabel;
    public ComboBox<Integer> pageSizeComboBox;
    public Label labelFriendsPageNumber;
    public ComboBox<Integer> pageSizeComboBoxFriends;

    private NetworkService service;

    private int currentPageNumber;

    private int currentPageNumberFriends;
    private int currentPageSize;
    private int currentPageSizeFriends;

    private User loggedInUser;

    private User selectedUser;
    private UserFriendDTO lastSelectedFriend;

    private ObservableList<User> allUsersModel = FXCollections.observableArrayList();

    private ObservableList<UserFriendDTO> friendsModel = FXCollections.observableArrayList();

    private ObservableList<Friendship> recievedFriendRequestModel = FXCollections.observableArrayList();


    public void setService(NetworkService service) {
        this.service = service;
        service.addObserver(this);
        initModel();
    }

    private void initModel() {
        Pageable pageable = new PageableImplementation(currentPageNumber, currentPageSize);
        Page<User> userPage = service.getAllUsersPaged(pageable);
        List<User> userList = StreamSupport.stream(userPage.getContent().spliterator(), false).toList();
        allUsersModel.setAll(userList);

        Pageable pageable1 = new PageableImplementation(currentPageNumberFriends, currentPageSizeFriends);
        Page<UserFriendDTO> userFriendDTOPage = service.getFriendsOfUserPaged(loggedInUser.getId(), pageable1);
        List<UserFriendDTO> userFriendDTOList = StreamSupport.stream(userFriendDTOPage.getContent().spliterator(), false).toList();
        friendsModel.setAll(userFriendDTOList);



        Iterable<Friendship> friendRequests = service.getPendingFriendRequests(loggedInUser.getId());
        List<Friendship> friendRequestList = StreamSupport.stream(friendRequests.spliterator(), false).toList();
        recievedFriendRequestModel.setAll(friendRequestList);


    }

    @FXML
    private void initialize() {
        initializeComboBox();
        initializeTableColumns();
        initializeTableViewItems();
        initializeListViewItems();
        initializeMessageFriendsListViewListener();
        initializeMessagesListViewCellFactory();
    }

    private void initializeComboBox() {
        pageSizeComboBox.getItems().addAll(2, 5, 10, 15);
        currentPageNumber = 1;
        currentPageSize = 5;
        pageSizeComboBox.getSelectionModel().select(1);

        pageNumberLabel.setText("1");

        pageSizeComboBoxFriends.getItems().addAll(2, 5, 10, 15);
        currentPageNumberFriends = 1;
        currentPageSizeFriends = 2;
        pageSizeComboBoxFriends.getSelectionModel().select(0);

        labelFriendsPageNumber.setText("1");
    }


    private void initializeTableColumns() {
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        friendTableColumnUsername.setCellValueFactory(new PropertyValueFactory<UserFriendDTO, String>("username"));
        friendTableColumnEmail.setCellValueFactory(new PropertyValueFactory<UserFriendDTO, String>("email"));
        tableColumnFriendsSince.setCellValueFactory(new PropertyValueFactory<UserFriendDTO, String>("friendsSince"));
    }

    private void initializeTableViewItems() {
        tableViewAllUsers.setItems(allUsersModel);
        tableViewFriends.setItems(friendsModel);
    }

    private void initializeListViewItems() {
        messageFriendsListView.setItems(friendsModel);
        pendingRequestListView.setItems(recievedFriendRequestModel);
    }

    private void initializeMessageFriendsListViewListener() {
        messageFriendsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lastSelectedFriend = newValue;
                populateMessagesListView(newValue);
            }
        });
    }

    private void initializeMessagesListViewCellFactory() {
        messagesListView.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String messageFormatText = item.getFrom().getUsername() + ": " + item.getMessage();
                    setText(messageFormatText);
                    if (item.getFrom().equals(loggedInUser)) {
                        setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        setAlignment(Pos.CENTER_LEFT);
                    }
                }
            }
        });
        //Go to the last message
        messagesListView.scrollTo(messagesListView.getItems().size() - 1);
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        welcomeUserLabel.setText("Welcome " + loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
    }

    public void handleSendRequest(ActionEvent event) {
        try {
            service.sendFriendRequest(loggedInUser.getId(), selectedUser.getId());

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void handleUserSelection(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 1) {
            selectedUser = tableViewAllUsers.getSelectionModel().getSelectedItem();
        }
    }

    public void handleLogOut(ActionEvent event) throws IOException {
        Stage homeStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        service.removeObserver(this);
        homeStage.close();

    }


    @FXML
    public void handleAcceptRequest(ActionEvent event) {
        User sender = pendingRequestListView.getSelectionModel().getSelectedItem().getUser1();
        User receiver = pendingRequestListView.getSelectionModel().getSelectedItem().getUser2();

        try {
            service.acceptFriendRequest(sender.getId(), receiver.getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Friend request accepted!");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        initModel();
    }

    @FXML
    public void handleRejectRequest(ActionEvent event) {
        User sender = pendingRequestListView.getSelectionModel().getSelectedItem().getUser1();
        User receiver = pendingRequestListView.getSelectionModel().getSelectedItem().getUser2();

        try {
            service.declineFriendRequest(sender.getId(), receiver.getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Friend request declined!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        initModel();
    }

    private void populateMessagesListView(UserFriendDTO selectedFriend) {
        User friendUser = service.getUserByUsername(selectedFriend.getUsername());
        Iterable<Message> messages = service.getMessagesBetweenUsers(loggedInUser, friendUser);
        List<Message> messagesList = StreamSupport.stream(messages.spliterator(), false).sorted(Comparator.comparing(Message::getSentAt)).collect(Collectors.toList());
        ObservableList<Message> messagesModel = FXCollections.observableArrayList(messagesList);
        messagesListView.setItems(messagesModel);
        messagesListView.scrollTo(messagesListView.getItems().size() - 1);

    }

    public void handleDeleteFriend(ActionEvent event) {
        User sender = service.getUserByUsername(tableViewFriends.getSelectionModel().getSelectedItem().getUsername());
        User receiver = loggedInUser;

        try {
            service.declineFriendRequest(sender.getId(), receiver.getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Friend deleted!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        initModel();
    }


    public void handleSendSingleMessage(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            String message = messageTextField.getText();
            User sender = loggedInUser;
            User receiver = service.getUserByUsername(lastSelectedFriend.getUsername());

            service.sendOneToOneMessage(sender, receiver, message);

            populateMessagesListView(lastSelectedFriend);
            messageTextField.clear();

        }
    }

    public void handleGoToMessageAll(ActionEvent event) {
        try {
            FXMLLoader messageAllLoader = new FXMLLoader();
            messageAllLoader.setLocation(getClass().getResource("/com/projects/socialnetwork/views/message-all-view.fxml"));

            AnchorPane messageAllLayout = messageAllLoader.load();
            Scene scene = new Scene(messageAllLayout);

            MessageAllController messageAllController = messageAllLoader.getController();
            messageAllController.setControllerSettings(service, loggedInUser);

            Stage messageAllStage = new Stage();
            messageAllStage.setScene(scene);
            messageAllStage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void update() {
        initModel();

        if (lastSelectedFriend != null) {
            populateMessagesListView(lastSelectedFriend);
        }
        initializeMessagesListViewCellFactory();
    }

    public void handlePreviousPage(ActionEvent actionEvent) {
        if (currentPageNumber > 1) {
            currentPageNumber--;
            pageNumberLabel.setText(String.valueOf(currentPageNumber));
            initModel();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You are on the first page!");
            alert.showAndWait();
        }
    }

    public void handleNextPage(ActionEvent actionEvent) {
        if (service.getAllUsersPaged(new PageableImplementation(currentPageNumber + 1, currentPageSize)).getContent().count() > 0) {
            currentPageNumber++;
            pageNumberLabel.setText(String.valueOf(currentPageNumber));
            initModel();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You are on the last page!");
            alert.showAndWait();
        }
        initModel();
    }

    public void ChangeCurrentPageSize(ActionEvent actionEvent) {
        currentPageSize = pageSizeComboBox.getSelectionModel().getSelectedItem();
        System.out.println(currentPageSize);
        System.out.println(currentPageNumber);
        initModel();
    }

    public void handleNextPageFriends(ActionEvent actionEvent) {
        if (service.getFriendsOfUserPaged(loggedInUser.getId(), new PageableImplementation(currentPageNumberFriends + 1, currentPageSizeFriends)).getContent().count() > 0) {
            currentPageNumberFriends++;
            labelFriendsPageNumber.setText(String.valueOf(currentPageNumberFriends));
            initModel();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You are on the last page!");
            alert.showAndWait();
        }
        initModel();
    }

    public void handlePreviousPageFriends(ActionEvent actionEvent) {
        if (currentPageNumberFriends > 1) {
            currentPageNumberFriends--;
            labelFriendsPageNumber.setText(String.valueOf(currentPageNumberFriends));
            initModel();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You are on the first page!");
            alert.showAndWait();
        }
    }

    public void handleChangePageSizeFriends(ActionEvent actionEvent) {
        currentPageSizeFriends = pageSizeComboBoxFriends.getSelectionModel().getSelectedItem();
        System.out.println(currentPageSizeFriends);
        System.out.println(currentPageNumberFriends);
        initModel();
    }
}
