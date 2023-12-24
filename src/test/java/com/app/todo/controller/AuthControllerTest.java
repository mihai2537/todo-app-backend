package com.app.todo.controller;

import com.app.todo.dto.request.RegistrationDto;
import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.UserRespDto;
import com.app.todo.repository.UserRepository;
import com.app.todo.security.CustomUserDetailsService;
import com.app.todo.security.SecurityConfiguration;
import com.app.todo.service.AuthenticationService;
import com.app.todo.service.TokenService;
import com.app.todo.utils.RsaKeyProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@Import({SecurityConfiguration.class, RsaKeyProperties.class, CustomUserDetailsService.class})
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authService;
    @MockBean
    private TokenService tokenService;

    // The userRepository is needed by the SecurityConfiguration
    @MockBean
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testRegisterUser() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("user@user.com");
        dto.setPassword("pass");

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
}
