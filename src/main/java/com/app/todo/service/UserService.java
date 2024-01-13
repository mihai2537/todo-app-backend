package com.app.todo.service;

import com.app.todo.repository.UserRepository;
import com.app.todo.service.interfaces.FieldValueExists;
import org.springframework.stereotype.Service;

@Service
public class UserService implements FieldValueExists {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException {
        if (fieldName == null) {
            return false;
        }

        if (!fieldName.equals("email")) {
            throw new UnsupportedOperationException("Field name not supported!");
        }

        if (value == null) {
            return false;
        }

        return this.userRepo.existsByEmail(value.toString());
    }
}
