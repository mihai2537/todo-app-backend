package com.app.todo.service;

import com.app.todo.dto.response.UserRespDto;
import com.app.todo.model.Role;
import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthenticationService {
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRespDto registerUser(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        Role role = Role.USER;
        User user = new User(email, encodedPassword, role.getValue());
        userRepo.save(user);

        return new UserRespDto(user.getEmail(), user.getRoles());
    }
}
