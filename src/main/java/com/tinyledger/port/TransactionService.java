package com.tinyledger.port;

import com.tinyledger.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    void recordTransaction(Transaction transaction);

    List<Transaction> getTransactions();

    BigDecimal getBalance();
}
