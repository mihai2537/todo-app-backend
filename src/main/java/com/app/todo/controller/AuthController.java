package com.app.todo.controller;

import com.app.todo.dto.request.RegistrationDto;
import com.app.todo.dto.response.UserRespDto;
import com.app.todo.service.AuthenticationService;
import com.app.todo.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final TokenService tokenService;
    private AuthenticationService authService;

    public AuthController(TokenService tokenService, AuthenticationService authService) {
        this.tokenService = tokenService;
        this.authService = authService;
    }

    @PostMapping("/token")
    public String token(Authentication authentication) {
        LOG.debug("Token requested for '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        LOG.debug("Token granted {}", token);

        return token;
    }

    @PostMapping("/register")
    public UserRespDto registerUser(@RequestBody RegistrationDto body) {
        return authService.registerUser(body.getEmail(), body.getPassword());
    }
}
