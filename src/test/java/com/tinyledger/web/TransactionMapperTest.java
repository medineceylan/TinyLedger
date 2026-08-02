package com.tinyledger.web;

import com.tinyledger.domain.Transaction;
import com.tinyledger.domain.TransactionType;
import com.tinyledger.web.dto.TransactionHistoryDto;
import com.tinyledger.web.dto.TransactionRequest;
import com.tinyledger.web.dto.TransactionTypeDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionMapperTest {

    TransactionMapper mapper = new TransactionMapper();

    @Test
    void shouldMapDepositRequestToTransaction() {
        TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(100), TransactionTypeDto.DEPOSIT);

        Transaction transaction = mapper.map(request);

        assertEquals(BigDecimal.valueOf(100), transaction.amount());
        assertEquals(TransactionType.DEPOSIT, transaction.transactionType());
    }

    @Test
    void shouldMapWithdrawalRequestToTransaction() {
        TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(50), TransactionTypeDto.WITHDRAWAL);

        Transaction transaction = mapper.map(request);

        assertEquals(BigDecimal.valueOf(50), transaction.amount());
        assertEquals(TransactionType.WITHDRAWAL, transaction.transactionType());
    }

    @Test
    void shouldNotAssignIdentityOrTimestampWhenMappingRequest() {
        TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(10), TransactionTypeDto.DEPOSIT);

        Transaction transaction = mapper.map(request);

        assertNull(transaction.transactionId());
        assertNull(transaction.transactionTime());
    }

    @Test
    void shouldMapEmptyTransactionListToEmptyHistoryList() {
        List<TransactionHistoryDto> histories = mapper.mapTransactionsToTransactionHistories(List.of());

        assertTrue(histories.isEmpty());
    }

    @Test
    void shouldMapTransactionsToHistoriesPreservingFieldsAndOrder() {
        LocalDateTime depositTime = LocalDateTime.now().minusMinutes(5);
        LocalDateTime withdrawalTime = LocalDateTime.now();
        Transaction deposit = getTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100), depositTime);
        Transaction withdrawal = getTransaction(TransactionType.WITHDRAWAL, BigDecimal.valueOf(40), withdrawalTime);

        List<TransactionHistoryDto> histories = mapper.mapTransactionsToTransactionHistories(List.of(deposit, withdrawal));

        assertEquals(2, histories.size());
        assertEquals(BigDecimal.valueOf(100), histories.get(0).transactionAmount());
        assertEquals(TransactionTypeDto.DEPOSIT, histories.get(0).transactionType());
        assertEquals(depositTime, histories.get(0).transactionTime());
        assertEquals(BigDecimal.valueOf(40), histories.get(1).transactionAmount());
        assertEquals(TransactionTypeDto.WITHDRAWAL, histories.get(1).transactionType());
        assertEquals(withdrawalTime, histories.get(1).transactionTime());
    }

    private Transaction getTransaction(TransactionType type, BigDecimal amount, LocalDateTime time) {
        return Transaction.builder()
                .transactionId(UUID.randomUUID())
                .transactionType(type)
                .transactionTime(time)
                .amount(amount)
                .build();
    }
}
