package com.projects.socialnetwork.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Message extends Entity<UUID> {
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime sentAt;

    public Message(User from, List<User> to, String message, LocalDateTime sentAt) {
        this.setId(UUID.randomUUID());
        this.from = from;
        this.to = to;
        this.message = message;
        this.sentAt = sentAt;
    }

    public Message(UUID id, User from, List<User> to, String message, LocalDateTime sentAt) {
        this.setId(id);
        this.from = from;
        this.to = to;
        this.message = message;
        this.sentAt = sentAt;
    }


    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message1)) return false;
        return Objects.equals(from, message1.from) && Objects.equals(to, message1.to) && Objects.equals(message, message1.message) && Objects.equals(sentAt, message1.sentAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, message, sentAt);
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + sentAt +
                '}';
    }
}
