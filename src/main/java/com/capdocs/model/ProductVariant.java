package com.capdocs.model;

/**
 * Modelo para Variantes de Producto (Tallas).
 * El inventario se maneja a este nivel.
 */
public class ProductVariant {
    private Integer id;
    private Integer productId;
    private String size;
    private Integer stockQuantity;

    // Opcional: Para mostrar en la UI
    private String productName;

    public ProductVariant() {
    }

    public ProductVariant(Integer id, Integer productId, String size, Integer stockQuantity, String productName) {
        this.id = id;
        this.productId = productId;
        this.size = size;
        this.stockQuantity = stockQuantity;
        this.productName = productName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer productId;
        private String size;
        private Integer stockQuantity;
        private String productName;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder productId(Integer productId) {
            this.productId = productId;
            return this;
        }

        public Builder size(String size) {
            this.size = size;
            return this;
        }

        public Builder stockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public ProductVariant build() {
            return new ProductVariant(id, productId, size, stockQuantity, productName);
        }
    }
}
