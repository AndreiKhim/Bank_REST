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
import com.example.bankcards.entity.enums.CardStatus;

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
                .orElseThrow(() -> new CardNotFoundException("Исходная карта не найдена или не принадлежит пользователю"));
        Card destinationCard = cardRepository.findByIdAndUser(request.getDestinationCardId(), user)
                .orElseThrow(() -> new CardNotFoundException("Карта получателя не найдена или не принадлежит пользователю"));

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Сумма перевода должна быть больше нуля");
        }

        // ================== Проверка статусов ==================
        if (sourceCard.getCardStatus() != CardStatus.ACTIVE || destinationCard.getCardStatus() != CardStatus.ACTIVE) {
            Transaction failedTx = new Transaction(
                    sourceCard,
                    destinationCard,
                    amount,
                    TransactionType.TRANSFER,
                    TransactionStatus.FAILED,
                    "Операция отклонена: одна из карт заблокирована или неактивна"
            );
            transactionRepository.save(failedTx);
            return transactionMapper.toResponse(failedTx);
        }

        // ================== Проверка баланса ==================
        if (sourceCard.getBalance().compareTo(amount) < 0) {
            Transaction failedTx = new Transaction(
                    sourceCard,
                    destinationCard,
                    amount,
                    TransactionType.TRANSFER,
                    TransactionStatus.FAILED,
                    "Недостаточно средств"
            );
            transactionRepository.save(failedTx);
            return transactionMapper.toResponse(failedTx);
        }

        // ================== Успешный перевод ==================
        sourceCard.setBalance(sourceCard.getBalance().subtract(amount));
        destinationCard.setBalance(destinationCard.getBalance().add(amount));

        cardRepository.save(sourceCard);
        cardRepository.save(destinationCard);

        Transaction successTx = new Transaction(
                sourceCard,
                destinationCard,
                amount,
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS,
                request.getDescription() != null ? request.getDescription() : "Перевод выполнен"
        );
        transactionRepository.save(successTx);

        return transactionMapper.toResponse(successTx);
    }


    /**
     * Получение всех транзакций по всем картам пользователя (USER).
     */
    public Page<TransactionResponse> getTransactionsForUser(User user, Pageable pageable) {
        return transactionRepository
                .findBySourceCardUserOrDestinationCardUser(user, user, pageable)
                .map(transactionMapper::toResponse);
    }

    /**
     * Получение всех транзакций в системе (ADMIN).
     */
    public Page<TransactionResponse> getAllTransactions(Pageable pageable) {
        return transactionRepository
                .findAll(pageable)
                .map(transactionMapper::toResponse);
    }
}
