package com.app.todo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
public class User extends AuditableEntity {
    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private String roles;

    @OneToMany(fetch= FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    public User(String email, String password, String roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User() {}
}
