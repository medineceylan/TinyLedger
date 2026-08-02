package com.tinyledger.persistence;

import com.tinyledger.domain.Transaction;
import com.tinyledger.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionInMemoryStorageTest {

    TransactionInMemoryStorage storage = new TransactionInMemoryStorage();

    @Test
    void shouldAddTransaction() {
        storage.storeTransaction(Transaction.builder().amount(BigDecimal.valueOf(100)).build());
        assertEquals(BigDecimal.valueOf(100), storage.getTransactions().get(0).amount());
    }

    @Test
    void shouldGetTransactions() {
        storage.storeTransaction(Transaction.builder().amount(BigDecimal.valueOf(100)).transactionType(TransactionType.DEPOSIT).build());
        storage.storeTransaction(Transaction.builder().amount(BigDecimal.valueOf(1)).transactionType(TransactionType.WITHDRAWAL).build());
        assertArrayEquals(new Transaction[]{Transaction.builder().amount(BigDecimal.valueOf(100)).transactionType(TransactionType.DEPOSIT).build(), Transaction.builder().amount(BigDecimal.valueOf(1)).transactionType(TransactionType.WITHDRAWAL).build()}, storage.getTransactions().toArray());
    }

    @Test
    void shouldGetTransactionsAsEmptyIfThereIsNoTransaction() {
        assertNotNull(storage.getTransactions());
        assertEquals(0, storage.getTransactions().size());
    }

    @Test
    void shouldGetBalance() {
        storage.storeTransaction(Transaction.builder().amount(BigDecimal.valueOf(100)).transactionType(TransactionType.DEPOSIT).build());
        storage.storeTransaction(Transaction.builder().amount(BigDecimal.valueOf(1)).transactionType(TransactionType.WITHDRAWAL).build());
        assertNotNull(storage.getTransactions());
        assertEquals(BigDecimal.valueOf(99), storage.getBalance());
    }
}