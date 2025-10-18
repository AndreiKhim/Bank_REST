package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Ответ при аутентификации или регистрации")
public class AuthResponse {

    @Schema(description = "JWT токен для доступа", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Сообщение о результате операции", example = "Регистрация успешна")
    private String message;

    @Schema(description = "Информация о пользователе")
    private UserResponse user;

    public AuthResponse(String token, String message, UserResponse user) {
        this.token = token;
        this.message = message;
        this.user = user;
    }
}
