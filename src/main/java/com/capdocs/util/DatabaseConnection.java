package com.capdocs.util;

import java.sql.Connection;
import java.sql.DriverManager;
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

                        // Users Table
                        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "username TEXT UNIQUE NOT NULL, " +
                                        "password_hash TEXT NOT NULL, " +
                                        "role TEXT NOT NULL CHECK (role IN ('ADMIN', 'OPERATOR'))" +
                                        ");");

                        // Categories Table
                        stmt.execute("CREATE TABLE IF NOT EXISTS categories (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "name TEXT UNIQUE NOT NULL" +
                                        ");");

                        // Suppliers Table
                        stmt.execute("CREATE TABLE IF NOT EXISTS suppliers (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "name TEXT NOT NULL, " +
                                        "contact_person TEXT, " +
                                        "phone TEXT, " +
                                        "email TEXT, " +
                                        "address TEXT, " +
                                        "notes TEXT" +
                                        ");");

                        // Products Table
                        stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "category_id INTEGER NOT NULL, " +
                                        "supplier_id INTEGER, " +
                                        "name TEXT NOT NULL, " +
                                        "description TEXT, " +
                                        "base_cost REAL NOT NULL, " +
                                        "sale_price REAL NOT NULL, " +
                                        "wholesale_price REAL NOT NULL DEFAULT 0.0, " +
                                        "wholesale_min_units INTEGER DEFAULT 12, " +
                                        "image_path TEXT, " +
                                        "FOREIGN KEY (category_id) REFERENCES categories(id), " +
                                        "FOREIGN KEY (supplier_id) REFERENCES suppliers(id)" +
                                        ");");

                        // ProductVariants Table (Inventory by Size)
                        stmt.execute("CREATE TABLE IF NOT EXISTS product_variants (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "product_id INTEGER NOT NULL, " +
                                        "size TEXT NOT NULL, " +
                                        "stock_quantity INTEGER DEFAULT 0, " +
                                        "FOREIGN KEY (product_id) REFERENCES products(id)" +
                                        ");");

                        // Clients Table
                        stmt.execute("CREATE TABLE IF NOT EXISTS clients (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "name TEXT NOT NULL, " +
                                        "phone TEXT, " +
                                        "email TEXT, " +
                                        "address TEXT, " +
                                        "balance REAL DEFAULT 0.0" +
                                        ");");

                        // Orders Table
                        stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "client_id INTEGER NOT NULL, " +
                                        "status TEXT NOT NULL CHECK (status IN ('PENDING', 'IN_PROCESS', 'FINISHED', 'DELIVERED', 'CANCELLED')), "
                                        +
                                        "total_price REAL NOT NULL, " +
                                        "paid_amount REAL DEFAULT 0.0, " +
                                        "delivery_date DATETIME, " +
                                        "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (client_id) REFERENCES clients(id)" +
                                        ");");

                        // Payments Table (Abonos/Historial de Pagos)
                        stmt.execute("CREATE TABLE IF NOT EXISTS payments (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "order_id INTEGER NOT NULL, " +
                                        "amount REAL NOT NULL, " +
                                        "payment_method TEXT, " +
                                        "date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                        "notes TEXT, " +
                                        "FOREIGN KEY (order_id) REFERENCES orders(id)" +
                                        ");");

                        // OrderItems Table
                        stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "order_id INTEGER NOT NULL, " +
                                        "variant_id INTEGER NOT NULL, " +
                                        "technique_details TEXT, " +
                                        "reference_image_path TEXT, " +
                                        "quantity INTEGER NOT NULL, " +
                                        "unit_price REAL NOT NULL, " +
                                        "subtotal REAL NOT NULL, " +
                                        "FOREIGN KEY (order_id) REFERENCES orders(id), " +
                                        "FOREIGN KEY (variant_id) REFERENCES product_variants(id)" +
                                        ");");

                        // Transactions Table (General Income/Expense)
                        stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "order_id INTEGER, " +
                                        "type TEXT NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')), " +
                                        "amount REAL NOT NULL, " +
                                        "date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (order_id) REFERENCES orders(id)" +
                                        ");");

                        // Create default admin user if not exists
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                        if (rs.next() && rs.getInt(1) == 0) {
                                stmt.execute(
                                                "INSERT INTO users (username, password_hash, role) VALUES ('admin', '$2a$10$EblZqNptsJLr9qchMqCPx.dO5.dO5.dO5.dO5.dO5.dO5.dO5.dO5', 'ADMIN')");
                                System.out.println("Default admin user created.");
                        }

                        System.out.println("Database initialized successfully.");

                } catch (SQLException e) {
                        System.err.println("Error initializing database: " + e.getMessage());
                        e.printStackTrace();
                }
        }
}
