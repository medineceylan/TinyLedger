package com.tinyledger.service;

import com.tinyledger.domain.Transaction;
import com.tinyledger.domain.TransactionType;
import com.tinyledger.exception.BadRequestException;
import com.tinyledger.port.TransactionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    TransactionStorage transactionStorage;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Test
    void shouldReturnEmptyListWhenThereAreNoTransactions() {
        given(transactionStorage.getTransactions()).willReturn(List.of());
        assertEquals(0, transactionService.getTransactions().size());
    }

    @Test
    void shouldReturnTransactionsFromStorage() {
        Transaction transaction = getTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100));
        given(transactionStorage.getTransactions()).willReturn(List.of(transaction));
        assertEquals(List.of(transaction), transactionService.getTransactions());
    }

    @Test
    void shouldStoreDepositWhenAmountIsPositive() {
        Transaction transaction = getTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100));

        transactionService.recordTransaction(transaction);

        Transaction stored = captureStoredTransaction();
        assertEquals(BigDecimal.valueOf(100), stored.amount());
        assertEquals(TransactionType.DEPOSIT, stored.transactionType());
    }

    @Test
    void shouldStoreWithdrawalWhenAmountEqualsBalance() {
        Transaction transaction = getTransaction(TransactionType.WITHDRAWAL, BigDecimal.valueOf(100));
        given(transactionStorage.getBalance()).willReturn(BigDecimal.valueOf(100));

        transactionService.recordTransaction(transaction);

        Transaction stored = captureStoredTransaction();
        assertEquals(BigDecimal.valueOf(100), stored.amount());
        assertEquals(TransactionType.WITHDRAWAL, stored.transactionType());
    }

    @Test
    void shouldAssignFreshIdentityAndTimestampWhenStoringTransaction() {
        Transaction transaction = getTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100));

        transactionService.recordTransaction(transaction);

        Transaction stored = captureStoredTransaction();
        assertNotNull(stored.transactionId());
        assertNotNull(stored.transactionTime());
    }

    @Test
    void shouldThrowExceptionAndNotStoreWhenAmountIsZero() {
        Transaction transaction = getTransaction(TransactionType.DEPOSIT, BigDecimal.ZERO);
        assertThrows(BadRequestException.class, () -> transactionService.recordTransaction(transaction));
        verify(transactionStorage, never()).storeTransaction(any());
    }

    @Test
    void shouldThrowExceptionAndNotStoreWhenAmountIsNegative() {
        Transaction transaction = getTransaction(TransactionType.WITHDRAWAL, BigDecimal.valueOf(-1));
        assertThrows(BadRequestException.class, () -> transactionService.recordTransaction(transaction));
        verify(transactionStorage, never()).storeTransaction(any());
    }

    @Test
    void shouldThrowExceptionAndNotStoreWhenWithdrawalExceedsBalance() {
        Transaction transaction = getTransaction(TransactionType.WITHDRAWAL, BigDecimal.valueOf(101));
        given(transactionStorage.getBalance()).willReturn(BigDecimal.valueOf(100));
        assertThrows(BadRequestException.class, () -> transactionService.recordTransaction(transaction));
        verify(transactionStorage, never()).storeTransaction(any());
    }

    @Test
    void shouldReturnBalanceFromStorage() {
        given(transactionStorage.getBalance()).willReturn(BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(100), transactionService.getBalance());
    }

    private Transaction getTransaction(TransactionType type, BigDecimal amount) {
        return Transaction.builder()
                .transactionType(type)
                .amount(amount)
                .build();
    }

    private Transaction captureStoredTransaction() {
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionStorage).storeTransaction(captor.capture());
        return captor.getValue();
    }
}
