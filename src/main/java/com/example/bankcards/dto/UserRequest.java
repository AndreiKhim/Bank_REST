package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для обновления данных пользователя.
 */
@Getter
@Setter
@Schema(description = "Запрос на обновление данных пользователя")
public class UserRequest {

    @NotBlank(message = "Имя обязательно")
    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String lastName;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    @Schema(description = "Email пользователя", example = "ivanov@example.com")
    private String email;

    @NotBlank(message = "Телефон обязателен")
    @Schema(description = "Телефон пользователя", example = "+79991234567")
    private String phoneNumber;

    @Schema(description = "Роль пользователя (может менять только админ)", example = "USER")
    private String role;
}
