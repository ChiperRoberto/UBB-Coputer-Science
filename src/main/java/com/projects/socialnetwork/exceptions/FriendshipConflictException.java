package com.projects.socialnetwork.exceptions;

public class FriendshipConflictException extends RuntimeException {
    public FriendshipConflictException() {
        super();
    }

    public FriendshipConflictException(String message) {
        super(message);
    }

    public FriendshipConflictException(String message, Throwable cause) {
        super(message,cause);
    }

    public FriendshipConflictException(Throwable cause) {
        super(cause);
    }

    public FriendshipConflictException(String message,Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message,cause,enableSuppression,writableStackTrace);
    }
}
