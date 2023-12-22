package com.app.todo.controller;

import com.app.todo.dto.request.LoginDto;
import com.app.todo.dto.request.RegistrationDto;
import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.LoginResponseDto;
import com.app.todo.dto.response.UserRespDto;
import com.app.todo.service.AuthenticationService;
import com.app.todo.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<APIResponse<UserRespDto>> registerUser(@RequestBody RegistrationDto body) {
        APIResponse<UserRespDto> resp = authService.registerUser(body.getEmail(), body.getPassword());

        return ResponseEntity
                .status(HttpStatus.valueOf(resp.getHttpStatus()))
                .body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<LoginResponseDto>> loginUser(@RequestBody LoginDto body) {
        APIResponse<LoginResponseDto> resp = authService.loginUser(body.getEmail(), body.getPassword());

        return ResponseEntity
                .status(HttpStatus.valueOf(resp.getHttpStatus()))
                .body(resp);
    }
}
