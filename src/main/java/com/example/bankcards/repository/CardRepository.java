package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface CardRepository extends JpaRepository<Card, Long> {

    // Все карты пользователя
    Page<Card> findByUser(User user, Pageable pageable);

    // Все карты пользователя с определённым статусом
    Page<Card> findByUserAndCardStatus(User user, CardStatus status, Pageable pageable);

    // Пагинация
    Page<Card> findAll(Pageable pageable);

    // Проверка уникальности номера карты (по зашифрованному номеру)
    boolean existsByCardNumberEncrypted(String cardNumberEncrypted);

    // Поиск по зашифрованному номеру
    Optional<Card> findByCardNumberEncrypted(String cardNumberEncrypted);

    // Поиск карты по id и пользователю (для ограничения доступа)
    Optional<Card> findByIdAndUser(Long id, User user);

    // Поиск всех карт, срок действия которых истёк
    List<Card> findAllByExpirationDateBefore(LocalDate date);

    // Поиск активной карты по зашифрованному номеру (для переводов)
    Optional<Card> findByCardNumberEncryptedAndCardStatus(String cardNumberEncrypted, CardStatus status);
}