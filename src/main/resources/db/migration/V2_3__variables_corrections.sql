
ALTER TABLE users
MODIFY COLUMN enabled BIT,
MODIFY COLUMN verified_profile BIT;

ALTER TABLE orders
MODIFY COLUMN total_amount DECIMAL(38,2);

ALTER TABLE payments
MODIFY COLUMN amount DECIMAL(38,2);

ALTER TABLE order_item
MODIFY COLUMN price_per_unit DECIMAL(38,2);

ALTER TABLE products
MODIFY COLUMN price DECIMAL(38,2);