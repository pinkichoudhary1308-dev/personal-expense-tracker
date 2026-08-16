package com.main.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;

    private String type;

    private String category;

    private BigDecimal amount;

    private String description;

    private LocalDate transactionDate;

    private LocalDateTime createdAt;
}
