package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private CardService cardService;

    private User user;
    private CardRequest cardRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("Ivanov", "Ivan", "ivan@mail.com", "1234567890", "password", null);
        cardRequest = new CardRequest();
        cardRequest.setCardNumber("1234567812345678");
        cardRequest.setBalance(new BigDecimal("1000"));
        cardRequest.setExpirationDate(LocalDate.now().plusYears(3));
    }

    @Test
    void testCreateCard() {
        Card card = new Card();
        card.setId(1L);
        card.setBalance(cardRequest.getBalance());
        card.setCardStatus(CardStatus.ACTIVE);
        card.setUser(user);
        card.setExpirationDate(cardRequest.getExpirationDate());
        card.setCardNumberEncrypted("encrypted");

        when(encryptionService.encrypt(anyString())).thenReturn("encrypted");
        when(encryptionService.decrypt("encrypted")).thenReturn(cardRequest.getCardNumber());
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toResponse(card, cardRequest.getCardNumber())).thenReturn(new CardResponse());

        CardResponse response = cardService.createCard(user, cardRequest);

        assertNotNull(response);
        verify(cardRepository, times(1)).save(any(Card.class));
        verify(encryptionService).encrypt(cardRequest.getCardNumber());
        verify(encryptionService).decrypt("encrypted");
    }

    @Test
    void testBlockCard() {
        Card card = new Card();
        card.setId(1L);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setCardNumberEncrypted("encrypted");

        when(cardRepository.findById(1L)).thenReturn(java.util.Optional.of(card));
        when(encryptionService.decrypt("encrypted")).thenReturn("1234567812345678");
        when(cardMapper.toResponse(card, "1234567812345678")).thenReturn(new CardResponse());
        when(cardRepository.save(card)).thenReturn(card);

        CardResponse response = cardService.blockCard(1L);

        assertNotNull(response);
        assertEquals(CardStatus.BLOCKED, card.getCardStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void testGetCardBalance() {
        Card card = new Card();
        card.setId(1L);
        card.setBalance(new BigDecimal("1500"));
        card.setUser(user);

        when(cardRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.of(card));

        BigDecimal balance = cardService.getCardBalance(1L, user);

        assertEquals(new BigDecimal("1500"), balance);
    }

    @Test
    void testRequestBlockCard() {
        Card card = new Card();
        card.setId(1L);
        card.setBlockRequested(false);
        card.setCardNumberEncrypted("encrypted");
        card.setUser(user);

        when(cardRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.of(card));
        when(encryptionService.decrypt("encrypted")).thenReturn("1234567812345678");
        when(cardMapper.toResponse(card, "1234567812345678")).thenReturn(new CardResponse());
        when(cardRepository.save(card)).thenReturn(card);

        CardResponse response = cardService.requestBlockCard(1L, user);

        assertNotNull(response);
        assertTrue(card.isBlockRequested());
        verify(cardRepository).save(card);
    }

    @Test
    void testGetAllCards() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNumberEncrypted("encrypted");

        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(encryptionService.decrypt("encrypted")).thenReturn("1234567812345678");
        when(cardMapper.toResponse(card, "1234567812345678")).thenReturn(new CardResponse());

        Page<CardResponse> result = cardService.getAllCards(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetUserCardsByStatus() {
        Card card = new Card();
        card.setId(1L);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setCardNumberEncrypted("encrypted");

        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findByUserAndCardStatus(user, CardStatus.ACTIVE, PageRequest.of(0, 10)))
                .thenReturn(page);
        when(encryptionService.decrypt("encrypted")).thenReturn("1234567812345678");
        when(cardMapper.toResponse(card, "1234567812345678")).thenReturn(new CardResponse());

        Page<CardResponse> result = cardService.getUserCardsByStatus(user, CardStatus.ACTIVE, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findByUserAndCardStatus(user, CardStatus.ACTIVE, PageRequest.of(0, 10));
    }

}
