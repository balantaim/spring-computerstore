ALTER TABLE carts ADD CONSTRAINT fk_product_cart FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE carts ADD CONSTRAINT fk_user_cart FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE order_item ADD CONSTRAINT fk_order_order_item FOREIGN KEY (order_id) REFERENCES orders(id);
ALTER TABLE order_item ADD CONSTRAINT fk_product_order_item FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE orders ADD CONSTRAINT fk_user_order FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE payments ADD CONSTRAINT fk_order_payment FOREIGN KEY (order_id) REFERENCES orders(id);

ALTER TABLE products ADD CONSTRAINT fk_category_product FOREIGN KEY (category_id) REFERENCES categories(id);

ALTER TABLE shipments ADD CONSTRAINT fk_order_shipment FOREIGN KEY (order_id) REFERENCES orders(id);