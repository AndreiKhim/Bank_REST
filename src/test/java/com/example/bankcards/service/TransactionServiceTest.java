package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.entity.enums.TransactionStatus;
import com.example.bankcards.entity.enums.TransactionType;
import com.example.bankcards.exception.InvalidTransactionException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

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

        user = new User();
        user.setId(1L);
        user.setRole(Role.USER);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("1234567890");
        user.setPassword("password");

        sourceCard = new Card();
        sourceCard.setId(1L);
        sourceCard.setUser(user);
        sourceCard.setBalance(BigDecimal.valueOf(1000));

        destinationCard = new Card();
        destinationCard.setId(2L);
        destinationCard.setUser(user);
        destinationCard.setBalance(BigDecimal.valueOf(500));
    }

    @Test
    void testTransferSuccess() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(200), "Payment");

        when(cardRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(destinationCard));

        TransactionResponse response = TransactionResponse.builder()
                .status(TransactionStatus.SUCCESS)
                .amount(BigDecimal.valueOf(200))
                .type(TransactionType.TRANSFER)
                .build();

        when(transactionMapper.toResponse(any())).thenReturn(response);

        TransactionResponse result = transactionService.transfer(user, request);

        assertEquals(TransactionStatus.SUCCESS, result.getStatus());
        assertEquals(BigDecimal.valueOf(800), sourceCard.getBalance());
        assertEquals(BigDecimal.valueOf(700), destinationCard.getBalance());

        verify(cardRepository).save(sourceCard);
        verify(cardRepository).save(destinationCard);
        verify(transactionRepository).save(any());
    }

    @Test
    void testTransferNegativeAmountThrows() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(-50), "Invalid");

        when(cardRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(destinationCard));

        assertThrows(InvalidTransactionException.class,
                () -> transactionService.transfer(user, request));
    }

    @Test
    void testTransferInsufficientBalance() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(2000), "Too much");

        when(cardRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(destinationCard));

        TransactionResponse response = TransactionResponse.builder()
                .status(TransactionStatus.FAILED)
                .amount(BigDecimal.valueOf(2000))
                .type(TransactionType.TRANSFER)
                .build();

        when(transactionMapper.toResponse(any())).thenReturn(response);

        TransactionResponse result = transactionService.transfer(user, request);

        assertEquals(TransactionStatus.FAILED, result.getStatus());
        assertEquals(BigDecimal.valueOf(1000), sourceCard.getBalance()); // не списалось
        assertEquals(BigDecimal.valueOf(500), destinationCard.getBalance()); // не зачислилось
    }
}
