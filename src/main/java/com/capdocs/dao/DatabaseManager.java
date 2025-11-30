package com.capdocs.dao;

import java.sql.*;

/**
 * DatabaseManager singleton: manages a single SQLite file and initializes minimal tables.
 */
public final class DatabaseManager {
    private static final DatabaseManager INSTANCE = new DatabaseManager();
    // Use a dedicated DB file to avoid conflicts with any previous schema
    private static final String DB_URL = "jdbc:sqlite:capdocs_skel.db"; // local file

    private DatabaseManager() {}

    public static DatabaseManager getInstance() { return INSTANCE; }

    public Connection getConnection() throws SQLException {
        Connection cn = DriverManager.getConnection(DB_URL);
        try (Statement st = cn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA journal_mode = WAL");
            st.execute("PRAGMA busy_timeout = 5000");
        }
        return cn;
    }

    /** Create minimal tables and seed admin user */
    public void initializeDatabase() {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, role TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS customers (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
            st.execute("CREATE TABLE IF NOT EXISTS productions (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
            st.execute("INSERT OR IGNORE INTO users (username, password, role) VALUES ('admin','admin','ADMIN')");
        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }
}
