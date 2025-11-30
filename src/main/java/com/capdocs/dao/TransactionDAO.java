package com.capdocs.dao;

import com.capdocs.model.Transaction;
import com.capdocs.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void save(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (order_id, type, amount) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (transaction.getOrderId() != null) {
                pstmt.setInt(1, transaction.getOrderId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, transaction.getType().name());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.executeUpdate();
        }
    }

    public List<Transaction> findAllToday() {
        List<Transaction> transactions = new ArrayList<>();
        // SQLite 'start of day' logic
        String sql = "SELECT * FROM transactions WHERE date >= date('now', 'start of day')";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(Transaction.builder()
                        .id(rs.getInt("id"))
                        .orderId(rs.getInt("order_id"))
                        .type(Transaction.Type.valueOf(rs.getString("type")))
                        .amount(rs.getDouble("amount"))
                        .date(rs.getTimestamp("date").toLocalDateTime())
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
