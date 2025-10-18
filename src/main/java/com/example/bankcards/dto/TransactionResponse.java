package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для отображения транзакции.
 */
@Builder
@Getter
@Setter
@Schema(description = "Информация о транзакции")
public class TransactionResponse {

    @Schema(description = "ID транзакции", example = "123")
    private Long id;

    @Schema(description = "Номер карты списания (маскированный)", example = "**** **** **** 1234")
    private String sourceCardMasked;

    @Schema(description = "Номер карты зачисления (маскированный)", example = "**** **** **** 5678")
    private String destinationCardMasked;

    @Schema(description = "Сумма транзакции", example = "1500.50")
    private BigDecimal amount;

    @Schema(description = "Тип транзакции", example = "TRANSFER")
    private String type;

    @Schema(description = "Статус транзакции", example = "SUCCESS")
    private String status;

    @Schema(description = "Дата и время транзакции", example = "2025-09-18 14:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Описание транзакции", example = "Перевод между своими картами")
    private String description;
}
