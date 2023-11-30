package com.app.todo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDto {
    private String email;
    private String password;
}
