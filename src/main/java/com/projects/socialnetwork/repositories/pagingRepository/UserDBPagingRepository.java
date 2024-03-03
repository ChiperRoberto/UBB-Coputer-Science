package com.projects.socialnetwork.repositories.pagingRepository;

import com.projects.socialnetwork.models.User;
import com.projects.socialnetwork.repositories.databaseRepository.UserDBRepository;
import com.projects.socialnetwork.repositories.paging.Page;
import com.projects.socialnetwork.repositories.paging.PageImplementation;
import com.projects.socialnetwork.repositories.paging.Pageable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDBPagingRepository extends UserDBRepository implements PagingRepository<UUID, User> {

    public UserDBPagingRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        try (Connection connection = DriverManager.getConnection(sqlurl, sqlUsername, sqlPassword)) {
            PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ? ");
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, (pageable.getPageNumber()-1) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                User user = getUserFromResultSet(resultSet);
                users.add(user);
            }
            return new PageImplementation<>(pageable, users.stream());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
