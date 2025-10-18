package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.InvalidTransactionException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.TestDataFactory;
import org.junit.jupiter.api.*;
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

@DisplayName("🧾 Тесты для CardService (управление банковскими картами)")
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CardService cardService;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = TestDataFactory.createUser(1L, "test@mail.com");
        card = TestDataFactory.createCard(1L, user, BigDecimal.valueOf(1000));
    }

    // ==========================================
    // 🔹 TEST GROUP 1: Создание карты
    // ==========================================
    @Nested
    @DisplayName("Создание новой карты")
    class CreateCardTests {

        @Test
        @DisplayName("✅ Должна успешно создаваться карта с зашифрованным номером")
        void shouldCreateCardSuccessfully() {
            CreateCardRequest request = new CreateCardRequest();
            request.setCardNumber("1234567890123456");
            request.setBalance(BigDecimal.valueOf(500));

            when(encryptionService.encrypt("1234567890123456")).thenReturn("encrypted");
            when(cardRepository.save(any(Card.class))).thenReturn(card);
            when(cardMapper.toResponse(any(Card.class))).thenReturn(new CardResponse());

            CardResponse response = cardService.createCard(user, request);

            assertThat(response).isNotNull();
            verify(encryptionService).encrypt("1234567890123456");
            verify(cardRepository).save(any(Card.class));
        }

        @Test
        @DisplayName("💰 Если баланс не указан — устанавливается 0 по умолчанию")
        void shouldSetZeroBalanceIfNotProvided() {
            CreateCardRequest request = new CreateCardRequest();
            request.setCardNumber("1111222233334444");

            when(encryptionService.encrypt(any())).thenReturn("encrypted");
            when(cardRepository.save(any(Card.class))).thenReturn(card);
            when(cardMapper.toResponse(any(Card.class))).thenReturn(new CardResponse());

            cardService.createCard(user, request);

            verify(cardRepository).save(argThat(c ->
                    c.getBalance().compareTo(BigDecimal.ZERO) == 0));
        }
    }

    // ==========================================
    // 🔹 TEST GROUP 2: Активация / блокировка карты
    // ==========================================
    @Nested
    @DisplayName("Изменение статуса карты (активация / блокировка)")
    class CardStatusTests {

        @Test
        @DisplayName("✅ Активация карты сбрасывает флаг blockRequested")
        void shouldActivateCardAndResetFlag() {
            card.setBlockRequested(true);
            when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
            when(cardRepository.save(any())).thenReturn(card);
            when(cardMapper.toResponse(any())).thenReturn(new CardResponse());

            cardService.activateCard(1L);

            assertThat(card.getCardStatus()).isEqualTo(CardStatus.ACTIVE);
            assertThat(card.isBlockRequested()).isFalse();
        }

        @Test
        @DisplayName("🚫 Блокировка карты также сбрасывает флаг blockRequested")
        void shouldBlockCardAndResetFlag() {
            card.setBlockRequested(true);
            when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
            when(cardRepository.save(any())).thenReturn(card);
            when(cardMapper.toResponse(any())).thenReturn(new CardResponse());

            cardService.blockCard(1L);

            assertThat(card.getCardStatus()).isEqualTo(CardStatus.BLOCKED);
            assertThat(card.isBlockRequested()).isFalse();
        }
    }

    // ==========================================
    // 🔹 TEST GROUP 3: Удаление карты
    // ==========================================
    @Nested
    @DisplayName("Удаление карты")
    class DeleteCardTests {

        @Test
        @DisplayName("✅ Успешное удаление карты без транзакций")
        void shouldDeleteCardWithoutTransactions() {
            when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
            when(transactionRepository.existsBySourceCard(card)).thenReturn(false);
            when(transactionRepository.existsByDestinationCard(card)).thenReturn(false);

            cardService.deleteCard(1L);

            verify(cardRepository).delete(card);
        }

        @Test
        @DisplayName("❌ Нельзя удалить карту, если по ней есть транзакции")
        void shouldThrowExceptionWhenCardHasTransactions() {
            when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
            when(transactionRepository.existsBySourceCard(card)).thenReturn(true);

            assertThatThrownBy(() -> cardService.deleteCard(1L))
                    .isInstanceOf(InvalidTransactionException.class)
                    .hasMessageContaining("Нельзя удалить карту");
        }
    }

    // ==========================================
    // 🔹 TEST GROUP 4: Просмотр карт пользователя
    // ==========================================
    @Nested
    @DisplayName("Просмотр карт пользователя")
    class GetCardsTests {

        @Test
        @DisplayName("✅ Возвращает список карт пользователя")
        void shouldReturnUserCards() {
            Pageable pageable = PageRequest.of(0, 5);
            when(cardRepository.findByUser(user, pageable))
                    .thenReturn(new PageImpl<>(List.of(card)));
            when(cardMapper.toResponse(card)).thenReturn(new CardResponse());

            Page<CardResponse> result = cardService.getUserCards(user, pageable);

            assertThat(result).isNotEmpty();
            verify(cardRepository).findByUser(user, pageable);
        }

        @Test
        @DisplayName("✅ Возвращает баланс карты пользователя")
        void shouldReturnCardBalance() {
            when(cardRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(card));

            BigDecimal balance = cardService.getCardBalance(1L, user);

            assertThat(balance).isEqualByComparingTo("1000.00");
        }
    }
}
