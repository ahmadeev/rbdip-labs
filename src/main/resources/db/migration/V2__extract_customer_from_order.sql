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
    ADD CONSTRAINT fk_orders_customer
    FOREIGN KEY (customer_id)
    REFERENCES customers(id);

ALTER TABLE orders
    DROP COLUMN customer_full_name,
    DROP COLUMN customer_address,
    DROP COLUMN customer_phone;
