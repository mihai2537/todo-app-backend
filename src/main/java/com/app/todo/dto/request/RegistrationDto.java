package com.app.todo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDto {
    @Email(message = "Not a valid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Size(min=8, max=256, message = "Password must be at least 8 characters long")
    @NotNull(message = "Password cannot be null")
    private String password;
}
