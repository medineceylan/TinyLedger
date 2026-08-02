package com.tinyledger.web.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionHistoryDto(
        BigDecimal transactionAmount,
        TransactionTypeDto transactionType,
        LocalDateTime transactionTime
) {
};
