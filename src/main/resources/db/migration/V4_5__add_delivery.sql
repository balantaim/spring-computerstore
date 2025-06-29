INSERT INTO categories
(description, image_url, name, is_visible)
VALUES
('Delivery fee', '/images/category/delivery.webp', 'delivery', 0);

INSERT INTO products
(product_name, description, producer, price, stock, image_url, category_id, is_visible, is_searchable, barcode_utc)
VALUES
('Speedy',
'Speedy fee',
'Speedy',
5.00,
199999,
'',
'13',
0,
0,
'000000000001'),
('Econt',
'Econt fee',
'Econt',
5.00,
199999,
'',
'13',
0,
0,
'000000000002');