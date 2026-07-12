CREATE TABLE carts (
    id UUID PRIMARY KEY,
    user_id UUID,
    session_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cartitem_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT fk_cartitem_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    sub_total DECIMAL(10, 2) NOT NULL,
    tax_total DECIMAL(10, 2) NOT NULL,
    shipping_total DECIMAL(10, 2) NOT NULL,
    grand_total DECIMAL(10, 2) NOT NULL,
    shipping_address TEXT NOT NULL,
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_by VARCHAR(255),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price_at_purchase DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES products(id)
);
