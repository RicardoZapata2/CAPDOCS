package com.capdocs.model;

import java.time.LocalDateTime;

/**
 * Modelo para Transacciones Financieras.
 */
public class Transaction {
    private Integer id;
    private Integer orderId;
    private Type type;
    private Double amount;
    private LocalDateTime date;

    public enum Type {
        INCOME,
        EXPENSE
    }

    public Transaction() {
    }

    public Transaction(Integer id, Integer orderId, Type type, Double amount, LocalDateTime date) {
        this.id = id;
        this.orderId = orderId;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer orderId;
        private Type type;
        private Double amount;
        private LocalDateTime date;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder orderId(Integer orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder date(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public Transaction build() {
            return new Transaction(id, orderId, type, amount, date);
        }
    }
}
