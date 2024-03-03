package com.projects.socialnetwork.DTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserFriendDTO {
    private String username;
    private String email;

    private LocalDateTime friendsSince;

    public UserFriendDTO(String username, String email, LocalDateTime friendsSince) {
        this.username = username;
        this.email = email;
        this.friendsSince = friendsSince;
    }

    public UserFriendDTO() {
        this.username = "";
        this.email = "";
        this.friendsSince = LocalDateTime.now();
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

    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }

    public void setFriendsSince(LocalDateTime friendsSince) {
        this.friendsSince = friendsSince;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserFriendDTO that)) return false;
        return Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(friendsSince, that.friendsSince);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, friendsSince);
    }
}
