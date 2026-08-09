package com.cs.harin.rest.controller;

import com.cs.harin.rest.dao.TransactionDAO;
import com.cs.harin.rest.dao.request.TransactionRequest;
import com.cs.harin.rest.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionDAO> getTransactions() {
        return transactionService.getTransactions();
    }

    @GetMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionDAO getTransactionById(@PathVariable String transactionId) {
        return transactionService.getTransactionById(transactionId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDAO createTransaction() {
        return null;
    }

    @PutMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionDAO createTransaction(
            @PathVariable String transactionId,
            @RequestBody TransactionRequest request
    ) {
        return transactionService.createTransaction(request);
    }

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransactionById(@PathVariable String transactionId) {
        transactionService.deleteTransactionById(transactionId);
    }

}
