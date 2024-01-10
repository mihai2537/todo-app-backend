package com.app.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemReqDto {
    @NotBlank(message = "Text cannot be blank")
    @Size(max = 32, message = "Text cannot be longer than 32")
    private String text;
}
