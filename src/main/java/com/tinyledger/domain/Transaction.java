package com.tinyledger.domain;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record Transaction(
        UUID transactionId,
        BigDecimal amount,
        TransactionType transactionType,
        LocalDateTime transactionTime) {
};

