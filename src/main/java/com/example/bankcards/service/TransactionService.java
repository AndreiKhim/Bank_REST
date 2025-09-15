package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.TransactionStatus;
import com.example.bankcards.entity.enums.TransactionType;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InvalidTransactionException;
import com.example.bankcards.mapper.TransactionMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final TransactionMapper transactionMapper;

    /**
     * Перевод между своими картами
     */
    @Transactional
    public TransactionResponse transfer(User user, TransferRequest request) {
        Card sourceCard = cardRepository.findByIdAndUser(request.getSourceCardId(), user)
                .orElseThrow(() -> new CardNotFoundException("Source card not found or does not belong to user"));
        Card destinationCard = cardRepository.findByIdAndUser(request.getDestinationCardId(), user)
                .orElseThrow(() -> new CardNotFoundException("Destination card not found or does not belong to user"));

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be greater than zero");
        }

        Transaction transaction = new Transaction();
        transaction.setSourceCard(sourceCard);
        transaction.setDestinationCard(destinationCard);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.TRANSFER);

        if (sourceCard.getBalance().compareTo(amount) < 0) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setDescription("Недостаточно средств");
            transactionRepository.save(transaction);
            return transactionMapper.toResponse(transaction);
        }

        // Списание и зачисление
        sourceCard.setBalance(sourceCard.getBalance().subtract(amount));
        destinationCard.setBalance(destinationCard.getBalance().add(amount));

        cardRepository.save(sourceCard);
        cardRepository.save(destinationCard);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Перевод выполнен");
        transactionRepository.save(transaction);

        return transactionMapper.toResponse(transaction);
    }

    /**
     * Получение всех транзакций по всем картам пользователя с пагинацией.
     */
    public Page<TransactionResponse> getTransactionsForUser(User user, Pageable pageable) {
        return transactionRepository
                .findBySourceCardUserOrDestinationCardUser(user, user, pageable)
                .map(transactionMapper::toResponse);
    }
}