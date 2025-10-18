package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Запрос на создание карты")
public class CreateCardRequest {

    @Schema(description = "Номер карты (16 цифр)", example = "1234567812345678")
    @NotBlank(message = "Номер карты обязателен")
    @Size(min = 16, max = 16, message = "Номер карты должен быть 16 цифр")
    private String cardNumber;

    @Schema(description = "Начальный баланс", example = "1000.00", defaultValue = "0.00")
    @DecimalMin(value = "0.0", inclusive = true, message = "Баланс не может быть отрицательным")
    private BigDecimal balance;
}
