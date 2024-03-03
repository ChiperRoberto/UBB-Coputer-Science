package com.projects.socialnetwork.repositories.databaseRepository;

import com.projects.socialnetwork.models.User;
import com.projects.socialnetwork.repositories.Repository;
import com.projects.socialnetwork.validators.UserValidator;
import com.projects.socialnetwork.validators.Validator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserDBRepository implements Repository<UUID, User> {

    protected String sqlurl;
    protected String sqlUsername;
    protected String sqlPassword;

    private Validator<User> validator;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserDBRepository(String sqlurl, String sqlUsername, String sqlPassword) {
        this.sqlurl = sqlurl;
        this.sqlUsername = sqlUsername;
        this.sqlPassword = sqlPassword;
        validator = new UserValidator();
    }

    @Override
    public Optional<User> getById(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(sqlurl, sqlUsername, sqlPassword)) {
            PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");
            statement.setObject(1, uuid, Types.OTHER);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = getUserFromResultSet(resultSet);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString("id"));
        String firstName = resultSet.getString("firstname");
        String lastName = resultSet.getString("lastname");
        String username = resultSet.getString("username");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        return new User(id, firstName, lastName, username, email, password);
    }

    @Override
    public Iterable<User> getAll() {
        try (Connection connection = DriverManager.getConnection(sqlurl, sqlUsername, sqlPassword)) {
            Set<User> users = new HashSet<>();
            PreparedStatement statement = connection.prepareStatement("select * from users");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = getUserFromResultSet(resultSet);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        validator.validate(entity);
        String insertSQL = "insert into users(id, firstname, lastname, email, password, username) values (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(sqlurl, sqlUsername, sqlPassword);
             PreparedStatement statement = connection.prepareStatement(insertSQL);) {
            validator.validate(entity);
            statement.setObject(1, entity.getId(), Types.OTHER);
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getEmail());

            String encodedPassword = passwordEncoder.encode(entity.getPassword());
            statement.setString(5, encodedPassword);
            statement.setString(6, entity.getUsername());
            statement.executeUpdate();
            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(UUID uuid) {
        String deleteSQL = "delete from users where id = ?";
        try (Connection connection = DriverManager.getConnection(sqlurl, sqlUsername, sqlPassword);
             PreparedStatement statement = connection.prepareStatement(deleteSQL);
        ) {
            statement.setObject(1, uuid, Types.OTHER);
            Optional<User> foundUser = getById(uuid);
            int response = 0;
            if (foundUser.isPresent()) {
                response = statement.executeUpdate();
            }
            return response == 0 ? Optional.empty() : foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        String updateSQL = "update users set firstname = ?, lastname = ?, email = ?, password = ?, username = ? where id = ?";
        try (Connection connection = DriverManager.getConnection(sqlurl, sqlUsername, sqlPassword);
             PreparedStatement statement = connection.prepareStatement(updateSQL);
        ) {
            validator.validate(entity);
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());
            statement.setString(5, entity.getUsername());
            statement.setObject(6, entity.getId(), Types.OTHER);
            int response = statement.executeUpdate();
            return response == 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> getUserByUsername(String username) {
        try (Connection connection = DriverManager.getConnection(sqlurl, sqlUsername, sqlPassword)) {
            PreparedStatement statement = connection.prepareStatement("select * from users where username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = getUserFromResultSet(resultSet);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> getUserByEmail(String email) {
        try (Connection connection = DriverManager.getConnection(sqlurl, sqlUsername, sqlPassword)) {
            PreparedStatement statement = connection.prepareStatement("select * from users where email = ?");
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = getUserFromResultSet(resultSet);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
