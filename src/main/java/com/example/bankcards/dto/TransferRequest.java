package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO для перевода между картами пользователя.
 */
@Getter
@Setter
@Schema(description = "Запрос на перевод между своими картами")
public class TransferRequest {

    @Schema(description = "ID карты-источника", example = "1")
    @NotNull(message = "ID карты-источника обязателен")
    private Long sourceCardId;

    @Schema(description = "ID карты-получателя", example = "2")
    @NotNull(message = "ID карты-получателя обязателен")
    private Long destinationCardId;

    @Schema(description = "Сумма транзакции", example = "1500.50")
    @DecimalMin(value = "0.01", inclusive = true, message = "Сумма должна быть больше нуля")
    private BigDecimal amount;

    @Schema(description = "Описание транзакции", example = "Перевод между своими картами")
    private String description;
}
