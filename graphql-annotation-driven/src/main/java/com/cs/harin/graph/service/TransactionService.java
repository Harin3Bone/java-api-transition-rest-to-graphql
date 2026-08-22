package com.cs.harin.graph.service;

import com.cs.harin.graph.dao.TransactionDAO;
import com.cs.harin.graph.dao.request.TransactionRequest;
import com.cs.harin.graph.entity.Transaction;
import com.cs.harin.graph.entity.TransactionStatus;
import com.cs.harin.graph.exception.NotFoundException;
import com.cs.harin.graph.repository.TransactionRepository;
import com.cs.harin.graph.util.DateFormatUtil;
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

    private final TransactionRepository transactionRepository;
    private final Clock systemClock;

    public List<TransactionDAO> getTransactions() {
        log.info("Retrieving all transactions");

        return transactionRepository.findAll()
                .stream()
                .map(this::toTransactionDAO)
                .toList();
    }

    public TransactionDAO getTransactionById(String id) {
        log.info("Retrieving transaction by id: {}", id);
        var transaction = transactionRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(id));

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

        transactionRepository.save(transaction);

        return toTransactionDAO(transaction);
    }

    public TransactionDAO updateTransaction(String id, TransactionRequest request) {
        log.info("Updating transaction by id: {}", id);

        var transaction = transactionRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(id));
        transaction.setStatus(TransactionStatus.valueOf(request.getStatus()));
        transaction.setFundCode(request.getFundCode());
        transaction.setAccount(BigInteger.valueOf(request.getAccount()));
        transaction.setDealer(request.getDealer());
        transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
        transaction.setUpdatedTimestamp(ZonedDateTime.now(systemClock));

        transactionRepository.save(transaction);

        return toTransactionDAO(transaction);
    }

    public void deleteTransactionById(String id) {
        log.info("Deleting transaction by id: {}", id);
        transactionRepository.deleteById(UUID.fromString(id));
    }

    private TransactionDAO toTransactionDAO(Transaction transaction) {
        var dao = new TransactionDAO();
        dao.setId(transaction.getId().toString());
        dao.setCreatedTimestamp(DateFormatUtil.zonedDateTimeToString(transaction.getCreatedTimestamp()));
        dao.setUpdatedTimestamp(DateFormatUtil.zonedDateTimeToString(transaction.getUpdatedTimestamp()));
        dao.setStatus(transaction.getStatus().name());
        dao.setFundCode(transaction.getFundCode());
        dao.setAccount(transaction.getAccount().longValue());
        dao.setDealer(transaction.getDealer());
        dao.setAmount(transaction.getAmount().doubleValue());

        return dao;
    }

}
