package com.tinyledger.web;

import com.tinyledger.domain.Transaction;
import com.tinyledger.domain.TransactionType;
import com.tinyledger.port.TransactionService;
import com.tinyledger.web.dto.TransactionHistoryDto;
import com.tinyledger.web.dto.TransactionRequest;
import com.tinyledger.web.dto.TransactionTypeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LedgerTransactionControllerTest {

    @Mock
    TransactionMapper mapper;

    @Mock
    TransactionService service;

    @InjectMocks
    LedgerTransactionController controller;

    @Test
    void shouldMapAndRecordTransactionThenReturnAccepted() {
        TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(100), TransactionTypeDto.DEPOSIT);
        Transaction transaction = getTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100));
        given(mapper.map(request)).willReturn(transaction);

        ResponseEntity<Void> response = controller.recordTransactions(request);

        verify(service).recordTransaction(transaction);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void shouldReturnTransactionHistoryFromService() {
        Transaction transaction = getTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100));
        TransactionHistoryDto historyDto = TransactionHistoryDto.builder()
                .transactionAmount(BigDecimal.valueOf(100))
                .transactionType(TransactionTypeDto.DEPOSIT)
                .transactionTime(transaction.transactionTime())
                .build();
        given(service.getTransactions()).willReturn(List.of(transaction));
        given(mapper.mapTransactionsToTransactionHistories(List.of(transaction))).willReturn(List.of(historyDto));

        ResponseEntity<List<TransactionHistoryDto>> response = controller.getTransactions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(historyDto), response.getBody());
    }

    @Test
    void shouldReturnEmptyTransactionHistoryWhenNoneRecorded() {
        given(service.getTransactions()).willReturn(List.of());
        given(mapper.mapTransactionsToTransactionHistories(List.of())).willReturn(List.of());

        ResponseEntity<List<TransactionHistoryDto>> response = controller.getTransactions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    void shouldReturnBalanceFromService() {
        given(service.getBalance()).willReturn(BigDecimal.valueOf(60));

        ResponseEntity<BigDecimal> response = controller.getBalance();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(60), response.getBody());
    }

    private Transaction getTransaction(TransactionType type, BigDecimal amount) {
        return Transaction.builder()
                .transactionId(UUID.randomUUID())
                .transactionType(type)
                .transactionTime(LocalDateTime.now())
                .amount(amount)
                .build();
    }
}
