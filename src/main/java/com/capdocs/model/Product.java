package com.capdocs.model;

/**
 * Modelo para Productos.
 */
public class Product {
    private Integer id;
    private Integer categoryId;
    private Integer supplierId;
    private String name;
    private String description;
    private Double baseCost;
    private Double salePrice;
    private Double wholesalePrice;
    private Integer wholesaleMinUnits;
    private String imagePath;

    // Opcional: Para mostrar en la UI
    private String categoryName;

    public Product() {
    }

    public Product(Integer id, Integer categoryId, Integer supplierId, String name, String description,
            Double baseCost, Double salePrice, Double wholesalePrice, Integer wholesaleMinUnits,
            String imagePath, String categoryName) {
        this.id = id;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.name = name;
        this.description = description;
        this.baseCost = baseCost;
        this.salePrice = salePrice;
        this.wholesalePrice = wholesalePrice;
        this.wholesaleMinUnits = wholesaleMinUnits;
        this.imagePath = imagePath;
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

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(Double baseCost) {
        this.baseCost = baseCost;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Double getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(Double wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public Integer getWholesaleMinUnits() {
        return wholesaleMinUnits;
    }

    public void setWholesaleMinUnits(Integer wholesaleMinUnits) {
        this.wholesaleMinUnits = wholesaleMinUnits;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
        private Integer supplierId;
        private String name;
        private String description;
        private Double baseCost;
        private Double salePrice;
        private Double wholesalePrice;
        private Integer wholesaleMinUnits;
        private String imagePath;
        private String categoryName;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder categoryId(Integer categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder supplierId(Integer supplierId) {
            this.supplierId = supplierId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder baseCost(Double baseCost) {
            this.baseCost = baseCost;
            return this;
        }

        public Builder salePrice(Double salePrice) {
            this.salePrice = salePrice;
            return this;
        }

        public Builder wholesalePrice(Double wholesalePrice) {
            this.wholesalePrice = wholesalePrice;
            return this;
        }

        public Builder wholesaleMinUnits(Integer wholesaleMinUnits) {
            this.wholesaleMinUnits = wholesaleMinUnits;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public Product build() {
            return new Product(id, categoryId, supplierId, name, description, baseCost, salePrice, wholesalePrice,
                    wholesaleMinUnits, imagePath, categoryName);
        }
    }
}
