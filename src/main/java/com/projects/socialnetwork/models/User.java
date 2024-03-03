package com.projects.socialnetwork.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * User  class
 */
public class User extends Entity<UUID> {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private List<User> friends;

    public User(UUID id, String firstName, String lastName, String username, String email, String password) {
        this.setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.friends = new ArrayList<>();
    }

    public User(String firstName, String lastName, String username, String email, String password) {
        this.setId(UUID.randomUUID());
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.friends = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Adds a friend to the user's friend list
     *
     * @param user - the user to be added
     */
    public void addFriend(User user) {
        this.friends.add(user);
    }

    /**
     * Removes a friend from the user's friend list
     *
     * @param user - the user to be removed
     */
    public void removeFriend(User user) {
        this.friends.remove(user);
    }

    /**
     * Gets the user's friend list
     *
     * @return the user's friend list
     */
    public List<User> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return "User{" +
//                "id='" + this.getId() + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, username, email);
    }
}
