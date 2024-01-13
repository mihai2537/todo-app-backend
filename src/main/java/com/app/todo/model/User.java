package com.app.todo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
public class User extends AuditableEntity {
    @Column(unique = true)
    @Email
    @NotBlank
    private String email;

    @Column
    @Size(min = 8, max = 256)
    @NotNull
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
