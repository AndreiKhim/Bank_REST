package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с картами (только ADMIN).
 */
@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final CardService cardService;
    private final UserService userService;

    /**
     * Получить все карты с пагинацией.
     */
    @GetMapping
    public ResponseEntity<Page<CardResponse>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(cardService.getAllCards(PageRequest.of(page, size)));
    }

    /**
     * Получить карты конкретного пользователя по его ID.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CardResponse>> getUserCardsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        User user = userService.getUserById(userId);
        Page<CardResponse> cards = cardService.getUserCards(user, PageRequest.of(page, size));
        return ResponseEntity.ok(cards);
    }

    /**
     * Создать карту для пользователя.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<CardResponse> createCard(
            @PathVariable Long userId,
            @RequestBody CreateCardRequest request
    ) {
        User user = userService.getUserById(userId);
        CardResponse response = cardService.createCard(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Заблокировать карту по ID.
     */
    @PutMapping("/{cardId}/block")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.blockCard(cardId));
    }

    /**
     * Активировать карту по ID.
     */
    @PutMapping("/{cardId}/activate")
    public ResponseEntity<CardResponse> activateCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.activateCard(cardId));
    }

    /**
     * Удалить карту по ID.
     */
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}
