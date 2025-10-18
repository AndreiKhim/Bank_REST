package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InvalidTransactionException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
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
    private final TransactionRepository transactionRepository;

    @Value("${app.card.default.expiration-years}")
    private int cardExpirationYears;

    // ====== Админские методы ======

    public CardResponse createCard(User user, CreateCardRequest request) {
        String encryptedNumber = encryptionService.encrypt(request.getCardNumber());

        Card card = new Card(
                encryptedNumber,
                LocalDate.now().plusYears(cardExpirationYears),
                CardStatus.ACTIVE,
                request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO,
                false,
                user
        );

        Card savedCard = cardRepository.save(card);
        return cardMapper.toResponse(savedCard);
    }

    public CardResponse activateCard(Long cardId) {
        Card card = getCardById(cardId);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setBlockRequested(false);
        return cardMapper.toResponse(cardRepository.save(card));
    }

    public CardResponse blockCard(Long cardId) {
        Card card = getCardById(cardId);
        card.setCardStatus(CardStatus.BLOCKED);
        card.setBlockRequested(false);
        return cardMapper.toResponse(cardRepository.save(card));
    }

    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));

        // Проверяем, есть ли связанные транзакции
        boolean hasTransactions =
                transactionRepository.existsBySourceCard(card) ||
                        transactionRepository.existsByDestinationCard(card);

        if (hasTransactions) {
            throw new InvalidTransactionException("Нельзя удалить карту: по ней есть связанные транзакции");
        }

        cardRepository.delete(card);
    }

    public Page<CardResponse> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(cardMapper::toResponse);
    }

    // ====== Методы пользователя ======

    public Page<CardResponse> getUserCards(User user, Pageable pageable) {
        return cardRepository.findByUser(user, pageable)
                .map(cardMapper::toResponse);
    }

    public CardResponse requestBlockCard(Long cardId, User user) {
        Card card = getCardByIdAndUser(cardId, user);
        card.setBlockRequested(true);
        return cardMapper.toResponse(cardRepository.save(card));
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
