package com.projects.socialnetwork.models;

import java.io.Serializable;

/**
 * Abstract class for entities
 */
public class Entity<ID> implements Serializable {
    private static final long serialVersionUID = 7331115341259248461L;

    /**
     * ID of the entity
     */
    protected ID id;

    /**
     * Getter for the ID
     *
     * @return ID
     */
    public ID getId() {
        return id;
    }

    /**
     * Setter for the ID
     *
     * @param id - new ID
     */
    public void setId(ID id) {
        this.id = id;
    }
}
