package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.EncryptionService;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(name = "status", nullable = false)
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
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // -------------------------
    // Вспомогательные методы
    // -------------------------

    /**
     * Получение маскированного номера карты для отображения (**** **** **** 1234).
     * Здесь мы уже работаем с расшифрованным номером, который сервис нам передаст.
     */
    @Transient
    public String getCardNumberMasked(EncryptionService encryptionService) {
        String plainNumber = encryptionService.decrypt(this.cardNumberEncrypted);
        return encryptionService.maskCardNumber(plainNumber);
    }

    /**
     * Виртуальное поле для отображения владельца карты
     */
    @Transient
    public String getCardHolder() {
        if (user == null) return null;
        return user.getLastName() + " " + user.getFirstName();
    }

    /**
     * Инициализация перед сохранением в БД
     */
    @PrePersist
    public void prePersist() {
        if (cardStatus == null) cardStatus = CardStatus.ACTIVE;
        if (balance == null) balance = BigDecimal.ZERO;
    }
}