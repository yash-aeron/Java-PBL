CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    price REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
);

INSERT INTO users (username, password)
SELECT 'admin', 'admin123'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO products (name, category, quantity, price) VALUES ('Smartphone', 'Electronics', 50, 699.99);
INSERT INTO products (name, category, quantity, price) VALUES ('Laptop', 'Electronics', 30, 1200.00);
INSERT INTO products (name, category, quantity, price) VALUES ('Apples', 'Food', 150, 1.50);
INSERT INTO products (name, category, quantity, price) VALUES ('Bread', 'Food', 40, 2.00);
INSERT INTO products (name, category, quantity, price) VALUES ('Notebook', 'Stationery', 200, 3.50);
