package com.capdocs.model;

public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer variantId;
    private Integer techniqueId;
    private String techniqueDetails;
    private String referenceImagePath;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;

    // Display Helpers
    private String productName;
    private String size;

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

    public OrderItem() {
    }

    public OrderItem(Integer id, Integer orderId, Integer variantId, Integer techniqueId, String techniqueDetails,
            String referenceImagePath, Integer quantity, Double unitPrice, Double subtotal) {
        this.id = id;
        this.orderId = orderId;
        this.variantId = variantId;
        this.techniqueId = techniqueId;
        this.techniqueDetails = techniqueDetails;
        this.referenceImagePath = referenceImagePath;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
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

    public Integer getTechniqueId() {
        return techniqueId;
    }

    public void setTechniqueId(Integer techniqueId) {
        this.techniqueId = techniqueId;
    }

    public String getTechniqueDetails() {
        return techniqueDetails;
    }

    public void setTechniqueDetails(String techniqueDetails) {
        this.techniqueDetails = techniqueDetails;
    }

    public String getReferenceImagePath() {
        return referenceImagePath;
    }

    public void setReferenceImagePath(String referenceImagePath) {
        this.referenceImagePath = referenceImagePath;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer orderId;
        private Integer variantId;
        private Integer techniqueId;
        private String techniqueDetails;
        private String referenceImagePath;
        private Integer quantity;
        private Double unitPrice;
        private Double subtotal;

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

        public Builder techniqueId(Integer techniqueId) {
            this.techniqueId = techniqueId;
            return this;
        }

        public Builder techniqueDetails(String techniqueDetails) {
            this.techniqueDetails = techniqueDetails;
            return this;
        }

        public Builder referenceImagePath(String referenceImagePath) {
            this.referenceImagePath = referenceImagePath;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder unitPrice(Double unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder subtotal(Double subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(id, orderId, variantId, techniqueId, techniqueDetails, referenceImagePath, quantity,
                    unitPrice, subtotal);
        }
    }
}
