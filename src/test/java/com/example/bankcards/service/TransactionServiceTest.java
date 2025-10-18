package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.TransactionStatus;
import com.example.bankcards.entity.enums.TransactionType;
import com.example.bankcards.exception.InvalidTransactionException;
import com.example.bankcards.mapper.TransactionMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Card sourceCard;
    private Card destinationCard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = TestDataFactory.createUser(1L, "test@mail.com");
        sourceCard = TestDataFactory.createCard(1L, user, new BigDecimal("1000.00"));
        destinationCard = TestDataFactory.createCard(2L, user, new BigDecimal("500.00"));
    }

    @Test
    void transfer_success() {
        TransferRequest request = new TransferRequest();
        request.setSourceCardId(sourceCard.getId());
        request.setDestinationCardId(destinationCard.getId());
        request.setAmount(new BigDecimal("200.00"));

        when(cardRepository.findByIdAndUser(sourceCard.getId(), user)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findByIdAndUser(destinationCard.getId(), user)).thenReturn(Optional.of(destinationCard));

        TransactionResponse responseMock = TransactionResponse.builder()
                .status(TransactionStatus.SUCCESS.name())
                .amount(request.getAmount())
                .build();

        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(responseMock);

        TransactionResponse result = transactionService.transfer(user, request);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.SUCCESS.name());
        assertThat(sourceCard.getBalance()).isEqualByComparingTo("800.00");
        assertThat(destinationCard.getBalance()).isEqualByComparingTo("700.00");

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transfer_insufficientFunds_shouldReturnFailedTransaction() {
        TransferRequest request = new TransferRequest();
        request.setSourceCardId(sourceCard.getId());
        request.setDestinationCardId(destinationCard.getId());
        request.setAmount(new BigDecimal("2000.00"));

        when(cardRepository.findByIdAndUser(sourceCard.getId(), user)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findByIdAndUser(destinationCard.getId(), user)).thenReturn(Optional.of(destinationCard));

        TransactionResponse failedResponse = TransactionResponse.builder()
                .status(TransactionStatus.FAILED.name())
                .description("Недостаточно средств")
                .build();

        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(failedResponse);

        TransactionResponse result = transactionService.transfer(user, request);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED.name());
        assertThat(result.getDescription()).contains("Недостаточно средств");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transfer_blockedCard_shouldReturnFailedTransaction() {
        sourceCard.setCardStatus(CardStatus.BLOCKED);

        TransferRequest request = new TransferRequest();
        request.setSourceCardId(sourceCard.getId());
        request.setDestinationCardId(destinationCard.getId());
        request.setAmount(new BigDecimal("100.00"));

        when(cardRepository.findByIdAndUser(sourceCard.getId(), user)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findByIdAndUser(destinationCard.getId(), user)).thenReturn(Optional.of(destinationCard));

        TransactionResponse failedResponse = TransactionResponse.builder()
                .status(TransactionStatus.FAILED.name())
                .description("Операция отклонена: одна из карт заблокирована или неактивна")
                .build();

        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(failedResponse);

        TransactionResponse result = transactionService.transfer(user, request);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED.name());
        assertThat(result.getDescription()).contains("заблокирована");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transfer_negativeAmount_shouldThrowInvalidTransactionException() {
        TransferRequest request = new TransferRequest();
        request.setSourceCardId(sourceCard.getId());
        request.setDestinationCardId(destinationCard.getId());
        request.setAmount(BigDecimal.ZERO);

        when(cardRepository.findByIdAndUser(sourceCard.getId(), user)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findByIdAndUser(destinationCard.getId(), user)).thenReturn(Optional.of(destinationCard));

        assertThatThrownBy(() -> transactionService.transfer(user, request))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Сумма перевода должна быть больше нуля");
    }

    @Test
    void getTransactionsForUser_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 5);
        Transaction tx = TestDataFactory.createTransaction(sourceCard, destinationCard, BigDecimal.valueOf(100));

        TransactionResponse mappedResponse = TransactionResponse.builder()
                .id(1L)
                .status(TransactionStatus.SUCCESS.name())
                .build();

        when(transactionRepository.findBySourceCardUserOrDestinationCardUser(user, user, pageable))
                .thenReturn(new PageImpl<>(List.of(tx)));
        when(transactionMapper.toResponse(tx)).thenReturn(mappedResponse);

        Page<TransactionResponse> result = transactionService.getTransactionsForUser(user, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(TransactionStatus.SUCCESS.name());
        verify(transactionRepository, times(1))
                .findBySourceCardUserOrDestinationCardUser(user, user, pageable);
    }

    @Test
    void getAllTransactions_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Transaction tx = TestDataFactory.createTransaction(sourceCard, destinationCard, BigDecimal.valueOf(100));

        TransactionResponse mappedResponse = TransactionResponse.builder()
                .id(1L)
                .status(TransactionStatus.SUCCESS.name())
                .build();
        when(transactionRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(tx)));
        when(transactionMapper.toResponse(tx)).thenReturn(mappedResponse);

        Page<TransactionResponse> result = transactionService.getAllTransactions(pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(TransactionStatus.SUCCESS.name());
        verify(transactionRepository, times(1)).findAll(pageable);
    }
}
