package com.app.todo.controller;

import com.app.todo.dto.request.LoginDto;
import com.app.todo.dto.request.RegistrationDto;
import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.LoginResponseDto;
import com.app.todo.dto.response.UserRespDto;
import com.app.todo.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@Validated
public class AuthController {
    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<UserRespDto>> registerUser(@Valid @RequestBody RegistrationDto body) {
        APIResponse<UserRespDto> resp = authService.registerUser(body.getEmail(), body.getPassword());

        return ResponseEntity
                .status(HttpStatus.valueOf(resp.getHttpStatus()))
                .body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<LoginResponseDto>> loginUser(@Valid @RequestBody LoginDto body) {
        APIResponse<LoginResponseDto> resp = authService.loginUser(body.getEmail(), body.getPassword());

        return ResponseEntity
                .status(HttpStatus.valueOf(resp.getHttpStatus()))
                .body(resp);
    }
}
