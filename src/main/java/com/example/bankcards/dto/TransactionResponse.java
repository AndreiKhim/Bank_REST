package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.TransactionStatus;
import com.example.bankcards.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private String sourceCardMasked;
    private String destinationCardMasked;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private TransactionType type;
    private TransactionStatus status;
    private String description;
}
