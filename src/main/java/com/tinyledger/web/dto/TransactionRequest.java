package com.tinyledger.web.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull
        BigDecimal transactionAmount,
        @NotNull
        TransactionTypeDto transactionType) {
}
