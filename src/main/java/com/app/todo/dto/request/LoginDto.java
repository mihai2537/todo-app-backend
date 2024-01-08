package com.app.todo.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 256, message = "Email length cannot be bigger than 256")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(max = 256, message = "Password length cannot be bigger than 256")
    private String password;
}
