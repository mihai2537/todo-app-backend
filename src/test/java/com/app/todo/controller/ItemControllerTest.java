package com.app.todo.controller;

import com.app.todo.dto.request.ItemReqDto;
import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.ItemResponseDto;
import com.app.todo.dto.response.ItemsResponseDto;
import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import com.app.todo.security.CustomUserDetailsService;
import com.app.todo.security.SecurityConfiguration;
import com.app.todo.service.ItemService;
import com.app.todo.utils.Endpoint;
import com.app.todo.utils.RsaKeyProperties;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockAuthentication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@Import({SecurityConfiguration.class, RsaKeyProperties.class, CustomUserDetailsService.class})
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    // The userRepository is needed by the SecurityConfiguration
    @MockBean
    private UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateFails_whenUserNotLoggedIn() throws Exception {
        mockMvc.perform(post(Endpoint.ITEM_CREATE.toString()).with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    /** create **/

    @Test
    @WithMockAuthentication(name = "user@noemail.com", principalType = User.class, authorities = {"ROLE_USER"})
    public void testCreateWorks_whenUserLoggedIn() throws Exception {
        ItemReqDto body = new ItemReqDto();
        body.setText("bla");

        ItemResponseDto data = new ItemResponseDto();
        data.setText(body.getText());
        data.setId(1);
        APIResponse<ItemResponseDto> resp = APIResponse.ok(data, "bla");

        when(itemService.createItem(eq(body.getText()), Mockito.any(User.class))).thenReturn(resp);

        mockMvc.perform(
                post(Endpoint.ITEM_CREATE.toString())
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.text").value(body.getText()))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockAuthentication(name = "user@noemail.com", principalType = User.class, authorities = {"ROLE_USER"})
    public void testCreateFails_whenMissingBody() throws Exception {
        String expectedMsg = "Request body is missing or malformed. Please provide a valid request body.";

        mockMvc.perform(
                post(Endpoint.ITEM_CREATE.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMsg));
    }

    @Test
    @WithMockAuthentication(name = "user@noemail.com", principalType = User.class, authorities = {"ROLE_USER"})
    public void testCreateFails_whenTextEmptyString() throws Exception {
        ItemReqDto body = new ItemReqDto();
        body.setText("");

        mockMvc.perform(
                post(Endpoint.ITEM_CREATE.toString())
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Text cannot be blank"));
    }

    @Test
    @WithMockAuthentication(name = "user@noemail.com", principalType = User.class, authorities = {"ROLE_USER"})
    public void testCreateFails_whenTextNull() throws Exception {
        ItemReqDto body = new ItemReqDto();
        body.setText(null);

        mockMvc.perform(
                post(Endpoint.ITEM_CREATE.toString())
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Text cannot be blank"));
    }

    @Test
    @WithMockAuthentication(name = "user@noemail.com", principalType = User.class, authorities = {"ROLE_USER"})
    public void testCreateFails_whenTextExceedsLength() throws Exception {
        ItemReqDto body = new ItemReqDto();
        body.setText("a".repeat(33));

        mockMvc.perform(
                post(Endpoint.ITEM_CREATE.toString())
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Text cannot be longer than 32"));
    }

    /** show **/
    @Test
    public void testShowFails_whenUserNotLoggedIn() throws Exception {
        mockMvc.perform(post(Endpoint.ITEM_SHOW.toString()).with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockAuthentication(name =  "user@noemail.com", principalType = User.class, authorities = {"ROLE_USER"})
    public void testShowWorks_whenUserLoggedIn() throws Exception {
        APIResponse<ItemsResponseDto> resp = APIResponse.ok(new ItemsResponseDto(), "bla");
        when(itemService.showItems(Mockito.any(User.class))).thenReturn(resp);

        mockMvc.perform(
                get(Endpoint.ITEM_SHOW.toString())
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.items").hasJsonPath());
    }

    /** delete **/

    @Test
    public void testDeleteFails_whenUserNotLoggedIn() throws Exception {
        mockMvc.perform(delete(Endpoint.ITEM_DELETE.toString()).with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockAuthentication(name = "user@noemail.com", principalType = User.class, authorities = {"ROLE_USER"})
    public void testDeleteWorks_whenUserLoggedIn() throws Exception {
        ItemResponseDto data = new ItemResponseDto();
        APIResponse<ItemResponseDto> resp = APIResponse.ok(data, "bla");
        long id = 1;

        when(itemService.deleteItem(Mockito.anyLong(), Mockito.any(User.class))).thenReturn(resp);

        mockMvc.perform(
                delete(Endpoint.ITEM_DELETE.toString())
                        .param("id", String.valueOf(id))
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.id").hasJsonPath())
                .andExpect(jsonPath("$.data.text").hasJsonPath());
    }

    @Test
    @WithMockAuthentication(name = "user@noemail.com", principalType = User.class, authorities = {"ROLE_USER"})
    public void testDeleteFails_whenIdMissing() throws Exception {
        mockMvc.perform(
                delete(Endpoint.ITEM_DELETE.toString())
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Parameter id of type long is missing!"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockAuthentication(name = "user@noemail.com", principalType = User.class, authorities = {"ROLE_USER"})
    public void testDeleteFails_whenIdWrongType() throws Exception {
        mockMvc.perform(
                delete(Endpoint.ITEM_DELETE.toString()).param("id", "wrongVal")
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Parameter id must be of type long"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
