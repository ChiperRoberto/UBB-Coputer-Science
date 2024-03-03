package com.projects.socialnetwork.DTO;

import java.time.LocalDateTime;

public class UserFriendshipDTO {
    private String firstName;
    private String lastName;
    private LocalDateTime friendsSince;

    public UserFriendshipDTO(String firstName, String lastName, LocalDateTime friendsSince) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friendsSince = friendsSince;
    }

    public UserFriendshipDTO() {
        this.firstName = "";
        this.lastName = "";
        this.friendsSince = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return firstName + "|" + lastName + "|" + friendsSince;
    }
}
