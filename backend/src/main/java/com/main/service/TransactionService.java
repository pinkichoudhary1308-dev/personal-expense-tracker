package com.main.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.time.DayOfWeek;

import org.springframework.stereotype.Service;

import com.main.dto.TransactionRequest;
import com.main.dto.TransactionResponse;
import com.main.dto.TransactionSummaryResponse;
import com.main.entity.Transaction;
import com.main.entity.User;
import com.main.exception.ResourceNotFoundException;
import com.main.repository.TransactionRepository;
import com.main.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.main.dto.TransactionPageResponse;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionResponse createTransaction(TransactionRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        Transaction transaction = new Transaction();

        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return convertToResponse(savedTransaction);
    }

    public TransactionPageResponse getAllTransactions(
            Long userId,
            String type,
            String category,
            LocalDate date,
            String period,
            int page,
            int size,
            String sortBy,
            String direction) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(sortDirection, sortBy)
                );

        LocalDate startDate = null;
        LocalDate endDate = null;

        if (period == null || period.isBlank()) {
            period = "ALL";
        }

        switch (period.toUpperCase()) {
            case "ALL":
                break;
                
            case "WEEK":
                LocalDate today = LocalDate.now();
                startDate = today.with(DayOfWeek.MONDAY);
                endDate = today.with(DayOfWeek.SUNDAY);
                break;
                
            case "MONTH":
                LocalDate currentMonth = LocalDate.now();
                startDate = currentMonth.withDayOfMonth(1);
                endDate = currentMonth.withDayOfMonth( currentMonth.lengthOfMonth());
                break;

            case "YEAR":
                LocalDate currentYear = LocalDate.now();
                startDate = currentYear.withDayOfYear(1);
                endDate = currentYear.withDayOfYear(currentYear.lengthOfYear());
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid period. Use ALL, WEEK, MONTH or YEAR."
                );
        }

        Page<Transaction> transactionPage =
                transactionRepository.findTransactions(
                        user,
                        type,
                        category,
                        date,
                        startDate,
                        endDate,
                        pageable
                );

        List<TransactionResponse> transactions =
                transactionPage
                        .getContent()
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return new TransactionPageResponse(
                transactions,
                transactionPage.getNumber(),
                transactionPage.getSize(),
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages(),
                transactionPage.isFirst(),
                transactionPage.isLast()
        );
    }

    public TransactionResponse updateTransaction(
            Long transactionId,
            Long userId,
            TransactionRequest request) {

        Transaction transaction =
                transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found with id: "
                                        + transactionId
                        )
                );

        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Transaction not found with id: "
                            + transactionId
            );
        }

        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return convertToResponse(updatedTransaction);
    }

    public void deleteTransaction(
            Long transactionId,
            Long userId) {

        Transaction transaction =
                transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found with id: "
                                        + transactionId
                        )
                );

        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Transaction not found with id: "
                            + transactionId
            );
        }

        transactionRepository.delete(transaction);
    }

    private TransactionResponse convertToResponse(Transaction transaction) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getCreatedAt()
        );
    }
    
    public TransactionSummaryResponse getSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId)
                );
        BigDecimal totalIncome = transactionRepository.calculateTotalByType(
                        user,"INCOME");
        BigDecimal totalExpense = transactionRepository.calculateTotalByType(
                        user,"EXPENSE");
        BigDecimal balance =totalIncome.subtract(totalExpense);
        return new TransactionSummaryResponse(
                totalIncome, totalExpense,balance
        );
    }
    
}