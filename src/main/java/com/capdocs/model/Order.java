package com.capdocs.model;

import java.time.LocalDateTime;

public class Order {
    private Integer id;
    private Integer clientId;
    private String status; // PENDING, IN_PROCESS, FINISHED, DELIVERED, CANCELLED
    private Double totalPrice;
    private Double paidAmount;
    private LocalDateTime deliveryDate;
    private LocalDateTime createdAt;

    // UI Helper
    private String clientName;

    public Order() {
    }

    public Order(Integer id, Integer clientId, String status, Double totalPrice, Double paidAmount,
            LocalDateTime deliveryDate, LocalDateTime createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.paidAmount = paidAmount;
        this.deliveryDate = deliveryDate;
        this.createdAt = createdAt;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
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

    @Override
    public String toString() {
        return "Order #" + id + " - " + status;
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer clientId;
        private String status;
        private Double totalPrice;
        private Double paidAmount;
        private LocalDateTime deliveryDate;
        private LocalDateTime createdAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder clientId(Integer clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder status(Status status) {
            this.status = status.name();
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

        public Builder deliveryDate(LocalDateTime deliveryDate) {
            this.deliveryDate = deliveryDate;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Order build() {
            return new Order(id, clientId, status, totalPrice, paidAmount, deliveryDate, createdAt);
        }
    }

    public enum Status {
        PENDING, IN_PROCESS, FINISHED, DELIVERED, CANCELLED
    }
}
