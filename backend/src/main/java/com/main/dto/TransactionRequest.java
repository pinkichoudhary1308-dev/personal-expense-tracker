package com.main.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {

	@Pattern(regexp = "INCOME|EXPENSE", message = "Type must be INCOME or EXPENSE")
	@NotBlank(message = "Type is required")
	private String type;

	@NotBlank(message = "Category is required")
	@Pattern(
	    regexp = "^[a-zA-Z]+( [a-zA-Z]+)*$",
	    message = "Category must contain words only"
	)
	private String category;

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
	private BigDecimal amount;

	private String description;

	@NotNull(message = "Transaction date is required")
	private LocalDate transactionDate;
}