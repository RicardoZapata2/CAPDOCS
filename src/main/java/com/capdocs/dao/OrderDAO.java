package com.capdocs.dao;

import com.capdocs.model.Order;
import com.capdocs.model.OrderItem;
import com.capdocs.model.Payment;
import com.capdocs.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDAO {

    /**
     * Creates an order with all its dependencies in a single transaction.
     * ACID Compliant: Atomicity ensured by manual commit/rollback.
     */
    public void createOrder(Order order, List<OrderItem> items, Payment initialPayment) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert Order
            String insertOrderSql = "INSERT INTO orders (client_id, status, total_price, paid_amount, delivery_date) VALUES (?, ?, ?, ?, ?)";
            int orderId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, order.getClientId());
                pstmt.setString(2, order.getStatus());
                pstmt.setDouble(3, order.getTotalPrice());
                pstmt.setDouble(4, order.getPaidAmount());
                pstmt.setObject(5, order.getDeliveryDate());
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                        order.setId(orderId);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }

            // 2. Insert Order Items and Update Stock
            String insertItemSql = "INSERT INTO order_items (order_id, variant_id, technique_id, technique_details, reference_image_path, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            String updateStockSql = "UPDATE product_variants SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";

            try (PreparedStatement pstmtItem = conn.prepareStatement(insertItemSql);
                    PreparedStatement pstmtStock = conn.prepareStatement(updateStockSql)) {

                for (OrderItem item : items) {
                    // Insert Item
                    pstmtItem.setInt(1, orderId);
                    pstmtItem.setInt(2, item.getVariantId());
                    if (item.getTechniqueId() != null)
                        pstmtItem.setInt(3, item.getTechniqueId());
                    else
                        pstmtItem.setNull(3, Types.INTEGER);
                    pstmtItem.setString(4, item.getTechniqueDetails());
                    pstmtItem.setString(5, item.getReferenceImagePath());
                    pstmtItem.setInt(6, item.getQuantity());
                    pstmtItem.setDouble(7, item.getUnitPrice());
                    pstmtItem.setDouble(8, item.getSubtotal());
                    pstmtItem.executeUpdate();

                    // Update Stock
                    pstmtStock.setInt(1, item.getQuantity());
                    pstmtStock.setInt(2, item.getVariantId());
                    pstmtStock.setInt(3, item.getQuantity()); // Ensure enough stock
                    int rowsAffected = pstmtStock.executeUpdate();

                    if (rowsAffected == 0) {
                        throw new SQLException("Insufficient stock for variant ID: " + item.getVariantId());
                    }
                }
            }

            // 3. Insert Payment (if any)
            if (initialPayment != null && initialPayment.getAmount() > 0) {
                String insertPaymentSql = "INSERT INTO payments (order_id, amount, payment_method, notes) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertPaymentSql)) {
                    pstmt.setInt(1, orderId);
                    pstmt.setDouble(2, initialPayment.getAmount());
                    pstmt.setString(3, initialPayment.getPaymentMethod());
                    pstmt.setString(4, initialPayment.getNotes());
                    pstmt.executeUpdate();
                }

                // 4. Register Transaction (Income)
                String insertTransSql = "INSERT INTO transactions (order_id, type, amount, description) VALUES (?, 'INCOME', ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertTransSql)) {
                    pstmt.setInt(1, orderId);
                    pstmt.setDouble(2, initialPayment.getAmount());
                    pstmt.setString(3, "Venta #" + orderId);
                    pstmt.executeUpdate();
                }
            }

            conn.commit(); // Commit Transaction
            System.out.println("Order created successfully with ID: " + orderId);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                    System.err.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Re-throw to handle in UI
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Order> findAllOrders() {
        List<Order> orders = new java.util.ArrayList<>();
        String sql = "SELECT o.*, c.name as client_name FROM orders o JOIN clients c ON o.client_id = c.id ORDER BY o.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setClientId(rs.getInt("client_id"));
                order.setStatus(rs.getString("status"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setPaidAmount(rs.getDouble("paid_amount"));
                order.setDeliveryDate(rs.getObject("delivery_date", LocalDateTime.class));
                order.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                order.setClientName(rs.getString("client_name"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public void updateStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OrderItem> findItemsByOrderId(int orderId) {
        List<OrderItem> items = new java.util.ArrayList<>();
        String sql = "SELECT oi.*, pv.size, p.name as product_name FROM order_items oi " +
                "JOIN product_variants pv ON oi.variant_id = pv.id " +
                "JOIN products p ON pv.product_id = p.id " +
                "WHERE oi.order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setVariantId(rs.getInt("variant_id"));
                item.setTechniqueId(rs.getInt("technique_id") == 0 ? null : rs.getInt("technique_id"));
                item.setTechniqueDetails(rs.getString("technique_details"));
                item.setReferenceImagePath(rs.getString("reference_image_path"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setSubtotal(rs.getDouble("subtotal"));

                item.setProductName(rs.getString("product_name"));
                item.setSize(rs.getString("size"));

                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void addPayment(int orderId, double amount, String method, String notes) throws SQLException {
        String updateOrderSql = "UPDATE orders SET paid_amount = paid_amount + ? WHERE id = ?";
        String insertPaymentSql = "INSERT INTO payments (order_id, amount, payment_method, notes) VALUES (?, ?, ?, ?)";
        String insertTransSql = "INSERT INTO transactions (order_id, type, amount, description) VALUES (?, 'INCOME', ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(updateOrderSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, orderId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertPaymentSql)) {
                pstmt.setInt(1, orderId);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, method);
                pstmt.setString(4, notes);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertTransSql)) {
                pstmt.setInt(1, orderId);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, "Abono a Orden #" + orderId);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Order> findOrdersByClientId(int clientId) {
        List<Order> orders = new java.util.ArrayList<>();
        String sql = "SELECT * FROM orders WHERE client_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setClientId(rs.getInt("client_id"));
                order.setStatus(rs.getString("status"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setPaidAmount(rs.getDouble("paid_amount"));
                order.setDeliveryDate(rs.getObject("delivery_date", LocalDateTime.class));
                order.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public double calculateTotalReceivables() {
        double total = 0;
        String sql = "SELECT SUM(total_price - paid_amount) FROM orders WHERE status != 'CANCELLED'";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public double calculateTotalProfit(java.time.LocalDate date) {
        double profit = 0;
        // Profit = (Unit Price - Base Cost) * Quantity
        // We need to join order_items -> product_variants -> products
        String sql = "SELECT SUM((oi.unit_price - p.base_cost) * oi.quantity) " +
                "FROM order_items oi " +
                "JOIN orders o ON oi.order_id = o.id " +
                "JOIN product_variants pv ON oi.variant_id = pv.id " +
                "JOIN products p ON pv.product_id = p.id " +
                "WHERE date(o.created_at) = date(?) AND o.status != 'CANCELLED'";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                profit = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profit;
    }

    public int countItemsSoldByDate(java.time.LocalDate date) {
        int count = 0;
        String sql = "SELECT SUM(oi.quantity) FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE date(o.created_at) = date(?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
