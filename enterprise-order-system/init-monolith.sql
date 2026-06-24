-- Create Database
CREATE DATABASE IF NOT EXISTS enterprise_monolith_db;
USE enterprise_monolith_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL
);

-- 2. Inventory Table
CREATE TABLE IF NOT EXISTS inventory (
    product_id BIGINT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    stock INT NOT NULL
);

-- 3. Orders Table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    status_reason VARCHAR(255),
    idempotency_key VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed Data
INSERT INTO users (username, email) VALUES 
('aarav', 'aarav@enterprise.com'),
('vihaan', 'vihaan@enterprise.com'),
('diya', 'diya@enterprise.com')
ON DUPLICATE KEY UPDATE email=VALUES(email);

INSERT INTO inventory (product_id, product_name, stock) VALUES
(101, 'Enterprise Laptop', 10),
(102, 'Wireless Mouse', 50),
(103, 'Mechanical Keyboard', 5)
ON DUPLICATE KEY UPDATE stock=VALUES(stock);
