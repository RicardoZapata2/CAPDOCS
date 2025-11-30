PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS roles (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  role_id INTEGER,
  full_name TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS products (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  sku TEXT,
  name TEXT,
  category TEXT,
  referencia TEXT,
  base_price REAL DEFAULT 0,
  costo_incl_iva REAL DEFAULT 0,
  has_sizes INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS price_scales (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  product_id INTEGER NOT NULL,
  min_qty INTEGER NOT NULL,
  max_qty INTEGER NOT NULL,
  unit_price REAL NOT NULL,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Seed roles
INSERT OR IGNORE INTO roles(name) VALUES('ADMIN');
INSERT OR IGNORE INTO roles(name) VALUES('OPERADOR');

-- Nuevas tablas para módulos ampliados
CREATE TABLE IF NOT EXISTS customers (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  email TEXT,
  phone TEXT,
  address TEXT,
  saldo_pendiente REAL DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS garments (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  product_id INTEGER NOT NULL,
  talla TEXT,
  color TEXT,
  personalizado INTEGER DEFAULT 0,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS techniques (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre TEXT NOT NULL,
  descripcion TEXT,
  costo_base REAL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS productions (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  customer_id INTEGER NOT NULL,
  fecha_inicio DATE DEFAULT (DATE('now')),
  fecha_entrega_estimada DATE,
  estado TEXT DEFAULT 'PLANEADO',
  costo_total REAL DEFAULT 0,
  notas TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS customizations (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  production_id INTEGER NOT NULL,
  garment_id INTEGER NOT NULL,
  technique_id INTEGER NOT NULL,
  detalles TEXT,
  costo_extra REAL DEFAULT 0,
  FOREIGN KEY (production_id) REFERENCES productions(id),
  FOREIGN KEY (garment_id) REFERENCES garments(id),
  FOREIGN KEY (technique_id) REFERENCES techniques(id)
);

CREATE TABLE IF NOT EXISTS designs (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  production_id INTEGER NOT NULL,
  nombre TEXT NOT NULL,
  descripcion TEXT,
  archivo_ruta TEXT,
  costo REAL DEFAULT 0,
  FOREIGN KEY (production_id) REFERENCES productions(id)
);

CREATE TABLE IF NOT EXISTS costs (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  production_id INTEGER,
  concepto TEXT NOT NULL,
  monto REAL NOT NULL,
  fecha DATE DEFAULT (DATE('now')),
  FOREIGN KEY (production_id) REFERENCES productions(id)
);

-- Ítems de producción simples (producto, talla, cantidad, precio unitario, técnica opcional)
CREATE TABLE IF NOT EXISTS production_items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  production_id INTEGER NOT NULL,
  product_id INTEGER NOT NULL,
  size TEXT,
  qty INTEGER NOT NULL,
  unit_price REAL NOT NULL,
  technique_id INTEGER,
  FOREIGN KEY (production_id) REFERENCES productions(id),
  FOREIGN KEY (product_id) REFERENCES products(id),
  FOREIGN KEY (technique_id) REFERENCES techniques(id)
);

-- Inventario por tallas
CREATE TABLE IF NOT EXISTS product_stock (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  product_id INTEGER NOT NULL,
  size TEXT NOT NULL,
  qty INTEGER NOT NULL DEFAULT 0,
  UNIQUE(product_id, size),
  FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Índices recomendados
CREATE INDEX IF NOT EXISTS idx_productions_customer ON productions(customer_id);
CREATE INDEX IF NOT EXISTS idx_customizations_prod ON customizations(production_id);
CREATE INDEX IF NOT EXISTS idx_designs_prod ON designs(production_id);
