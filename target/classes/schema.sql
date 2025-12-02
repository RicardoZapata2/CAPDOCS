-- Enable Foreign Keys
PRAGMA foreign_keys = ON;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('ADMIN', 'OPERATOR'))
);

-- Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL
);

-- Suppliers Table
CREATE TABLE IF NOT EXISTS suppliers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    contact_person TEXT,
    phone TEXT,
    email TEXT,
    address TEXT,
    notes TEXT
);

-- Techniques Table (Bordado, Estampado, etc.)
CREATE TABLE IF NOT EXISTS techniques (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL,
    description TEXT,
    base_cost REAL DEFAULT 0.0
);

-- Products Table
CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_id INTEGER NOT NULL,
    supplier_id INTEGER,
    name TEXT NOT NULL,
    description TEXT,
    base_cost REAL NOT NULL,
    sale_price REAL NOT NULL,
    wholesale_price REAL DEFAULT 0.0,
    wholesale_min_units INTEGER DEFAULT 12,
    image_path TEXT,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- ProductVariants Table (Inventory by Size)
CREATE TABLE IF NOT EXISTS product_variants (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL,
    size TEXT NOT NULL CHECK (size IN ('S', 'M', 'L', 'XL', 'XXL', 'Ajustable', 'Única')),
    stock_quantity INTEGER DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Clients Table
CREATE TABLE IF NOT EXISTS clients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT,
    email TEXT,
    address TEXT,
    balance REAL DEFAULT 0.0
);

-- Orders Table (Producciones/Ventas)
CREATE TABLE IF NOT EXISTS orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    client_id INTEGER NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('PENDING', 'IN_PROCESS', 'FINISHED', 'DELIVERED', 'CANCELLED')),
    total_price REAL NOT NULL,
    paid_amount REAL DEFAULT 0.0,
    delivery_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

-- OrderItems Table
CREATE TABLE IF NOT EXISTS order_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    variant_id INTEGER NOT NULL,
    technique_id INTEGER,
    technique_details TEXT,
    reference_image_path TEXT,
    quantity INTEGER NOT NULL,
    unit_price REAL NOT NULL,
    subtotal REAL NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (variant_id) REFERENCES product_variants(id),
    FOREIGN KEY (technique_id) REFERENCES techniques(id)
);

-- Payments Table (Abonos)
CREATE TABLE IF NOT EXISTS payments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    amount REAL NOT NULL,
    payment_method TEXT,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Transactions Table (Financial Movements)
CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER,
    type TEXT NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    amount REAL NOT NULL,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Initial Data
INSERT OR IGNORE INTO users (username, password_hash, role) VALUES ('admin', '$2a$10$EblZqNptsJLr9qchMqCPx.dO5.dO5.dO5.dO5.dO5.dO5.dO5.dO5', 'ADMIN');
INSERT OR IGNORE INTO techniques (name, description) VALUES ('Bordado', 'Bordado computarizado');
INSERT OR IGNORE INTO techniques (name, description) VALUES ('Estampado', 'Estampado textil');
INSERT OR IGNORE INTO techniques (name, description) VALUES ('Vinilo', 'Vinilo textil');
INSERT OR IGNORE INTO categories (name) VALUES ('Gorras');
INSERT OR IGNORE INTO categories (name) VALUES ('Camisetas');
INSERT OR IGNORE INTO categories (name) VALUES ('Polos');
