package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.TransactionStatus;
import com.example.bankcards.entity.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Отправитель (null для DEPOSIT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_card_id")
    private Card sourceCard;

    // Получатель (null для WITHDRAWAL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_card_id")
    private Card destinationCard;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "timestamp", nullable = false, updatable = false, columnDefinition = "datetime(6)")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type; // WITHDRAWAL, DEPOSIT, TRANSFER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status; // SUCCESS, FAILED

    @Column(length = 255)
    private String description;

    // === Конструкторы ===

    /**
     * Обязателен для JPA и юнит-тестов.
     * Ничего не делает — просто нужен для фреймворков.
     */
    protected Transaction() {}

    public Transaction(Card sourceCard,
                       Card destinationCard,
                       BigDecimal amount,
                       TransactionType type,
                       TransactionStatus status,
                       String description) {
        this.sourceCard = sourceCard;
        this.destinationCard = destinationCard;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.description = description;
    }

    // === Геттеры и сеттеры ===

    public Long getId() {
        return id;
    }

    public Card getSourceCard() {
        return sourceCard;
    }

    public void setSourceCard(Card sourceCard) {
        this.sourceCard = sourceCard;
    }

    public Card getDestinationCard() {
        return destinationCard;
    }

    public void setDestinationCard(Card destinationCard) {
        this.destinationCard = destinationCard;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // === Жизненный цикл JPA ===
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    // === equals/hashCode только по id ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // === toString без карт (иначе Lazy-загрузка) ===
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", type=" + type +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", description='" + description + '\'' +
                '}';
    }
}
