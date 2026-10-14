-- V1: Criação da estrutura base do E-commerce de Joias

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    cpf VARCHAR(255) UNIQUE, -- Tamanho maior para suportar AES-256 criptografado
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    slug VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    category_id UUID REFERENCES categories(id),
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    material VARCHAR(50),
    price_cents INTEGER NOT NULL,
    image_url VARCHAR(255),
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE product_sizes (
    id UUID PRIMARY KEY,
    product_id UUID REFERENCES products(id),
    size_name VARCHAR(10) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    status VARCHAR(50) NOT NULL,
    total_amount_cents INTEGER NOT NULL,
    shipping_fee_cents INTEGER NOT NULL,
    shipping_address TEXT,
    payment_gateway_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES orders(id),
    product_id UUID REFERENCES products(id),
    product_name_snapshot VARCHAR(100) NOT NULL,
    unit_price_cents_snapshot INTEGER NOT NULL,
    size VARCHAR(10),
    quantity INTEGER NOT NULL
);