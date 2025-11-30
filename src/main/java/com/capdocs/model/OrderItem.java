package com.capdocs.model;

/**
 * Modelo para Ítems de una Orden.
 */
public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer variantId;
    private String techniqueDetails;
    private Integer quantity;

    // Opcional: Para mostrar en la UI
    private String productName;
    private String size;

    public OrderItem() {
    }

    public OrderItem(Integer id, Integer orderId, Integer variantId, String techniqueDetails, Integer quantity,
            String productName, String size) {
        this.id = id;
        this.orderId = orderId;
        this.variantId = variantId;
        this.techniqueDetails = techniqueDetails;
        this.quantity = quantity;
        this.productName = productName;
        this.size = size;
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

    public Integer getVariantId() {
        return variantId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public String getTechniqueDetails() {
        return techniqueDetails;
    }

    public void setTechniqueDetails(String techniqueDetails) {
        this.techniqueDetails = techniqueDetails;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer orderId;
        private Integer variantId;
        private String techniqueDetails;
        private Integer quantity;
        private String productName;
        private String size;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder orderId(Integer orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder variantId(Integer variantId) {
            this.variantId = variantId;
            return this;
        }

        public Builder techniqueDetails(String techniqueDetails) {
            this.techniqueDetails = techniqueDetails;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder size(String size) {
            this.size = size;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(id, orderId, variantId, techniqueDetails, quantity, productName, size);
        }
    }
}
