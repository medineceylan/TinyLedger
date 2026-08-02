package com.tinyledger.service;

import com.tinyledger.domain.Transaction;
import com.tinyledger.domain.TransactionType;
import com.tinyledger.exception.BadRequestException;
import com.tinyledger.port.TransactionService;
import com.tinyledger.port.TransactionStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionStorage transactionStore;

    @Override
    public void recordTransaction(Transaction transaction) {
        Transaction transactionToStore = Transaction.builder()
                .transactionId(UUID.randomUUID())
                .transactionTime(LocalDateTime.now())
                .amount(transaction.amount())
                .transactionType(transaction.transactionType())
                .build();

        if (transactionToStore.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transaction amount must be greater than zero");
        }

        if (transactionToStore.transactionType() == TransactionType.WITHDRAWAL && transactionToStore.amount().compareTo(transactionStore.getBalance()) > 0) {
            throw new BadRequestException("There isn't enough balance to withdraw");
        }

        transactionStore.storeTransaction(transactionToStore);
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactionStore.getTransactions();
    }

    @Override
    public BigDecimal getBalance() {
        return transactionStore.getBalance();
    }

}
