package com.app.todo.service;

import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.LoginResponseDto;
import com.app.todo.dto.response.UserRespDto;
import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthenticationServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authManager;
    @Mock
    TokenService tokenService;
    @InjectMocks
    AuthenticationService authService;
    // These last two mocks are used locally
    @Mock
    Authentication authentication;
    @Mock
    AuthenticationException authenticationException;

    @Test
    public void testRegisterUser_worksWithValidCred() {
        String email = "user@user.com";
        String password = "pass";
        String encodedPassword = "encodedPass";

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        APIResponse<UserRespDto> res = authService.registerUser(email, password);

        assertEquals(HttpStatus.OK.value(), res.getHttpStatus());
        assertEquals(HttpStatus.OK.getReasonPhrase(), res.getStatus());
        assertEquals("User created", res.getMessage());

        assertEquals(email, res.getData().getEmail());
        assertEquals("USER", res.getData().getRole());
    }

    @Test
    public void testLoginUser_worksWhenValidCred() {
        String email = "user@user.com";
        String password = "pass";
        String token = "jwtToken";

        when(authManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenService.generateToken(Mockito.any(Authentication.class))).thenReturn(token);

        APIResponse<LoginResponseDto> res = authService.loginUser(email, password);

        assertEquals(HttpStatus.OK.value(), res.getHttpStatus());
        assertEquals(HttpStatus.OK.getReasonPhrase(), res.getStatus());
        assertEquals("Logged in successfully", res.getMessage());

        assertEquals(email, res.getData().getEmail());
        assertEquals(token, res.getData().getJwt());
    }

    @Test
    public void testLoginUser_failsWhenInvalidCred() {
        String email = "user@user.com";
        String password = "pass";

        when(authManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(authenticationException);

        APIResponse<LoginResponseDto> res = authService.loginUser(email, password);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), res.getHttpStatus());
        assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), res.getStatus());
        assertEquals("Login failed", res.getMessage());

        assertEquals("", res.getData().getEmail());
        assertEquals("", res.getData().getJwt());

    }

}
