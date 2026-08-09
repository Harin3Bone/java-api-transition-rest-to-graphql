package com.cs.harin.rest.controller;

import com.cs.harin.rest.dao.TransactionDAO;
import com.cs.harin.rest.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("")
    public List<TransactionDAO> getTransactions() {
        return transactionService.getTransactions();
    }

    @GetMapping("/{transactionId}")
    public TransactionDAO getTransactionById(@PathVariable String transactionId) {
        return transactionService.getTransactionById(transactionId);
    }

}
