package com.tinyledger.web;

import com.tinyledger.domain.Transaction;
import com.tinyledger.domain.TransactionType;
import com.tinyledger.web.dto.TransactionHistoryDto;
import com.tinyledger.web.dto.TransactionRequest;
import com.tinyledger.web.dto.TransactionTypeDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionMapper {

    public Transaction map(TransactionRequest transactionDto) {
        return Transaction.builder().amount(transactionDto.transactionAmount()).transactionType(getTransactionType(transactionDto.transactionType())).build();
    }

    private TransactionType getTransactionType(TransactionTypeDto transactionType) {
        return TransactionType.valueOf(transactionType.name());
    }

    private TransactionTypeDto getTransactionType(TransactionType transactionType) {
        return TransactionTypeDto.valueOf(transactionType.name());
    }

    public List<TransactionHistoryDto> mapTransactionsToTransactionHistories(List<Transaction> transactions) {
        List<TransactionHistoryDto> transactionHistoryDtos = new ArrayList<>();
        transactions.forEach(transaction -> {
            transactionHistoryDtos.add(TransactionHistoryDto.builder().transactionAmount(transaction.amount()).transactionTime(transaction.transactionTime()).transactionType(getTransactionType(transaction.transactionType())).build());

        });

        return transactionHistoryDtos;
    }
}
