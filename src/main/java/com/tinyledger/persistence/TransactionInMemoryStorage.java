package com.tinyledger.persistence;

import com.tinyledger.domain.Transaction;
import com.tinyledger.domain.TransactionType;
import com.tinyledger.port.TransactionStorage;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionInMemoryStorage implements TransactionStorage {

    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void storeTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    @Override
    public BigDecimal getBalance() {
        return transactions.stream()
                .map(t -> t.transactionType() == TransactionType.DEPOSIT ? t.amount() : t.amount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
