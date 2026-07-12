-- ==========================================
-- V2: Product Catalog Architecture
-- ==========================================

-- 1. Categories Table
CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    parent_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100)
);

-- 2. Brands Table
CREATE TABLE brands (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(500),
    website VARCHAR(255),
    country VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100)
);

-- 3. Products Table
CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    short_description TEXT,
    long_description TEXT,
    category_id UUID NOT NULL REFERENCES categories(id),
    brand_id UUID REFERENCES brands(id),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT', -- DRAFT, ACTIVE, OUT_OF_STOCK, DISCONTINUED, ARCHIVED
    seo_title VARCHAR(255),
    seo_description TEXT,
    tax_class VARCHAR(50),
    hsn_code VARCHAR(50),
    country_of_origin VARCHAR(100),
    manufacturer VARCHAR(255),
    warranty_period VARCHAR(100),
    returnable BOOLEAN DEFAULT FALSE,
    return_days INT DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100)
);

-- 4. Product Variants Table
CREATE TABLE product_variants (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sku VARCHAR(100) NOT NULL UNIQUE,
    barcode VARCHAR(100),
    price DECIMAL(10, 2) NOT NULL,
    cost_price DECIMAL(10, 2),
    stock_quantity INT NOT NULL DEFAULT 0,
    weight DECIMAL(8, 2),
    length DECIMAL(8, 2),
    width DECIMAL(8, 2),
    height DECIMAL(8, 2),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    low_stock_threshold INT DEFAULT 5,
    allow_backorder BOOLEAN DEFAULT FALSE,
    min_order_quantity INT DEFAULT 1,
    max_order_quantity INT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100)
);

-- 5. Product Images Table
CREATE TABLE product_images (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    variant_id UUID REFERENCES product_variants(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE
);

-- 6. Attributes Table
CREATE TABLE attributes (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 7. Attribute Values Table
CREATE TABLE attribute_values (
    id UUID PRIMARY KEY,
    attribute_id UUID NOT NULL REFERENCES attributes(id) ON DELETE CASCADE,
    attribute_value VARCHAR(100) NOT NULL,
    UNIQUE (attribute_id, attribute_value)
);

-- 8. Product Attributes (Join Table)
CREATE TABLE product_attributes (
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    attribute_id UUID NOT NULL REFERENCES attributes(id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, attribute_id)
);

-- 9. Variant Attribute Values (Join Table)
CREATE TABLE variant_attribute_values (
    variant_id UUID NOT NULL REFERENCES product_variants(id) ON DELETE CASCADE,
    attribute_value_id UUID NOT NULL REFERENCES attribute_values(id) ON DELETE CASCADE,
    PRIMARY KEY (variant_id, attribute_value_id)
);

-- Indexes for performance
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_brand_id ON products(brand_id);
CREATE INDEX idx_variants_product_id ON product_variants(product_id);
CREATE INDEX idx_product_images_product_id ON product_images(product_id);
CREATE INDEX idx_product_images_variant_id ON product_images(variant_id);
