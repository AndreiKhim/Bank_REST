package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMapper {

    private final EncryptionService encryptionService;

    /**
     * Преобразует сущность Card в DTO для отображения пользователю.
     * Автоматически расшифровывает и маскирует номер карты.
     */
    public CardResponse toResponse(Card card) {
        if (card == null) return null;

        CardResponse response = new CardResponse();
        response.setId(card.getId());

        // Декодируем и маскируем
        String plainNumber = encryptionService.decrypt(card.getCardNumberEncrypted());
        response.setCardNumber(encryptionService.maskCardNumber(plainNumber));

        // Владелец карты (формируется из User внутри Card#getCardHolder)
        response.setCardHolder(card.getCardHolder());

        response.setExpirationDate(card.getExpirationDate());
        response.setCardStatus(card.getCardStatus() != null ? card.getCardStatus().name() : null);
        response.setBalance(card.getBalance());
        response.setBlockRequested(card.isBlockRequested());

        return response;
    }
}