package com.projects.socialnetwork.validators;

import com.projects.socialnetwork.exceptions.ValidationException;
import com.projects.socialnetwork.models.Friendship;

/**
 * Validator for a friendship
 * */

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (entity.getUser1().equals(entity.getUser2()))
            throw new ValidationException("You can't be friends with yourself!");
        if (entity.getUser1() == null || entity.getUser2() == null)
            throw new ValidationException("The friendship must have two users");
    }
}
