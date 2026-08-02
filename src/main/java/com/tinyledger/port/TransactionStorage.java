package com.tinyledger.port;

import com.tinyledger.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionStorage {
    void storeTransaction(Transaction transaction);

    List<Transaction> getTransactions();

    BigDecimal getBalance();
}
