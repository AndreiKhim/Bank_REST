package com.example.bankcards.mapper;

import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final EncryptionService encryptionService;

    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) return null;

        return TransactionResponse.builder()
                .id(transaction.getId())
                .sourceCardMasked(transaction.getSourceCard() != null
                        ? encryptionService.maskCardNumber(
                        encryptionService.decrypt(transaction.getSourceCard().getCardNumberEncrypted()))
                        : null)
                .destinationCardMasked(transaction.getDestinationCard() != null
                        ? encryptionService.maskCardNumber(
                        encryptionService.decrypt(transaction.getDestinationCard().getCardNumberEncrypted()))
                        : null)
                .amount(transaction.getAmount())
                .type(transaction.getType() != null ? transaction.getType().name() : null)
                .status(transaction.getStatus() != null ? transaction.getStatus().name() : null)
                .description(transaction.getDescription())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}


