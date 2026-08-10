package com.cs.harin.rest.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    private UUID id;
    private TransactionStatus status;
    private String fundCode;
    private BigInteger account;
    private Integer dealer;
    private BigDecimal amount;
    private ZonedDateTime createdTimestamp;
    private ZonedDateTime updatedTimestamp;

}

