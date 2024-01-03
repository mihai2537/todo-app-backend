package com.app.todo.integration;

import com.app.todo.dto.request.LoginDto;
import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.LoginResponseDto;
import com.app.todo.model.Role;
import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import com.app.todo.service.TokenService;
import com.app.todo.utils.Endpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityTest {
    @Autowired
    ApplicationContext applicationContext;

    @LocalServerPort
    private int port;
    private static HttpHeaders headers;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    private User currentUser;
    private final String password = "password";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private String createURLWithPort(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }


    @BeforeEach
    public void beforeEach() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Role.USER.getValue());

        currentUser = user;
        userRepo.save(user);
    }

    @AfterEach
    public void afterEach() {
        userRepo.delete(currentUser);
    }

    @Test
    public void testLoginWorks_whenUserExists() throws Exception {
        String email = currentUser.getEmail();
        LoginDto body = new LoginDto();
        body.setEmail(email);
        body.setPassword(password);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
        ResponseEntity<APIResponse<LoginResponseDto>> response = restTemplate.exchange(
                createURLWithPort(Endpoint.LOGIN.toString()),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>(){}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertThat(response.getBody()).isNotNull();

        assertEquals(HttpStatus.OK.getReasonPhrase(), response.getBody().getStatus());
        assertEquals("Logged in successfully", response.getBody().getMessage());

        LoginResponseDto resData = response.getBody().getData();
        assertEquals(email, resData.getEmail());
        Assertions.assertThat(resData.getJwt()).isNotBlank();
    }
}