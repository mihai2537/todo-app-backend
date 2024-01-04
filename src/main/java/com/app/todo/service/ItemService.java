package com.app.todo.service;

import com.app.todo.dto.response.APIResponse;
import com.app.todo.dto.response.ItemResponseDto;
import com.app.todo.dto.response.ItemsResponseDto;
import com.app.todo.model.Item;
import com.app.todo.model.User;
import com.app.todo.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    /**
     * Retrieve the items of the user passed as param.
     * @param user usually the logged-in user
     * @return the list of items of the user
     a*/
    public APIResponse<ItemsResponseDto> showItems(User user) {
        List<Item> items = itemRepository.findAllByUser(user);
        List<ItemResponseDto> itemsDto = items
                .stream()
                .map(item -> new ItemResponseDto(item.getId(), item.getText()))
                .toList();

        return APIResponse.ok(
                new ItemsResponseDto(itemsDto),
                "Items retrieved"
        );
    }

    /**
     * Deletes the item with the given id which belongs to the given user.
     * @param id of the item
     * @param user usually the logged-in user
     * @return The deleted item if successful, dummy item otherwise.
     */
    public APIResponse<ItemResponseDto> deleteItem(long id, User user) {
        Optional<Item> item = itemRepository.findByIdAndUser(id, user);
        ItemResponseDto response = new ItemResponseDto();

        response.setId(-1);
        response.setText("");

        if (item.isEmpty()) {
            return APIResponse.notFound(response, "No such item was found");
        }

        response.setId(item.get().getId());
        response.setText(item.get().getText());

        itemRepository.delete(item.get());

        return APIResponse.ok(
                response,
                "Item deleted"
        );
    }
}
