package com.cs.harin.rest.dao.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {

    private String status;
    private String fundCode;
    private Long account;
    private Integer dealer;
    private Double amount;

}
