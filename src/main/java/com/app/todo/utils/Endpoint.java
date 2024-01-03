package com.app.todo.utils;

/**
 * Constants for the controllers' endpoints paths
 */
public enum Endpoint {
    LOGIN("/auth/login"),
    REGISTER("/auth/register");

    Endpoint(String s) {
        this.value = s;
    }

    private final String value;

    @Override
    public String toString() {
        return this.value;
    }
}
