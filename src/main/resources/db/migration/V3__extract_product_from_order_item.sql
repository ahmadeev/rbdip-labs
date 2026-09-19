ALTER TABLE products
    ADD COLUMN source_order_item_id BIGINT;

INSERT INTO products (source_order_item_id, name, price)
SELECT id, product_name, product_price
FROM order_items;

ALTER TABLE order_items
    ADD COLUMN product_id BIGINT;

UPDATE order_items oi
SET product_id = p.id
    FROM products p
WHERE p.source_order_item_id = oi.id;

ALTER TABLE order_items
    ALTER COLUMN product_id SET NOT NULL;

ALTER TABLE products
    DROP COLUMN source_order_item_id;

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_products
    FOREIGN KEY (product_id)
    REFERENCES products(id);

ALTER TABLE order_items
    DROP COLUMN product_name,
    DROP COLUMN product_price;
