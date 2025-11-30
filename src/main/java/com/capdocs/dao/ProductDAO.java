package com.capdocs.dao;

import com.capdocs.model.Category;
import com.capdocs.model.Product;
import com.capdocs.model.ProductVariant;
import com.capdocs.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // --- Categories ---
    public List<Category> findAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(Category.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void saveCategory(Category category) throws SQLException {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.executeUpdate();
        }
    }

    // --- Products ---
    public List<Product> findAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(Product.builder()
                        .id(rs.getInt("id"))
                        .categoryId(rs.getInt("category_id"))
                        .name(rs.getString("name"))
                        .baseCost(rs.getDouble("base_cost"))
                        .categoryName(rs.getString("category_name"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public int saveProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (category_id, name, base_cost) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, product.getCategoryId());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getBaseCost());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    // --- Variants ---
    public List<ProductVariant> findVariantsByProductId(int productId) {
        List<ProductVariant> variants = new ArrayList<>();
        String sql = "SELECT * FROM product_variants WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                variants.add(ProductVariant.builder()
                        .id(rs.getInt("id"))
                        .productId(rs.getInt("product_id"))
                        .size(rs.getString("size"))
                        .stockQuantity(rs.getInt("stock_quantity"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return variants;
    }

    public void saveVariant(ProductVariant variant) throws SQLException {
        String sql = "INSERT INTO product_variants (product_id, size, stock_quantity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, variant.getProductId());
            pstmt.setString(2, variant.getSize());
            pstmt.setInt(3, variant.getStockQuantity());
            pstmt.executeUpdate();
        }
    }

    public void updateStock(int variantId, int newQuantity) throws SQLException {
        String sql = "UPDATE product_variants SET stock_quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, variantId);
            pstmt.executeUpdate();
        }
    }
}
