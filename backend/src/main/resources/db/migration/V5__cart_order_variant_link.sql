-- Link cart items and order items to the specific product variant that was chosen.
-- Nullable so existing rows and single-variant flows remain valid.

ALTER TABLE cart_items ADD COLUMN variant_id UUID;
ALTER TABLE cart_items ADD CONSTRAINT fk_cartitem_variant
    FOREIGN KEY (variant_id) REFERENCES product_variants(id);

ALTER TABLE order_items ADD COLUMN variant_id UUID;
ALTER TABLE order_items ADD CONSTRAINT fk_orderitem_variant
    FOREIGN KEY (variant_id) REFERENCES product_variants(id);
