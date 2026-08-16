package com.main.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.main.dto.TransactionPageResponse;
import com.main.dto.TransactionRequest;
import com.main.dto.TransactionResponse;
import com.main.dto.TransactionSummaryResponse;
import com.main.security.CustomUserDetails;
import com.main.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction( @Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        TransactionResponse response =
                transactionService.createTransaction(
                        request,
                        userId
                );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<TransactionPageResponse> getTransactions(
            @RequestParam(required = false)
            String type,
            @RequestParam(required = false)
            String category,
            @RequestParam(required = false)
            LocalDate date,
            @RequestParam(defaultValue = "ALL")
            String period,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "10")
            int size,
            @RequestParam(defaultValue = "transactionDate")
            String sortBy,
            @RequestParam(defaultValue = "desc")
            String direction,
            @AuthenticationPrincipal
            CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();

        TransactionPageResponse response =
                transactionService.getAllTransactions(
                        userId,
                        type,
                        category,
                        date,
                        period,
                        page,
                        size,
                        sortBy,
                        direction
                );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        TransactionResponse response =
                transactionService.updateTransaction(
                        id,
                        userId,
                        request
                );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction( @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        transactionService.deleteTransaction(
                id,
                userId
        );
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryResponse> getSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        TransactionSummaryResponse summary =transactionService.getSummary(userId);
        return ResponseEntity.ok(summary);
    }
    
}