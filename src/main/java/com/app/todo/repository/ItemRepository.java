package com.app.todo.repository;

import com.app.todo.model.Item;
import com.app.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByUser(User user);
}
