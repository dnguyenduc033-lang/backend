SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE transactions;
TRUNCATE TABLE products;
TRUNCATE TABLE users;
TRUNCATE TABLE categories;
TRUNCATE TABLE suppliers;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (id, name, email, password, phone_number, role, created_at, manager_id) VALUES
-- ADMIN (root)
(1,  'Nguyen Van Admin',      'admin@inventory.com',      '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000001', 'ADMIN',   '2026-01-10 08:00:00.000000', NULL),
(2,  'Tran Thi Ha',           'ha.admin@inventory.com',   '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000002', 'ADMIN',   '2026-01-11 08:00:00.000000', NULL),

-- MANAGER (report to admin)
(3,  'Le Van Manager',        'manager@inventory.com',    '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000010', 'MANAGER', '2026-01-12 08:00:00.000000', 1),
(4,  'Pham Thi Lan',          'lan.manager@inventory.com','$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000011', 'MANAGER', '2026-01-12 09:00:00.000000', 1),
(5,  'Hoang Van Duc',         'duc.manager@inventory.com',  '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000012', 'MANAGER', '2026-01-13 08:00:00.000000', 2),

-- STAFF under Le Van Manager (warehouse)
(6,  'Vo Thi Mai',            'mai.staff@inventory.com',  '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000020', 'STAFF',   '2026-01-14 08:00:00.000000', 3),
(7,  'Dang Van Khoa',         'khoa.staff@inventory.com', '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000021', 'STAFF',   '2026-01-14 09:00:00.000000', 3),
(8,  'Bui Thi Ngoc',          'ngoc.staff@inventory.com', '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000022', 'STAFF',   '2026-01-15 08:00:00.000000', 3),
(9,  'Do Van Hung',           'hung.staff@inventory.com', '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000023', 'STAFF',   '2026-01-15 09:00:00.000000', 3),

-- STAFF under Pham Thi Lan (sales)
(10, 'Ngo Thi Huyen',         'huyen.staff@inventory.com','$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000030', 'STAFF',   '2026-01-16 08:00:00.000000', 4),
(11, 'Ly Van Tuan',           'tuan.staff@inventory.com', '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000031', 'STAFF',   '2026-01-16 09:00:00.000000', 4),
(12, 'Truong Thi Linh',        'linh.staff@inventory.com', '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000032', 'STAFF',   '2026-01-17 08:00:00.000000', 4),

-- STAFF under Hoang Van Duc (procurement)
(13, 'Phan Van Minh',         'minh.staff@inventory.com', '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000040', 'STAFF',   '2026-01-17 09:00:00.000000', 5),
(14, 'Huynh Thi Thao',        'thao.staff@inventory.com', '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000041', 'STAFF',   '2026-01-18 08:00:00.000000', 5),
(15, 'Mai Van Quan',          'quan.staff@inventory.com', '$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000042', 'STAFF',   '2026-01-18 09:00:00.000000', 5),
(16, 'Vu Thi Trang',          'trang.staff@inventory.com','$2b$10$TDBzM6Vg3xGBdfnb/FErxOlI5oHaRdy9JgQRFZC4hJou1Cb54tUuG', '0901000043', 'STAFF',   '2026-01-19 08:00:00.000000', 5);

INSERT INTO categories (id, name) VALUES
(1, 'Electronics'),
(2, 'Office Supplies'),
(3, 'Furniture');

INSERT INTO suppliers (id, name, contact_info, address) VALUES
(1, 'TechWorld Distributors', 'sales@techworld.vn', '12 Nguyen Hue, Ho Chi Minh City'),
(2, 'OfficeMart Vietnam', 'contact@officemart.vn', '45 Le Loi, Da Nang'),
(3, 'HomeComfort Co.', 'support@homecomfort.vn', '78 Tran Hung Dao, Hanoi');

INSERT INTO products (id, name, sku, price, stock_quantity, description, expiry_date, image_url, created_at, category_id) VALUES
(1, 'Wireless Mouse', 'ELEC-MOUSE-001', 19.99, 120, 'Ergonomic wireless mouse', NULL, NULL, '2026-01-20 10:00:00.000000', 1),
(2, 'USB-C Hub', 'ELEC-HUB-002', 34.50, 75, '7-in-1 USB-C adapter', NULL, NULL, '2026-01-20 10:05:00.000000', 1),
(3, 'A4 Copy Paper', 'OFF-PAPER-001', 5.25, 300, '500 sheets, 80gsm', NULL, NULL, '2026-01-21 11:00:00.000000', 2),
(4, 'Ballpoint Pen Pack', 'OFF-PEN-002', 3.99, 500, 'Pack of 10 blue pens', NULL, NULL, '2026-01-21 11:10:00.000000', 2),
(5, 'Office Chair', 'FURN-CHAIR-001', 149.00, 25, 'Adjustable mesh office chair', NULL, NULL, '2026-01-22 09:30:00.000000', 3),
(6, 'Standing Desk', 'FURN-DESK-002', 299.99, 15, 'Electric height-adjustable desk', NULL, NULL, '2026-01-22 09:45:00.000000', 3),
(7, 'Laptop Stand', 'ELEC-STAND-003', 24.00, 60, 'Aluminum laptop riser', NULL, NULL, '2026-01-23 14:00:00.000000', 1),
(8, 'Sticky Notes', 'OFF-NOTE-003', 2.50, 200, '76mm x 76mm, assorted colors', NULL, NULL, '2026-01-23 14:15:00.000000', 2);

INSERT INTO transactions (id, total_products, total_price, transaction_type, status, description, note, created_at, update_at, product_id, user_id, supplier_id) VALUES
(1, 50, 999.50, 'PURCHASE', 'COMPLETED', 'Restock wireless mice', 'Delivered on time', '2026-06-01 08:30:00.000000', '2026-06-01 10:00:00.000000', 1, 2, 1),
(2, 20, 690.00, 'PURCHASE', 'COMPLETED', 'USB-C hub shipment', NULL, '2026-06-02 09:00:00.000000', '2026-06-02 11:30:00.000000', 2, 2, 1),
(3, 5, 74.95, 'SALE', 'COMPLETED', 'Retail sale to walk-in customer', 'Paid in cash', '2026-06-03 13:15:00.000000', '2026-06-03 13:15:00.000000', 3, 6, NULL),
(4, 2, 298.00, 'SALE', 'PROCESSING', 'Office chair order', 'Awaiting delivery', '2026-06-04 15:00:00.000000', '2026-06-04 15:00:00.000000', 5, 10, NULL),
(5, 10, 149.90, 'RETURN_TO_SUPPLIER', 'PENDING', 'Defective pen packs', 'Batch #4421', '2026-06-05 10:20:00.000000', NULL, 4, 2, 2),
(6, 3, 897.00, 'PURCHASE', 'COMPLETED', 'Standing desk restock', 'Warehouse B', '2026-06-06 08:45:00.000000', '2026-06-06 12:00:00.000000', 6, 1, 3),
(7, 8, 192.00, 'SALE', 'COMPLETED', 'Laptop stand bulk order', 'Corporate client', '2026-06-07 16:30:00.000000', '2026-06-07 16:30:00.000000', 7, 11, NULL),
(8, 100, 250.00, 'PURCHASE', 'CANCELLED', 'Sticky notes order cancelled', 'Supplier out of stock', '2026-06-08 09:00:00.000000', '2026-06-08 09:30:00.000000', 8, 2, 2);
