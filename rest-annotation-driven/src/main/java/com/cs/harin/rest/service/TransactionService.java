package com.cs.harin.rest.service;

import com.cs.harin.rest.dao.TransactionDAO;
import com.cs.harin.rest.dao.request.TransactionRequest;
import com.cs.harin.rest.entity.Transaction;
import com.cs.harin.rest.entity.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final Clock systemClock;

    public List<TransactionDAO> getTransactions() {
        log.info("Retrieving all transactions");
        var transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        return List.of(toTransactionDAO(transaction));
    }

    public TransactionDAO getTransactionById(String id) {
        log.info("Retrieving transaction by id: {}", id);
        var transaction = new Transaction();
        transaction.setId(UUID.fromString(id));

        return toTransactionDAO(transaction);
    }

    public TransactionDAO createTransaction(TransactionRequest request) {
        var transactionId = UUID.randomUUID();
        log.info("Creating transaction with id: {}", transactionId);

        var transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setStatus(TransactionStatus.valueOf(request.getStatus()));
        transaction.setFundCode(request.getFundCode());
        transaction.setAccount(BigInteger.valueOf(request.getAccount()));
        transaction.setDealer(request.getDealer());
        transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
        transaction.setCreatedTimestamp(ZonedDateTime.now(systemClock));
        transaction.setUpdatedTimestamp(ZonedDateTime.now(systemClock));

        return toTransactionDAO(transaction);
    }

    public TransactionDAO updateTransaction(String transactionId, TransactionRequest request) {
        log.info("Updating transaction by id: {}", transactionId);

        var transaction = new Transaction();
        transaction.setId(UUID.fromString(transactionId));
        transaction.setStatus(TransactionStatus.valueOf(request.getStatus()));
        transaction.setFundCode(request.getFundCode());
        transaction.setAccount(BigInteger.valueOf(request.getAccount()));
        transaction.setDealer(request.getDealer());
        transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
        transaction.setCreatedTimestamp(ZonedDateTime.now(systemClock).minusDays(1));
        transaction.setUpdatedTimestamp(ZonedDateTime.now(systemClock));

        return toTransactionDAO(transaction);
    }

    public void deleteTransactionById(String transactionId) {
        log.info("Deleting transaction by id: {}", transactionId);
    }

    private TransactionDAO toTransactionDAO(Transaction transaction) {
        var dao = new TransactionDAO();
        dao.setId(transaction.getId().toString());

        return dao;
    }

}
