package com.cs.harin.rest.controller;

import com.cs.harin.rest.dao.TransactionDAO;
import com.cs.harin.rest.dao.request.TransactionRequest;
import com.cs.harin.rest.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("java:S4488")
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionDAO> getTransactions() {
        return transactionService.getTransactions();
    }

    @RequestMapping(value = "/{transactionId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public TransactionDAO getTransactionById(@PathVariable String transactionId) {
        return transactionService.getTransactionById(transactionId);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDAO createTransaction(@RequestBody TransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @RequestMapping(value = "/{transactionId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public TransactionDAO updateTransaction(
            @PathVariable String transactionId,
            @RequestBody TransactionRequest request
    ) {
        return transactionService.updateTransaction(transactionId,  request);
    }

    @RequestMapping(value = "/{transactionId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransactionById(@PathVariable String transactionId) {
        transactionService.deleteTransactionById(transactionId);
    }

}
