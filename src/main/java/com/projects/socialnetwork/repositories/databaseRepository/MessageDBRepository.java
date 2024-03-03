package com.projects.socialnetwork.repositories.databaseRepository;

import com.projects.socialnetwork.models.Message;
import com.projects.socialnetwork.models.User;
import com.projects.socialnetwork.repositories.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDBRepository implements Repository<UUID, Message> {

    protected final String url;
    protected final String username;
    protected final String password;

    protected final UserDBRepository userRepository;

    public MessageDBRepository(String url, String username, String password, UserDBRepository userRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Message> getById(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE id = ?");
            statement.setObject(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            Message message = null;
            String messageText = null;
            if (resultSet.next()) {
                messageText = resultSet.getString("message");
            }
            PreparedStatement getUsersStatement = connection.prepareStatement("SELECT * FROM sent_messages WHERE message_id = ?");
            getUsersStatement.setObject(1, uuid);
            ResultSet usersResultSet = getUsersStatement.executeQuery();
            List<User> to = new ArrayList<>();
            User from = null;
            LocalDateTime sentAt = null;
            while (usersResultSet.next()) {
                UUID from_id = UUID.fromString(usersResultSet.getString("from_id"));
                UUID to_id = UUID.fromString(usersResultSet.getString("to_id"));
                sentAt = usersResultSet.getTimestamp("sent_at").toLocalDateTime();
                from = userRepository.getById(from_id).get();
                to.add(userRepository.getById(to_id).get());
            }
            message = new Message(from, to, messageText, sentAt);
            message.setId(uuid);
            return Optional.of(message);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Message> getAll() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages");
            ResultSet resultSet = statement.executeQuery();
            List<Message> messages = new ArrayList<>();

            while (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String messageText = resultSet.getString("message");

                PreparedStatement getUsersStatement = connection.prepareStatement("SELECT * FROM sent_messages WHERE message_id = ?");
                getUsersStatement.setObject(1, id);
                ResultSet usersResultSet = getUsersStatement.executeQuery();
                List<User> to = new ArrayList<>();
                User from = null;
                LocalDateTime sentAt = null;
                while (usersResultSet.next()) {
                    UUID from_id = UUID.fromString(usersResultSet.getString("from_id"));
                    UUID to_id = UUID.fromString(usersResultSet.getString("to_id"));
                    sentAt = usersResultSet.getTimestamp("sent_at").toLocalDateTime();
                    from = userRepository.getById(from_id).get();
                    to.add(userRepository.getById(to_id).get());
                }
                Message message = new Message(from, to, messageText, sentAt);
                message.setId(id);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> save(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO messages VALUES (?, ?)");
            UUID id = UUID.randomUUID();
            statement.setObject(1, id);
            statement.setString(2, entity.getMessage());
            statement.executeUpdate();

            PreparedStatement sentMessagesStatement = connection.prepareStatement("INSERT INTO sent_messages VALUES (?,?, ?, ?, ?)");
            for (User user : entity.getTo()) {
                UUID sentMessageId = UUID.randomUUID();
                sentMessagesStatement.setObject(1, sentMessageId);
                sentMessagesStatement.setObject(2, id);
                sentMessagesStatement.setObject(3, entity.getFrom().getId());
                sentMessagesStatement.setObject(4, user.getId());
                sentMessagesStatement.setTimestamp(5, Timestamp.valueOf(entity.getSentAt()));
                sentMessagesStatement.executeUpdate();
            }

            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM messages WHERE id = ?");
            statement.setObject(1, uuid);
            statement.executeUpdate();
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("UPDATE messages SET message = ? WHERE id = ?");
            statement.setString(1, entity.getMessage());
            statement.setObject(2, entity.getId());
            statement.executeUpdate();
            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
