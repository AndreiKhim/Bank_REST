package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
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


    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionResponse> transfer(
            Authentication auth,
            @RequestBody TransferRequest request) {
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        TransactionResponse response = transactionService.transfer(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(transactionService.getAllTransactions(PageRequest.of(page, size)));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByUser(
           @PathVariable Long userId,
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "10") int size
    ) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(transactionService.getTransactionsForUser(user, PageRequest.of(page, size)));
    }
}
