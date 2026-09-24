-- customers

CREATE TABLE customers (
     id BIGSERIAL PRIMARY KEY,
     full_name VARCHAR(255) NOT NULL,
     address VARCHAR(500),
     phone VARCHAR(50),
     source_order_id BIGINT,
     FOREIGN KEY (source_order_id) REFERENCES orders(id)
);

INSERT INTO customers (source_order_id, full_name, address, phone)
SELECT id, customer_full_name, customer_address, customer_phone
FROM orders;

ALTER TABLE orders
    ADD COLUMN customer_id BIGINT;

UPDATE orders o
SET customer_id = c.id
    FROM customers c
WHERE c.source_order_id = o.id;

ALTER TABLE orders
    ALTER COLUMN customer_id SET NOT NULL;

ALTER TABLE customers
    DROP COLUMN source_order_id;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_customers
    FOREIGN KEY (customer_id)
    REFERENCES customers(id);

-- products

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
