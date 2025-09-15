package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    // Получение транзакций текущего пользователя
    @GetMapping("/my")
    public ResponseEntity<Page<TransactionResponse>> getMyTransactions(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<TransactionResponse> transactions =
                transactionService.getTransactionsForUser(user, PageRequest.of(page, size));

        return ResponseEntity.ok(transactions);
    }

    // Перевод между своими картами
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            Authentication auth,
            @RequestBody TransferRequest request
    ) {
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        TransactionResponse response = transactionService.transfer(user, request);

        return ResponseEntity.ok(response);
    }
}