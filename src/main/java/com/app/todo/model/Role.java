package com.app.todo.model;

public enum Role {
    USER("USER"),
    ADMIN("ADMIN");
    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getValue() {
        return this.role;
    }
}
