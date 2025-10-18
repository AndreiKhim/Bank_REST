package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Ответ с информацией о карте")
public class CardResponse {

    @Schema(description = "ID карты", example = "42")
    private Long id;

    @Schema(description = "Маскированный номер карты", example = "**** **** **** 1234")
    private String cardNumber;

    @Schema(description = "Владелец карты (Фамилия Имя)", example = "Иванов Иван")
    private String cardHolder;

    @Schema(description = "Срок действия карты", example = "2028-09-18")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private String cardStatus;

    @Schema(description = "Баланс карты", example = "1500.75")
    private BigDecimal balance;

    @Schema(description = "Запрошена ли блокировка карты", example = "false")
    private boolean blockRequested;
}
