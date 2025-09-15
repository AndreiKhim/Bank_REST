package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final UserRepository userRepository;
    private final UserService userService;


    // =================== Админ ===================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardResponse>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(cardService.getAllCards(PageRequest.of(page, size)));
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> createCard(
            @PathVariable Long userId,
            @RequestBody CardRequest request) {

        User user = userService.getUserById(userId); // проверка, что пользователь существует
        CardResponse response = cardService.createCard(user, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{cardId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.blockCard(cardId));
    }

    @PutMapping("/{cardId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> activateCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.activateCard(cardId));
    }

    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

    // =================== Пользователь ===================

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<CardResponse>> getMyCards(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        String email = auth.getName();

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email " + email + " не найден"));

        Page<CardResponse> cards = cardService.getUserCards(user, PageRequest.of(page, size));

        return ResponseEntity.ok(cards);
    }

    // Запрос на блокировку карты пользователем
    @PutMapping("/{cardId}/request-block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> requestBlock(
            @PathVariable Long cardId,
            Authentication auth
    ) {
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email " + auth.getName() + " не найден"));

        CardResponse response = cardService.requestBlockCard(cardId, user);
        return ResponseEntity.ok(response);
    }

    // Проверка баланса карты пользователем
    @GetMapping("/{cardId}/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable Long cardId,
            Authentication auth
    ) {
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email " + auth.getName() + " не найден"));

        BigDecimal balance = cardService.getCardBalance(cardId, user);
        return ResponseEntity.ok(balance);
    }
}

