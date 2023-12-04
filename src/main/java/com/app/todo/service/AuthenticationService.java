package com.app.todo.service;

import com.app.todo.dto.response.LoginResponseDto;
import com.app.todo.dto.response.UserRespDto;
import com.app.todo.model.Role;
import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthenticationService {
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authManager;

    private TokenService tokenService;

    public AuthenticationService(
            UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authManager,
            TokenService tokenService
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

    public UserRespDto registerUser(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        Role role = Role.USER;
        User user = new User(email, encodedPassword, role.getValue());
        userRepo.save(user);

        return new UserRespDto(user.getEmail(), user.getRoles());
    }

    public LoginResponseDto loginUser(String userName, String password) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userName, password)
            );
            String token = tokenService.generateToken(auth);
            return new LoginResponseDto(userName, token);

        } catch(AuthenticationException e) {
            return new LoginResponseDto("", "");
        }
    }
}
