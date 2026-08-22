package com.cs.harin.graph.controller;

import com.cs.harin.graph.dao.TransactionDAO;
import com.cs.harin.graph.dao.request.TransactionRequest;
import com.cs.harin.graph.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TransactionResolver {

    private final TransactionService transactionService;

    @QueryMapping
    public List<TransactionDAO> getTransactions() {
        return transactionService.getTransactions();
    }

    @QueryMapping
    public List<TransactionDAO> getTransactionsByFilter(
            @Argument(value = "page") Integer page,
            @Argument(value = "size") Integer size,
            @Argument(value = "sort") String sortBy,
            @Argument(value = "fundCode") Integer fundCode
    ) {
        // currently service only supports unpaged fetch; expand implementation if needed
        return transactionService.getTransactions();
    }

    @QueryMapping
    public TransactionDAO getTransactionById(@Argument String transactionId) {
        return transactionService.getTransactionById(transactionId);
    }

    @MutationMapping(name = "createTransaction")
    public TransactionDAO createTransaction(@Argument TransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @MutationMapping(name = "updateTransaction")
    public TransactionDAO updateTransaction(
            @Argument String transactionId,
            @Argument TransactionRequest transactionRequest
    ) {
        return transactionService.updateTransaction(transactionId, transactionRequest);
    }

    @MutationMapping(name = "deleteTransactionById")
    public Boolean deleteTransactionById(@Argument String transactionId) {
        transactionService.deleteTransactionById(transactionId);
        return true;
    }

}
