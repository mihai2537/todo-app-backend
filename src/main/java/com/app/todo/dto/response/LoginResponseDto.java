package com.app.todo.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String email;
    private String jwt;

    public LoginResponseDto(String email, String jwt) {
        this.email = email;
        this.jwt = jwt;
    }

    public LoginResponseDto() {}
}
