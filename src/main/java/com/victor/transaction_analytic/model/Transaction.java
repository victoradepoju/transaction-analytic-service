package com.victor.transaction_analytic.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private long transactionId;
    private LocalDateTime transactionTime;
    private String items;
    private BigDecimal saleAmount;

    // Constructor, getters, and setters
    public Transaction(long transactionId, LocalDateTime transactionTime, String items, BigDecimal saleAmount) {
        this.transactionId = transactionId;
        this.transactionTime = transactionTime;
        this.items = items;
        this.saleAmount = saleAmount;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public String getItems() {
        return items;
    }

    public BigDecimal getSaleAmount() {
        return saleAmount;
    }
}
