package com.app.todo.integration;

import com.app.todo.dto.request.LoginDto;
import com.app.todo.model.Role;
import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import com.app.todo.service.TokenService;
import com.app.todo.utils.Endpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SecurityTest {
    @Autowired
    ApplicationContext applicationContext;

    @LocalServerPort
    private int port;
    private static HttpHeaders headers;

    @Autowired
    private MockMvc mvc;

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
    public void init() {
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

        mvc.perform(
                        post(Endpoint.LOGIN.toString())
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email").value(body.getEmail()))
                .andExpect(jsonPath("$.data.jwt").isNotEmpty());
    }

    @Test
    public void testLoginFails_whenUserNotExisting() throws Exception {
        String email = "bla@noemail.com";
        LoginDto body = new LoginDto();
        body.setEmail(email);
        body.setPassword(password);

        mvc.perform(
                        post(Endpoint.LOGIN.toString())
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email").value(""))
                .andExpect(jsonPath("$.data.jwt").value(""));
    }

    @Test
    public void testLoginFails_whenPasswordWrong() throws Exception {
        String email = currentUser.getEmail();
        LoginDto body = new LoginDto();
        body.setEmail(email);
        body.setPassword("WrongPassword");

        mvc.perform(
                        post(Endpoint.LOGIN.toString())
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email").value(""))
                .andExpect(jsonPath("$.data.jwt").value(""));
    }
}