package com.app.todo.controller;

import com.app.todo.dto.request.ItemReqDto;
import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.ItemResponseDto;
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
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    Converter<Jwt, AbstractAuthenticationToken> converter;

    @Test
    public void testCreateFails_whenUserNotLoggedIn() throws Exception {
        mockMvc.perform(post(Endpoint.ITEM_CREATE.toString()).with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

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
}
