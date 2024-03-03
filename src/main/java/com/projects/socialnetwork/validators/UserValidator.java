package com.projects.socialnetwork.validators;

import com.projects.socialnetwork.exceptions.ValidationException;
import com.projects.socialnetwork.models.User;

/**
 * Validator for the User entity
 */
public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";
        errors += validateFirstName(entity.getFirstName());
        errors += validateLastName(entity.getLastName());
        errors += validateUsername(entity.getUsername());
        errors += validateEmail(entity.getEmail());
        errors += validatePassword(entity.getPassword());
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private String validatePassword(String password) {
        StringBuilder errors = new StringBuilder();

        if (password == null || password.isEmpty()) {
            errors.append("Password cannot be empty!\n");
        }
        if (password.length() < 8) {
            errors.append("Password must be at least 8 characters long!\n");
        }
        if (!password.matches(".*[A-Z].*")) {
            errors.append("Password must contain at least one uppercase letter!\n");
        }
        if (!password.matches(".*[a-z].*")) {
            errors.append("Password must contain at least one lowercase letter!\n");
        }
        if (!password.matches(".*[0-9].*")) {
            errors.append("Password must contain at least one digit!\n");
        }
        if (!password.matches(".*[!@#$%^&*()\\-+].*")) {
            errors.append("Password must contain at least one special character (e.g., !@#$%^&*()-+)!\n");
        }

        return errors.toString();
    }


    private String validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "Email cannot be empty!\n";
        }
        if (!email.contains("@")) {
            return "Invalid email !\n";
        }
        return "";
    }

    private String validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            return "Username cannot be empty!\n";
        }
        return "";
    }

    private String validateLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return "Last name cannot be empty!\n";
        }
        return "";
    }

    private String validateFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            return "First name cannot be empty!\n";
        }
        return "";
    }
}
