CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    created_at DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    contact_info VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2),
    stock_quantity INT,
    description VARCHAR(255),
    expiry_date DATETIME(6),
    image_url VARCHAR(255),
    created_at DATETIME(6),
    category_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_products_sku (sku),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    total_products INT,
    total_price DECIMAL(19, 2),
    transaction_type VARCHAR(255),
    status VARCHAR(255),
    description VARCHAR(255),
    note VARCHAR(255),
    created_at DATETIME(6),
    update_at DATETIME(6),
    product_id BIGINT,
    user_id BIGINT,
    supplier_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_transactions_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id)
);
