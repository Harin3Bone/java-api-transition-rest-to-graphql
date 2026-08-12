package com.cs.harin.rest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;

    @Column(name = "fund_code")
    private String fundCode;
    
    @Column(name = "account")
    private BigInteger account;
    
    @Column(name = "dealer")
    private Integer dealer;
    
    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "created_timestamp")
    private ZonedDateTime createdTimestamp;
    
    @Column(name = "updated_timestamp")
    private ZonedDateTime updatedTimestamp;

}
