package com.cs.harin.rest.dao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDAO {

    private String id;
    private String status;
    private String fundCode;
    private Long account;
    private Integer dealer;
    private Double amount;
    private String createdTimestamp;
    private String updatedTimestamp;

}
