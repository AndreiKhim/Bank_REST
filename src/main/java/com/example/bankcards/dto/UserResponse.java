package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Ответ с информацией о пользователе")
public class UserResponse {

    @Schema(description = "ID пользователя", example = "101")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String lastName;

    @Schema(description = "Email пользователя", example = "ivanov@example.com")
    private String email;

    @Schema(description = "Номер телефона", example = "+79998887766")
    private String phone;

    @Schema(description = "Роль пользователя", example = "USER")
    private String role;
}
