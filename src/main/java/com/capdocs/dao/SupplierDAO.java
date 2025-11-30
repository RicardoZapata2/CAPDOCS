package com.capdocs.dao;

import com.capdocs.model.Supplier;
import com.capdocs.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all suppliers: " + e.getMessage());
        }
        return suppliers;
    }

    public void create(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getContactPerson());
            pstmt.setString(3, supplier.getPhone());
            pstmt.setString(4, supplier.getEmail());
            pstmt.setString(5, supplier.getAddress());
            pstmt.setString(6, supplier.getNotes());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating supplier: " + e.getMessage());
        }
    }

    public void update(Supplier supplier) {
        String sql = "UPDATE suppliers SET name = ?, contact_person = ?, phone = ?, email = ?, address = ?, notes = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getContactPerson());
            pstmt.setString(3, supplier.getPhone());
            pstmt.setString(4, supplier.getEmail());
            pstmt.setString(5, supplier.getAddress());
            pstmt.setString(6, supplier.getNotes());
            pstmt.setInt(7, supplier.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM suppliers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
        }
    }

    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        return new Supplier(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("contact_person"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("notes"));
    }
}
