package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users/me/cards")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class UserCardController {

    private final CardService cardService;
    private final UserService userService;
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<CardResponse>> getMyCards(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + auth.getName()));
        Page<CardResponse> cards = cardService.getUserCards(user, PageRequest.of(page, size));
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{cardId}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable Long cardId,
            Authentication auth) {
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email " + auth.getName() + " не найден"));
        BigDecimal balance = cardService.getCardBalance(cardId, user);
        return ResponseEntity.ok(balance);
    }

    @PutMapping("/{cardId}/request-block")
    public ResponseEntity<CardResponse> requestBlock(
            @PathVariable Long cardId,
            Authentication auth) {
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email " + auth.getName() + " не найден"));
        CardResponse response = cardService.requestBlockCard(cardId, user);
        return ResponseEntity.ok(response);
    }
}

