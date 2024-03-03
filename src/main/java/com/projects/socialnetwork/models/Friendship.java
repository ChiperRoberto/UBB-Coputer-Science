package com.projects.socialnetwork.models;

import com.projects.socialnetwork.ENUM.FriendshipRequestStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Friendship extends Entity<UUID>{
    private User user1;
    private User user2;
    private LocalDateTime friendsSince;
    private FriendshipRequestStatus friendshipStatus;

    public Friendship(User user1, User user2) {
        this.setId(UUID.randomUUID());
        this.user1 = user1;
        this.user2 = user2;
        this.friendsSince = LocalDateTime.now();
        this.friendshipStatus = FriendshipRequestStatus.PENDING;
    }

    public Friendship(User user1, User user2, LocalDateTime friendsSince) {
        this.setId(UUID.randomUUID());
        this.user1 = user1;
        this.user2 = user2;
        this.friendsSince = friendsSince;
        this.friendshipStatus = FriendshipRequestStatus.PENDING;
    }

    public Friendship(User user1, User user2, LocalDateTime friendsSince, FriendshipRequestStatus status) {
        this.setId(UUID.randomUUID());
        this.user1 = user1;
        this.user2 = user2;
        this.friendsSince = friendsSince;
        this.friendshipStatus = status;
    }



    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }

    public void setFriendsSince(LocalDateTime friendsSince) {
        this.friendsSince = friendsSince;
    }

    public FriendshipRequestStatus getFriendshipStatus() {
        return friendshipStatus;
    }

    public void setFriendshipStatus(FriendshipRequestStatus friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship that)) return false;
        return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2);
    }

    @Override
    public String toString() {
        return user1.getUsername() + " " + user1.getEmail() + " " + friendsSince.toString() + " " + friendshipStatus.toString();
    }

}
