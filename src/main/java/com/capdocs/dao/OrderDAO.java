package com.capdocs.dao;

import com.capdocs.model.Order;
import com.capdocs.model.OrderItem;
import com.capdocs.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    /**
     * Creates an order atomically.
     * 1. Inserts Order
     * 2. Inserts OrderItems
     * 3. Updates Stock (decrements)
     * If any step fails, rolls back.
     */
    public void createOrder(Order order, List<OrderItem> items) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert Order
            String insertOrderSql = "INSERT INTO orders (client_id, status, total_price, paid_amount) VALUES (?, ?, ?, ?)";
            int orderId;
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, order.getClientId());
                pstmt.setString(2, order.getStatus().name());
                pstmt.setDouble(3, order.getTotalPrice());
                pstmt.setDouble(4, order.getPaidAmount());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                        order.setId(orderId);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }

            // 2. Insert Items & Update Stock
            String insertItemSql = "INSERT INTO order_items (order_id, variant_id, technique_details, quantity) VALUES (?, ?, ?, ?)";
            String updateStockSql = "UPDATE product_variants SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";

            try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql);
                    PreparedStatement stockStmt = conn.prepareStatement(updateStockSql)) {

                for (OrderItem item : items) {
                    // Insert Item
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, item.getVariantId());
                    itemStmt.setString(3, item.getTechniqueDetails());
                    itemStmt.setInt(4, item.getQuantity());
                    itemStmt.addBatch();

                    // Update Stock
                    stockStmt.setInt(1, item.getQuantity());
                    stockStmt.setInt(2, item.getVariantId());
                    stockStmt.setInt(3, item.getQuantity()); // Check if enough stock
                    int rowsAffected = stockStmt.executeUpdate();

                    if (rowsAffected == 0) {
                        throw new SQLException("Insufficient stock for variant ID: " + item.getVariantId());
                    }
                }
                itemStmt.executeBatch();
            }

            conn.commit(); // Commit Transaction

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Re-throw to be handled by Service/Controller
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Order> findAllPending() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.name as client_name FROM orders o JOIN clients c ON o.client_id = c.id WHERE o.status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(Order.builder()
                        .id(rs.getInt("id"))
                        .clientId(rs.getInt("client_id"))
                        .status(Order.Status.valueOf(rs.getString("status")))
                        .totalPrice(rs.getDouble("total_price"))
                        .paidAmount(rs.getDouble("paid_amount"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .clientName(rs.getString("client_name"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public void updateStatus(int orderId, Order.Status status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        }
    }
}
