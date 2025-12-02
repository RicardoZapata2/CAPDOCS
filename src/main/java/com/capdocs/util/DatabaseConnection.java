package com.capdocs.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

        private static final String DB_URL = "jdbc:sqlite:capdocs.db";

        public static Connection getConnection() throws SQLException {
                return DriverManager.getConnection(DB_URL);
        }

        public static void initializeDatabase() {
                try (Connection conn = getConnection();
                                Statement stmt = conn.createStatement()) {

                        // Enable foreign keys
                        stmt.execute("PRAGMA foreign_keys = ON;");

                        // Read and execute schema.sql
                        String schema = loadSchema();
                        if (schema != null && !schema.isEmpty()) {
                                // Split by semi-colon to execute statements individually
                                String[] statements = schema.split(";");
                                for (String sql : statements) {
                                        if (!sql.trim().isEmpty()) {
                                                stmt.execute(sql);
                                        }
                                }
                                System.out.println("Database initialized successfully from schema.sql.");
                        } else {
                                System.err.println("Error: schema.sql not found or empty.");
                        }

                } catch (SQLException e) {
                        System.err.println("Error initializing database: " + e.getMessage());
                        e.printStackTrace();
                }

                // Ensure admin password is correct (Reset to 'admin')
                resetAdminPassword();
        }

        private static void resetAdminPassword() {
                String sql = "UPDATE users SET password_hash = ? WHERE username = 'admin'";
                try (Connection conn = getConnection();
                                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        // Hash for "admin"
                        String hash = org.mindrot.jbcrypt.BCrypt.hashpw("admin", org.mindrot.jbcrypt.BCrypt.gensalt());
                        pstmt.setString(1, hash);
                        pstmt.executeUpdate();
                        System.out.println("Admin password reset to 'admin'.");
                } catch (SQLException e) {
                        System.err.println("Error resetting admin password: " + e.getMessage());
                }
        }

        private static String loadSchema() {
                try (java.io.InputStream is = DatabaseConnection.class.getResourceAsStream("/schema.sql");
                                java.util.Scanner scanner = new java.util.Scanner(is,
                                                java.nio.charset.StandardCharsets.UTF_8.name())) {
                        return scanner.useDelimiter("\\A").next();
                } catch (Exception e) {
                        System.err.println("Could not load schema.sql: " + e.getMessage());
                        return null;
                }
        }
}
