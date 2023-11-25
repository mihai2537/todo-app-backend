package com.app.todo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Item extends AuditableEntity {
    @Column(length = 64)
    private String text = "";
}
