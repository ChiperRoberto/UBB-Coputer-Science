package com.projects.socialnetwork.repositories.pagingRepository;

import com.projects.socialnetwork.DTO.UserFriendDTO;
import com.projects.socialnetwork.ENUM.FriendshipRequestStatus;
import com.projects.socialnetwork.models.Friendship;
import com.projects.socialnetwork.models.User;
import com.projects.socialnetwork.repositories.databaseRepository.FriendshipDBRepository;
import com.projects.socialnetwork.repositories.databaseRepository.UserDBRepository;
import com.projects.socialnetwork.repositories.paging.Page;
import com.projects.socialnetwork.repositories.paging.PageImplementation;
import com.projects.socialnetwork.repositories.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendshipDBPagingRepository extends FriendshipDBRepository implements PagingRepository<UUID, Friendship> {
    public FriendshipDBPagingRepository(String url, String username, String password, UserDBRepository userRepository) {
        super(url, username, password, userRepository);
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        try (Connection connection = DriverManager.getConnection(sqlurl, sqlusername, sqlpassword)) {
            PreparedStatement statement = connection.prepareStatement("select * from friendships limit ? offset ? ");
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, (pageable.getPageNumber() - 1) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            List<Friendship> friendships = new ArrayList<>();
            while (resultSet.next()) {

                UUID user1Id = (UUID) resultSet.getObject("user1_id");
                UUID user2Id = (UUID) resultSet.getObject("user2_id");
                FriendshipRequestStatus status = FriendshipRequestStatus.valueOf(resultSet.getString("friendship_status").toUpperCase());
                LocalDateTime friendsSince = resultSet.getTimestamp("friends_since").toLocalDateTime();

                Friendship friendship = new Friendship(userRepository.getById(user1Id).get(), userRepository.getById(user2Id).get(), friendsSince, status);
                friendship.setId((UUID) resultSet.getObject("id"));

            }
            return new PageImplementation<>(pageable, friendships.stream());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<UserFriendDTO> getFriendsOfUser(UUID userId, Pageable pageable) {
        try (Connection connection = DriverManager.getConnection(sqlurl, sqlusername, sqlpassword)) {
            PreparedStatement statement = connection.prepareStatement("select * from friendships where ((user1_id = ? or user2_id = ?) and friendship_status='ACCEPTED') limit ? offset ? ");
            statement.setObject(1, userId);
            statement.setObject(2, userId);
            statement.setInt(3, pageable.getPageSize());
            statement.setInt(4, (pageable.getPageNumber() - 1) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            List<UserFriendDTO> friends = new ArrayList<>();
            while (resultSet.next()) {
                UUID user1Id = (UUID) resultSet.getObject("user1_id");
                UUID user2Id = (UUID) resultSet.getObject("user2_id");
                FriendshipRequestStatus status = FriendshipRequestStatus.valueOf(resultSet.getString("friendship_status").toUpperCase());
                LocalDateTime friendsSince = resultSet.getTimestamp("friends_since").toLocalDateTime();

                User user1 = userRepository.getById(user1Id).get();
                User user2 = userRepository.getById(user2Id).get();

                if (user1.getId().equals(userId)) {
                    UserFriendDTO userFriendDTO = new UserFriendDTO(user2.getUsername(),user2.getEmail(),friendsSince);
                    friends.add(userFriendDTO);
                } else {
                    UserFriendDTO userFriendDTO = new UserFriendDTO(user1.getUsername(),user1.getEmail(),friendsSince);
                    friends.add(userFriendDTO);
                }

            }
            return new PageImplementation<>(pageable, friends.stream());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
