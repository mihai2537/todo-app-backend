package com.app.todo.integration;

import com.app.todo.dto.request.ItemReqDto;
import com.app.todo.dto.request.LoginDto;
import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.ItemsResponseDto;
import com.app.todo.dto.response.LoginResponseDto;
import com.app.todo.model.Item;
import com.app.todo.model.Role;
import com.app.todo.model.User;
import com.app.todo.repository.ItemRepository;
import com.app.todo.repository.UserRepository;
import com.app.todo.utils.Endpoint;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ItemTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ItemRepository itemRepo;
    private User currentUser;
    private final String password = "password";
    private String jwt = "";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Local helper to send a login request and retrieve the jwt.
     * this.currentUser data is used for the login request
     * the jwt is then stored inside this.jwt and used inside other tests
     * @throws Exception due to mvc perform call
     */
    private void authenticateUserAndSetJwt() throws Exception {
        // Get the JWT token by sending a login request
        LoginDto body = new LoginDto();
        body.setPassword(password);
        body.setEmail(currentUser.getEmail());
        mvc.perform(
                post(Endpoint.LOGIN.toString())
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(result -> {
            APIResponse<LoginResponseDto> response;
            response = objectMapper
                    .readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
            this.jwt = response.getData().getJwt();
        });
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        User user = new User();
        user.setEmail("user@user.com");
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Role.USER.getValue());

        currentUser = userRepo.save(user);
        this.authenticateUserAndSetJwt();
    }

    @AfterEach
    public void afterEach() {
        currentUser = userRepo.findByEmail(currentUser.getEmail()).orElseThrow();
        userRepo.delete(currentUser);
    }

    // endpoint: create
    @Test
    public void testCreateItemFails_whenNotAuthenticated() throws Exception {
        mvc.perform(
                post(Endpoint.ITEM_CREATE.toString())
                        .content(objectMapper.writeValueAsString(new ItemReqDto()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateItemWorks_whenAuthenticated() throws Exception {
        String itemText = "First item";
        ItemReqDto body = new ItemReqDto();
        body.setText(itemText);

        mvc.perform(
                        post(Endpoint.ITEM_CREATE.toString())
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + this.jwt)
                )
                .andExpect(status().isOk());

        User user = userRepo.findByEmailWithItems(currentUser.getEmail()).orElse(new User());
        List<Item> items = user.getItems();

        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).getText()).isEqualTo(itemText);

    }

    // endpoint: show
    @Test
    public void testShowItemsFails_whenNotAuthenticated() throws Exception {
        mvc.perform(get(Endpoint.ITEM_SHOW.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowItemsWorks_whenAuthenticated() throws Exception {
        mvc.perform(
                    get(Endpoint.ITEM_SHOW.toString())
                            .header("Authorization", "Bearer " + this.jwt)
                )
                .andExpect(status().isOk());

    }

    @Test
    public void testShowItemsReturnsThreeItems() throws Exception {
        Item firstItem = new Item();
        Item secondItem = new Item();
        Item thirdItem = new Item();
        Item[] itemsArr = new Item[] {firstItem, secondItem, thirdItem};
        List<Item> items = Arrays.asList(itemsArr);

        for (Item item : items) {
            item.setUser(currentUser);
        }

        itemRepo.saveAll(items);

        assertThat(itemRepo.findAll().size()).isEqualTo(3);

        mvc.perform(
                        get(Endpoint.ITEM_SHOW.toString())
                                .header("Authorization", "Bearer " + this.jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").exists())
                .andExpect(jsonPath("$.data.items").isNotEmpty())
                .andDo(result -> {
                    APIResponse<ItemsResponseDto> response = objectMapper.readValue(
                           result.getResponse().getContentAsString(),
                           new TypeReference<>() {}
                   );

                    assertThat(response.getData().getItems().size()).isEqualTo(3);
                });
    }

    // endpoint: delete
    @Test
    public void testDeleteItemFails_whenNotAuthenticated() throws Exception {
        mvc.perform(delete(Endpoint.ITEM_DELETE.toString()).param("id", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteItemWorks_whenAuthenticated() throws Exception {
        String text = "First item";

        Item item = new Item();
        item.setUser(currentUser);
        item.setText(text);
        item = itemRepo.save(item);
        String idParam = "" + item.getId();

        assertThat(itemRepo.findAll().size()).isEqualTo(1);

        mvc.perform(
                delete(Endpoint.ITEM_DELETE.toString())
                        .param("id", idParam)
                        .header("Authorization", "Bearer " + this.jwt)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(item.getId()))
                .andExpect(jsonPath("$.data.text").value(text));

        assertThat(itemRepo.findAll().size()).isEqualTo(0);
    }
}
