package com.projects.socialnetwork;

import com.projects.socialnetwork.controllers.LoginController;
import com.projects.socialnetwork.models.Message;
import com.projects.socialnetwork.models.User;
import com.projects.socialnetwork.repositories.databaseRepository.FriendshipDBRepository;
import com.projects.socialnetwork.repositories.databaseRepository.MessageDBRepository;
import com.projects.socialnetwork.repositories.databaseRepository.UserDBRepository;
import com.projects.socialnetwork.repositories.pagingRepository.FriendshipDBPagingRepository;
import com.projects.socialnetwork.repositories.pagingRepository.UserDBPagingRepository;
import com.projects.socialnetwork.services.NetworkService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class MainApplication extends Application {
    private NetworkService service;

    public static void main(String[] args) {

        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {


        String url = "jdbc:postgresql://localhost:5435/socialnetwork";
        String username = "postgres";
        String password = "password";
        UserDBRepository userDBRepository = new UserDBRepository(url, username, password);

        MessageDBRepository messageDBRepository = new MessageDBRepository(url, username, password, userDBRepository);

        UserDBPagingRepository userDBPagingRepository = new UserDBPagingRepository(url, username, password);
        FriendshipDBPagingRepository friendshipDBPagingRepository = new FriendshipDBPagingRepository(url, username, password, userDBPagingRepository);

        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(url, username, password, userDBRepository);
        service = new NetworkService(userDBRepository, friendshipDBRepository, messageDBRepository, userDBPagingRepository, friendshipDBPagingRepository);
        initView(stage);
        stage.show();
    }

    private void initView(Stage stage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("views/login-view.fxml"));
        AnchorPane loginLayout = loginLoader.load();
        stage.setScene(new Scene(loginLayout));

        LoginController loginController = loginLoader.getController();
        loginController.setService(service);
    }
}