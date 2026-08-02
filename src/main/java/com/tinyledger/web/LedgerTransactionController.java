package com.tinyledger.web;

import com.tinyledger.domain.Transaction;
import com.tinyledger.port.TransactionService;
import com.tinyledger.web.dto.TransactionHistoryDto;
import com.tinyledger.web.dto.TransactionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LedgerTransactionController {

    private final TransactionMapper mapper;
    private final TransactionService service;

    @PostMapping("/transactions")
    public ResponseEntity<Void> recordTransactions(@RequestBody @Valid TransactionRequest transactionDto) {

        Transaction transaction = mapper.map(transactionDto);
        service.recordTransaction(transaction);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionHistoryDto>> getTransactions() {
        List<Transaction> transactions = service.getTransactions();

        List<TransactionHistoryDto> transactionHistoryDtos = mapper.mapTransactionsToTransactionHistories(transactions);

        return ResponseEntity.ok(transactionHistoryDtos);
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance() {
        BigDecimal balance = service.getBalance();

        return ResponseEntity.ok(balance);
    }


}
