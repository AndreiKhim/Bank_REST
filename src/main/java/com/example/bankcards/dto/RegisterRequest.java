package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для регистрации нового пользователя.
 */
@Getter
@Setter
@Schema(description = "Запрос на регистрацию нового пользователя")
public class RegisterRequest {

    @Schema(description = "Имя пользователя", example = "Андрей")
    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Соколов")
    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    @Schema(description = "Email пользователя", example = "sokolov@example.com")
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @Schema(description = "Телефон пользователя (11 цифр без пробелов и символов)", example = "79001234567")
    @NotBlank(message = "Телефон обязателен")
    private String phoneNumber;

    @Schema(description = "Пароль пользователя", example = "password123")
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;

    @Schema(description = "Роль пользователя (игнорируется при самостоятельной регистрации, задается админом)", example = "USER")
    private String role;
}
