package com.capdocs.dao;

import com.capdocs.model.Client;
import com.capdocs.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clients.add(Client.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .phone(rs.getString("phone"))
                        .balance(rs.getDouble("balance"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public void save(Client client) throws SQLException {
        String sql = "INSERT INTO clients (name, phone, balance) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getPhone());
            pstmt.setDouble(3, client.getBalance());
            pstmt.executeUpdate();
        }
    }
}
