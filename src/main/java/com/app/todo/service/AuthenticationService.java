package com.app.todo.service;

import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.LoginResponseDto;
import com.app.todo.dto.response.UserRespDto;
import com.app.todo.model.Role;
import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    private final TokenService tokenService;

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

    public APIResponse<UserRespDto> registerUser(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        Role role = Role.USER;
        User user = new User(email, encodedPassword, role.getValue());
        userRepo.save(user);

        return APIResponse.ok(new UserRespDto(user.getEmail(), user.getRoles()), "User created");
    }

    public APIResponse<LoginResponseDto> loginUser(String userName, String password) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userName, password)
            );
            String token = tokenService.generateToken(auth);

            return APIResponse.ok(new LoginResponseDto(userName, token), "Logged in successfully");

        } catch(AuthenticationException e) {
            return APIResponse.unauthorized(new LoginResponseDto("", ""), "Login failed");
        }
    }
}
