package com.app.todo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Item extends AuditableEntity {
    @Column(length = 64)
    private String text = "";

    @ManyToOne()
    @JoinColumn(name="user_id")
    private User user;

    public Item() {
    }

    public Item(String text) {
        this.text = text;
    }
}
