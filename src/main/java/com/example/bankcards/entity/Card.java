package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.EncryptionService;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Зашифрованный номер карты, хранится в БД.
     */
    @Column(name = "card_number_encrypted", nullable = false, length = 255)
    private String cardNumberEncrypted;

    /**
     * Срок действия карты.
     */
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    /**
     * Статус карты: ACTIVE, BLOCKED, EXPIRED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CardStatus cardStatus;

    /**
     * Баланс карты
     */
    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    /**
     * Запрос на блокировку карты
     */
    @Column(name = "block_requested", nullable = false)
    private boolean blockRequested;

    /**
     * Связь с пользователем
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // === Конструкторы ===

    protected Card() {
          }

    public Card(String cardNumberEncrypted,
                LocalDate expirationDate,
                CardStatus cardStatus,
                BigDecimal balance,
                boolean blockRequested,
                User user) {
        this.cardNumberEncrypted = cardNumberEncrypted;
        this.expirationDate = expirationDate;
        this.cardStatus = cardStatus;
        this.balance = balance;
        this.blockRequested = blockRequested;
        this.user = user;
    }

    // === Геттеры и сеттеры ===

    public Long getId() {
        return id;
    }

    public String getCardNumberEncrypted() {
        return cardNumberEncrypted;
    }

    public void setCardNumberEncrypted(String cardNumberEncrypted) {
        this.cardNumberEncrypted = cardNumberEncrypted;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isBlockRequested() {
        return blockRequested;
    }

    public void setBlockRequested(boolean blockRequested) {
        this.blockRequested = blockRequested;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // === Вспомогательные методы ===

    @Transient
    public String getCardNumberMasked(EncryptionService encryptionService) {
        String plainNumber = encryptionService.decrypt(this.cardNumberEncrypted);
        return encryptionService.maskCardNumber(plainNumber);
    }

    @Transient
    public String getCardHolder() {
        if (user == null) return null;
        return user.getLastName() + " " + user.getFirstName();
    }

    // === Жизненный цикл JPA ===
    @PrePersist
    protected void onCreate() {
        if (cardStatus == null) cardStatus = CardStatus.ACTIVE;
        if (balance == null) balance = BigDecimal.ZERO;
    }

    // === equals/hashCode только по id ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return id != null && id.equals(card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // === toString без user (иначе потянет lazy) ===
    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", expirationDate=" + expirationDate +
                ", cardStatus=" + cardStatus +
                ", balance=" + balance +
                ", blockRequested=" + blockRequested +
                '}';
    }
}