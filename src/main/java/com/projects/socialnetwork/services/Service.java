package com.projects.socialnetwork.services;

import com.projects.socialnetwork.exceptions.ValidationException;
import com.projects.socialnetwork.models.Friendship;
import com.projects.socialnetwork.models.User;
import com.projects.socialnetwork.DTO.UserFriendshipDTO;

import java.time.Month;
import java.util.Optional;
import java.util.UUID;

public interface Service {
    /**
     * Adds a user to the repository
     *
     * @param firstName - first name of the user
     * @param lastName  - last name of the user
     * @param username  - username of the user
     * @param email     - email of the user
     * @param password  - password of the user
     * @return null if the user was added successfully, the user that exists otherwise
     * @throws ValidationException if the given user is not valid
     */
    Optional<User> addUser(String firstName, String lastName, String username, String email, String password);


    /**
     * Deletes a user from the repository and all its friendships
     *
     * @param username - username of the user
     * @return the user with the given username,
     * null if the user does not exist
     * @throws IllegalArgumentException if the given username is null
     *
     */
    Optional<User> deleteUserByUsername(String username);

    /**
     * Deletes a user from the repository and all its friendships
     *
     * @param id - id of the user
     * @return the user with the given username,
     * null if the user does not exist
     * @throws IllegalArgumentException if the given username is null
     *
     */
    Optional<User> deleteUserById(UUID id);

    /**
     * Sends a friend request to the given user
     * @param senderID: Id of the user that sent the request
     * @param receiverID: Id of the user that recieved the request
     */
    void sendFriendRequest(UUID senderID, UUID receiverID);

    /**
     * Accepts a friend request from the given user
     * @param senderID: Id of the user that sent the request
     * @param receiverID: Id of the user that recieved the request
     */
    void acceptFriendRequest(UUID senderID, UUID receiverID);

    /**
     * Declines a friend request from the sender user
     * @param senderID: Id of the user that sent the request
     * @param receiverID: Id of the user that recieved the request
     */
    void declineFriendRequest(UUID senderID, UUID receiverID);


    void deleteFriendship(UUID user1ID, UUID user2ID);


    /**
     * Gets all the users
     * @return all the users in the network
     */
    Iterable<User> getAllUsers();

    /**
     * Gets all the friendships
     * @return all the friendships in the network
     */
    Iterable<Friendship> getAllFriendships();

    /**
     * Gets the number of communities(e.g. groups of friends)
     * @return the number of communities
     */
    int getNumberOfCommunities();

    /**
     * Gets all the communities
     * @return all the communities
     */
    Iterable<Iterable<User>> getAllCommunities();

    /**
     * Gets the most sociable community(e.g. group of friends with the most friendships)
     * @return the most sociable community
     */
    Iterable<User> getMostSociableCommunity();

    /**
     * Gets the friends of a user that began their friendship on a given month
     * @param username - username of the user
     * @param month - month
     * @return the friends of a user that began their friendship on a given month
     */
    Iterable<UserFriendshipDTO> friendshipsOfUserOnMonth(String username, Month month);


    /**
        * Updates a user
     * @param id - id of the user
     * @param firstName - first name of the user
     * @param lastName - last name of the user
     * @param username - username of the user
     * @param email - email of the user
     * @param password - password of the user
     * @return null - if the entity is updated,
     *      * otherwise  returns the entity  - (e.g. id does not exist).
     */
    Optional<User> updateUser(UUID id, String firstName, String lastName, String username, String email, String password);

    User login(String username, String password);

}
