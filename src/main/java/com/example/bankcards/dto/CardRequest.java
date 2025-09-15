package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardRequest {

    @NotBlank(message = "Номер карты обязателен")
    @Size(min = 16, max = 16, message = "Номер карты должен быть 16 цифр")
    private String cardNumber;

    @NotNull(message = "Срок действия карты обязателен")
    private LocalDate expirationDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Баланс не может быть отрицательным")
    private BigDecimal balance;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}