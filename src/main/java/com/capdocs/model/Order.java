package com.capdocs.model;

import java.time.LocalDateTime;

/**
 * Modelo para Órdenes de Producción.
 */
public class Order {
    private Integer id;
    private Integer clientId;
    private Status status;
    private Double totalPrice;
    private Double paidAmount;
    private LocalDateTime createdAt;

    // Opcional: Para mostrar en la UI
    private String clientName;

    public enum Status {
        PENDING,
        IN_PROCESS,
        FINISHED
    }

    public Order() {
    }

    public Order(Integer id, Integer clientId, Status status, Double totalPrice, Double paidAmount,
            LocalDateTime createdAt, String clientName) {
        this.id = id;
        this.clientId = clientId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.paidAmount = paidAmount;
        this.createdAt = createdAt;
        this.clientName = clientName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer clientId;
        private Status status;
        private Double totalPrice;
        private Double paidAmount;
        private LocalDateTime createdAt;
        private String clientName;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder clientId(Integer clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder totalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder paidAmount(Double paidAmount) {
            this.paidAmount = paidAmount;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder clientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        public Order build() {
            return new Order(id, clientId, status, totalPrice, paidAmount, createdAt, clientName);
        }
    }
}
