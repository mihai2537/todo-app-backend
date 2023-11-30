package com.app.todo.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRespDto {
    private String email;
    private String role;

    public UserRespDto(String email, String role) {
        this.email = email;
        this.role = role;
    }
}
