package com.app.todo.controller;

import com.app.todo.dto.request.ItemReqDto;
import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.ItemResponseDto;
import com.app.todo.model.User;
import com.app.todo.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<ItemResponseDto>> createItem(@RequestBody ItemReqDto body, Authentication auth) {
        APIResponse<ItemResponseDto> resp = itemService.createItem(body.getText(), (User)auth.getPrincipal());

        return ResponseEntity
                .status(HttpStatus.valueOf(resp.getHttpStatus()))
                .body(resp);
    }
}
