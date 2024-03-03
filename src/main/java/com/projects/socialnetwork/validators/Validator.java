package com.projects.socialnetwork.validators;

import com.projects.socialnetwork.exceptions.ValidationException;

/**
 * Validator interface for validating entities
 * @param <T> - type of entity to be validated
 */
public interface Validator<T>{

    /**
     * Validates data from an entity
     * @param entity - the entity to be validated
     * @throws ValidationException if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}
