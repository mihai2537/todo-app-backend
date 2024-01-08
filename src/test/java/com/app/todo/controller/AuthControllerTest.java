package com.app.todo.controller;

import com.app.todo.dto.request.LoginDto;
import com.app.todo.dto.request.RegistrationDto;
import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.LoginResponseDto;
import com.app.todo.dto.response.UserRespDto;
import com.app.todo.repository.UserRepository;
import com.app.todo.security.CustomUserDetailsService;
import com.app.todo.security.SecurityConfiguration;
import com.app.todo.service.AuthenticationService;
import com.app.todo.service.TokenService;
import com.app.todo.utils.Endpoint;
import com.app.todo.utils.RsaKeyProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfiguration.class, RsaKeyProperties.class, CustomUserDetailsService.class})
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authService;
    // This bean is needed to build the AuthController
    @MockBean
    private TokenService tokenService;
    // The userRepository is needed by the SecurityConfiguration
    @MockBean
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testRegisterUser_worksWithValidCred() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("user@user.com");
        dto.setPassword("passwordLongEnough");

        when(authService.registerUser(dto.getEmail(), dto.getPassword())).thenReturn(
                APIResponse.ok(new UserRespDto(dto.getEmail(), "USER"), "User created")
        );

        mockMvc.perform(
                post("/auth/register")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    public void testRegisterUser_failsWithInvalidEmailFormat() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("user");
        dto.setPassword("passwordLongEnough");

        mockMvc.perform(
                        post("/auth/register")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Not a valid email format"));
    }
    @Test
    public void testRegisterUser_failsWithInvalidEmailEmptyString() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("");
        dto.setPassword("passwordLongEnough");

        mockMvc.perform(
                        post("/auth/register")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email cannot be blank"));
    }
    @Test
    public void testRegisterUser_failsWithInvalidEmailNull() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail(null);
        dto.setPassword("passwordLongEnough");

        mockMvc.perform(
                        post("/auth/register")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email cannot be blank"));
    }
    @Test
    public void testRegisterUser_failsWhenEmailLengthExceeded() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("a".repeat(65) + "@user.com");
        dto.setPassword("passwordLongEnough");

        mockMvc.perform(
                        post(Endpoint.REGISTER.toString())
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Not a valid email format"));
    }
    @Test
    public void testRegisterUser_failsWithMissingBody() throws Exception {
        String expectedMsg = "Request body is missing or malformed. Please provide a valid request body.";

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMsg));
    }
    @Test
    public void testRegisterUser_failsWithPasswordEmptyString() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("user@user.com");
        dto.setPassword("");

        mockMvc.perform(
                        post("/auth/register")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password must be at least 8 characters long"));
    }

    @Test
    public void testRegisterUser_failsWithPasswordNull() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("user@user.com");
        dto.setPassword(null);

        mockMvc.perform(
                        post("/auth/register")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password cannot be null"));
    }

    @Test
    public void testLoginUser_worksWhenValidCred() throws Exception {
        LoginDto body = new LoginDto();
        body.setEmail("user@user.com");
        body.setPassword("pass");

        when(authService.loginUser(body.getEmail(), body.getPassword())).thenReturn(
                APIResponse.ok(new LoginResponseDto(body.getEmail(), "jwtToken"), "Logged in successfully")
        );

        mockMvc.perform(
                post("/auth/login")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email").value(body.getEmail()))
                .andExpect(jsonPath("$.data.jwt").value("jwtToken"));
    }

    @Test
    public void testLoginUser_failsWhenWrongCredentials() throws Exception {
        LoginDto body = new LoginDto();
        body.setEmail("user@user.com");
        body.setPassword("pass");

        when(authService.loginUser(body.getEmail(), body.getPassword())).thenReturn(
                APIResponse.unauthorized(new LoginResponseDto("", ""), "Login failed")
        );

        mockMvc.perform(
                        post("/auth/login")
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email").value(""))
                .andExpect(jsonPath("$.data.jwt").value(""));
    }

    @Test
    public void testLoginUser_failsWhenEmailEmptyString() throws Exception {
        LoginDto body = new LoginDto();
        body.setEmail("");
        body.setPassword("pass");

        mockMvc.perform(
                post(Endpoint.LOGIN.toString())
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email cannot be blank"));
    }

    @Test
    public void testLoginUser_failsWhenEmailNull() throws Exception {
        LoginDto body = new LoginDto();
        body.setEmail(null);
        body.setPassword("pass");

        mockMvc.perform(
                        post(Endpoint.LOGIN.toString())
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email cannot be blank"));
    }

    @Test
    public void testLoginUser_failsWhenEmailLengthExceeded() throws Exception {
        LoginDto body = new LoginDto();
        body.setEmail("a".repeat(65) + "@user.com");
        body.setPassword("pass");

        mockMvc.perform(
                        post(Endpoint.LOGIN.toString())
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Not a valid email format"));
    }

    @Test
    public void testLoginUser_failsWhenPasswordEmptyString() throws Exception {
        LoginDto body = new LoginDto();
        body.setEmail("ion@ion.com");
        body.setPassword("");

        mockMvc.perform(
                        post(Endpoint.LOGIN.toString())
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password cannot be blank"));
    }

    @Test
    public void testLoginUser_failsWhenPasswordNull() throws Exception {
        LoginDto body = new LoginDto();
        body.setEmail("ion@ion.com");
        body.setPassword(null);

        mockMvc.perform(
                        post(Endpoint.LOGIN.toString())
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password cannot be blank"));
    }

    @Test
    public void testLoginUser_failsWhenPasswordLengthExceeded() throws Exception {
        LoginDto body = new LoginDto();
        body.setEmail("ion@ion.com");
        body.setPassword("p".repeat(257));

        mockMvc.perform(
                        post(Endpoint.LOGIN.toString())
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password length cannot be bigger than 256"));
    }

    @Test
    public void testLoginUser_failsWhenMissingBody() throws Exception {
        String errMsg = "Request body is missing or malformed. Please provide a valid request body.";
        mockMvc.perform(
                        post(Endpoint.LOGIN.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(errMsg));
    }
 }
