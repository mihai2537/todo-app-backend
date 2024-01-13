package com.app.todo.repository;

import com.app.todo.model.Item;
import com.app.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByUser(User user);
    Optional<Item> findByIdAndUser(long id, User user);
    long countAllByUser(User user);
}
