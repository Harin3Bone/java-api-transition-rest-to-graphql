package com.cs.harin.rest.service;

import com.cs.harin.rest.dao.TransactionDAO;
import com.cs.harin.rest.entity.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    public List<TransactionDAO> getTransactions() {
        var transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        return List.of(toTransactionDAO(transaction));
    }

    public TransactionDAO getTransactionById(String id) {
        var transaction = new Transaction();
        transaction.setId(UUID.fromString(id));

        return toTransactionDAO(transaction);
    }

    private TransactionDAO toTransactionDAO(Transaction transaction) {
        var dao = new TransactionDAO();
        dao.setId(transaction.getId().toString());

        return dao;
    }

}
