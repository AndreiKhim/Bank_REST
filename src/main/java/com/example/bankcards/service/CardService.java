package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final EncryptionService encryptionService;

    @Value("${app.card.default.expiration-years}")
    private int cardExpirationYears;

    // ====== Админские методы ======

    public CardResponse createCard(User user, CardRequest request) {
        Card card = new Card();
        card.setUser(user);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setBalance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO);
        card.setExpirationDate(LocalDate.now().plusYears(cardExpirationYears));

        // Шифруем номер карты перед сохранением
        String encryptedNumber = encryptionService.encrypt(request.getCardNumber());
        card.setCardNumberEncrypted(encryptedNumber);

        // Сохраняем в БД
        Card savedCard = cardRepository.save(card);

        // Расшифровываем для DTO
        String plainNumber = encryptionService.decrypt(savedCard.getCardNumberEncrypted());
        return cardMapper.toResponse(savedCard, plainNumber);
    }

    public CardResponse activateCard(Long cardId) {
        Card card = getCardById(cardId);
        card.setCardStatus(CardStatus.ACTIVE);
        Card saved = cardRepository.save(card);
        return cardMapper.toResponse(saved, encryptionService.decrypt(saved.getCardNumberEncrypted()));
    }

    public CardResponse blockCard(Long cardId) {
        Card card = getCardById(cardId);
        card.setCardStatus(CardStatus.BLOCKED);
        Card saved = cardRepository.save(card);
        return cardMapper.toResponse(saved, encryptionService.decrypt(saved.getCardNumberEncrypted()));
    }

    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new CardNotFoundException("Card not found with id: " + cardId);
        }
        cardRepository.deleteById(cardId);
    }

    public Page<CardResponse> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(card -> cardMapper.toResponse(card, encryptionService.decrypt(card.getCardNumberEncrypted())));
    }

    // ====== Методы пользователя ======

    public Page<CardResponse> getUserCards(User user, Pageable pageable) {
        return cardRepository.findByUser(user, pageable)
                .map(card -> cardMapper.toResponse(card, encryptionService.decrypt(card.getCardNumberEncrypted())));
    }

    public Page<CardResponse> getUserCardsByStatus(User user, CardStatus status, Pageable pageable) {
        return cardRepository.findByUserAndCardStatus(user, status, pageable)
                .map(card -> cardMapper.toResponse(card, encryptionService.decrypt(card.getCardNumberEncrypted())));
    }

    public CardResponse requestBlockCard(Long cardId, User user) {
        Card card = getCardByIdAndUser(cardId, user);
        card.setBlockRequested(true);
        Card saved = cardRepository.save(card);
        return cardMapper.toResponse(saved, encryptionService.decrypt(saved.getCardNumberEncrypted()));
    }

    public BigDecimal getCardBalance(Long cardId, User user) {
        Card card = getCardByIdAndUser(cardId, user);
        return card.getBalance();
    }

    // ====== Вспомогательные методы ======

    public Card getCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
    }

    public Card getCardByIdAndUser(Long id, User user) {
        return cardRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CardNotFoundException("Card not found for user"));
    }
}