package com.example.bankcards.entity;


import com.example.bankcards.entity.enums.TransactionStatus;
import com.example.bankcards.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Отправитель (null для DEPOSIT)
    @ManyToOne
    @JoinColumn(name = "source_card_id")
    private Card sourceCard;

    // Получатель (null для WITHDRAWAL)
    @ManyToOne
    @JoinColumn(name = "destination_card_id")
    private Card destinationCard;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // WITHDRAWAL, DEPOSIT, TRANSFER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status; // SUCCESS, FAILED

    @Column(length = 255)
    private String description;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (status == null) {
            status = TransactionStatus.SUCCESS; // по умолчанию успешная
        }
    }
}