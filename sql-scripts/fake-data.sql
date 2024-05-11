USE computer_store;

INSERT INTO categories
(description, image_url, name)
VALUES
('The Central Processing Unit (CPU)', '/images/category/category-cpu.jpg', 'cpu'),
('A computer monitor is an output device that displays information in pictorial or textual form', '/images/category/category-monitors.jpg', 'monitors'),
('A video card, also known as a graphics card or graphics processing unit (GPU)', '/images/category/category-video-card.jpg', 'video-cards');

INSERT INTO products
(product_name, description, producer, price, stock, image_url, category_id)
VALUES
('Intel Core i9-13900KS (2.4GHz)', 
'Total Cores: 24, Performance-cores: 8, Max Turbo Frequency: 6.00 GHz',
'Intel',
1636.99,
0,
'https://ardes.bg/uploads/original/intel-core-i9-13900ks-2-4ghz-441039.jpg',
'1'),
('Intel Core i9-12900F (1.8GHz)', 
'Total Cores: 24, Performance-cores: 16, Max Turbo Frequency: 5.10 GHz',
'Intel',
900.00,
3,
'https://ardes.bg/uploads/original/i9-12900f-5-1ghz-30m-box-1700-377469.jpg',
'1'),
('AMD Ryzen 9 7950X (4.5GHz)', 
'Total Cores: 16, Max Turbo Frequency: 4.50 GHz',
'AMD',
1269.60,
11,
'https://ardes.bg/uploads/original/amd-ryzen-9-7950x-box-407080.jpg',
'1'),

('57" Samsung Odyssey Neo G9', 
'Resolution: 7680 x 2160, 240 Hz',
'Samsung',
4999.60,
1,
'https://ardes.bg/uploads/original/samsung-ls57cg952nuxen-odyssey-g9-57inch-dwqhd-va-495627.jpg',
'2'),
('32" ASUS ROG Swift PG32UCDM', 
'Resolution: 3840 x 2160, 240 Hz',
'Asus',
3149.00,
2,
'https://ardes.bg/uploads/original/32-asus-rog-swift-pg32ucdm-541265.jpg',
'2'),

('PNY GeForce RTX 4090 24GB XLR8 Gaming Verto EPIC-X RGB DLSS 3', 
'Cuda cores: 16384',
'Nvidia',
4986.01,
1,
'https://ardes.bg/uploads/original/pny-geforce-rtx-4090-24gb-gddr6x-gaming-verto-517527.jpg',
'3'),
('ASUS Radeon RX 7900 XTX 24GB TUF Gaming OC', 
'RAM: GDDR6 24 GB',
'AMD',
2363.00,
3,
'https://ardes.bg/uploads/original/asus-amd-radeon-rx-7900-xtx-24gb-tuf-gaming-oc-450981.jpg',
'3');