package com.capdocs.model;

/**
 * Modelo para Productos.
 */
public class Product {
    private Integer id;
    private Integer categoryId;
    private String name;
    private Double baseCost;

    // Opcional: Para mostrar en la UI
    private String categoryName;

    public Product() {
    }

    public Product(Integer id, Integer categoryId, String name, Double baseCost, String categoryName) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.baseCost = baseCost;
        this.categoryName = categoryName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(Double baseCost) {
        this.baseCost = baseCost;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer categoryId;
        private String name;
        private Double baseCost;
        private String categoryName;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder categoryId(Integer categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder baseCost(Double baseCost) {
            this.baseCost = baseCost;
            return this;
        }

        public Builder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public Product build() {
            return new Product(id, categoryId, name, baseCost, categoryName);
        }
    }
}
