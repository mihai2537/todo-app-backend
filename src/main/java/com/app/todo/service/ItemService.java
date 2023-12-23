package com.app.todo.service;

import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.ItemResponseDto;
import com.app.todo.model.Item;
import com.app.todo.model.User;
import com.app.todo.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Create a new item for the logged-in user
     * @param text the content of the item
     * @return the body wrapped inside APIResponse
     */
    public APIResponse<ItemResponseDto> createItem(String text, User user) {
        Item item = new Item(text);
        item.setUser(user);
        itemRepository.save(item);

        return APIResponse.ok(
                new ItemResponseDto(item.getId(), item.getText()),
                "Item created successfully"
        );
    }
}
