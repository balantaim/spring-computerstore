ALTER TABLE products
ADD COLUMN product_specifications JSON DEFAULT NULL;


UPDATE products
SET compatible_with = 'AM5',
    product_specifications = JSON_OBJECT(
        'Chipset', 'AMD B850',
        'Supported CPU', 'AMD Ryzen 7000 series;AMD Ryzen 8000 series;AMD Ryzen 9000 series',
        'Memory', 'DDR5',
        'LAN', '10/100/1000/2500 Mb/s;WiFi 6E 802.11a/b/g/n/ac/ax;Bluetooth V5.3',
        'Form factor', 'ATX'
    )
WHERE product_name = 'GIGABYTE B850 GAMING X WIFI6E'
ORDER BY id ASC
LIMIT 1;


UPDATE products
SET compatible_with = 'PCI Express 5.0',
    product_specifications = JSON_OBJECT(
        'Memory capacity', '12 GB',
        'Memory type', 'GDDR 7',
        'Interfaces', '1 x HDMI 2.1b;3 x DisplayPort 2.1b',
        'Supported resolutions', '7680 x 4320',
        'Cuda cores', '6144',
        'Architecture', 'Blackwell',
        'Max power consumption', '250 W',
        'Extras', 'Nvidia G-Sync;RGB LED;Nvidia Game Ready;Nvidia Encoder;Nvidia Broadcast app;Nvidia Studio;GIGABYTE Control Center;WINDFORCE система на охлаждане;Nvidia DLSS 4;NVIDIA Reflex 2;NVIDIA ACE'
    )
WHERE product_name = 'GIGABYTE GeForce RTX 5070 12GB WINDFORCE SFF DLSS 4'
ORDER BY id ASC
LIMIT 1;