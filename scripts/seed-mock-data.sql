SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE product_items;
TRUNCATE TABLE product_specs;
TRUNCATE TABLE notifications;
TRUNCATE TABLE purchase_requests;
TRUNCATE TABLE news;
TRUNCATE TABLE transactions;
TRUNCATE TABLE products;
TRUNCATE TABLE users;
TRUNCATE TABLE categories;
TRUNCATE TABLE suppliers;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (id, name, email, password, phone_number, role, created_at, manager_id) VALUES
(1,  'Nguyen Van Admin',      'admin@inventory.com',      '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000001', 'ADMIN',   '2026-01-10 08:00:00.000000', NULL),
(2,  'Tran Thi Ha',           'ha.admin@inventory.com',   '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000002', 'ADMIN',   '2026-01-11 08:00:00.000000', NULL),
(3,  'Le Van Manager',        'manager@inventory.com',    '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000010', 'MANAGER', '2026-01-12 08:00:00.000000', 1),
(4,  'Pham Thi Lan',          'lan.manager@inventory.com','$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000011', 'MANAGER', '2026-01-12 09:00:00.000000', 1),
(5,  'Hoang Van Duc',         'duc.manager@inventory.com',  '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000012', 'MANAGER', '2026-01-13 08:00:00.000000', 2),
(6,  'Vo Thi Mai',            'mai.staff@inventory.com',  '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000020', 'STAFF',   '2026-01-14 08:00:00.000000', 3),
(7,  'Dang Van Khoa',         'khoa.staff@inventory.com', '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000021', 'STAFF',   '2026-01-14 09:00:00.000000', 3),
(8,  'Bui Thi Ngoc',          'ngoc.staff@inventory.com', '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000022', 'STAFF',   '2026-01-15 08:00:00.000000', 3),
(9,  'Do Van Hung',           'hung.staff@inventory.com', '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000023', 'STAFF',   '2026-01-15 09:00:00.000000', 3),
(10, 'Ngo Thi Huyen',         'huyen.staff@inventory.com','$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000030', 'STAFF',   '2026-01-16 08:00:00.000000', 4),
(11, 'Ly Van Tuan',           'tuan.staff@inventory.com', '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000031', 'STAFF',   '2026-01-16 09:00:00.000000', 4),
(12, 'Truong Thi Linh',        'linh.staff@inventory.com', '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000032', 'STAFF',   '2026-01-17 08:00:00.000000', 4),
(13, 'Phan Van Minh',         'minh.staff@inventory.com', '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000040', 'STAFF',   '2026-01-17 09:00:00.000000', 5),
(14, 'Huynh Thi Thao',        'thao.staff@inventory.com', '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000041', 'STAFF',   '2026-01-18 08:00:00.000000', 5),
(15, 'Mai Van Quan',          'quan.staff@inventory.com', '$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000042', 'STAFF',   '2026-01-18 09:00:00.000000', 5),
(16, 'Vu Thi Trang',          'trang.staff@inventory.com','$2b$10$iwd.pYmqGnIBQms1UiIx5ujfpV1uGNa8Aw5H.rpi52H1hKZYpqSga', '0901000043', 'STAFF',   '2026-01-19 08:00:00.000000', 5);

INSERT INTO categories (id, name, required_specs) VALUES
(1, 'Electronics', '[{"groupName":"Thông số kỹ thuật","specs":["Brand","Model","Warranty"]}]'),
(2, 'Office Supplies', '[{"groupName":"Thông tin cơ bản","specs":["Brand","Pack Size","Color"]}]'),
(3, 'Furniture', '[{"groupName":"Kích thước","specs":["Material","Dimensions","Weight Capacity"]}]');

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

INSERT INTO product_specs (id, spec_key, spec_value, group_name, product_id) VALUES
(1,  'Brand', 'Logitech', 'Thông số kỹ thuật', 1),
(2,  'Model', 'M650', 'Thông số kỹ thuật', 1),
(3,  'Warranty', '24 months', 'Thông số kỹ thuật', 1),
(4,  'Connectivity', 'Bluetooth 5.0', 'Thông số kỹ thuật', 1),
(5,  'Brand', 'Anker', 'Thông số kỹ thuật', 2),
(6,  'Model', 'A8346', 'Thông số kỹ thuật', 2),
(7,  'Warranty', '18 months', 'Thông số kỹ thuật', 2),
(8,  'Ports', '7-in-1 USB-C', 'Thông số kỹ thuật', 2),
(9,  'Brand', 'Double A', 'Thông tin cơ bản', 3),
(10, 'Pack Size', '500 sheets', 'Thông tin cơ bản', 3),
(11, 'Color', 'White', 'Thông tin cơ bản', 3),
(12, 'Brand', 'IKEA', 'Kích thước', 5),
(13, 'Material', 'Mesh + Steel', 'Kích thước', 5),
(14, 'Dimensions', '65 x 65 x 120 cm', 'Kích thước', 5),
(15, 'Weight Capacity', '120 kg', 'Kích thước', 5),
(16, 'Brand', 'Rain Design', 'Thông số kỹ thuật', 7),
(17, 'Model', 'mStand', 'Thông số kỹ thuật', 7),
(18, 'Warranty', '12 months', 'Thông số kỹ thuật', 7),
(19, 'Material', 'Aluminum', 'Thông số kỹ thuật', 7);

INSERT INTO product_items (id, serial_number, status, product_id, transaction_id) VALUES
(1,  'WM-2026-0001', 'IN_STOCK', 1, 1),
(2,  'WM-2026-0002', 'IN_STOCK', 1, 1),
(3,  'WM-2026-0003', 'IN_STOCK', 1, 1),
(4,  'WM-2026-0004', 'SOLD', 1, 1),
(5,  'WM-2026-0005', 'SOLD', 1, 1),
(6,  'HUB-2026-0001', 'IN_STOCK', 2, 2),
(7,  'HUB-2026-0002', 'IN_STOCK', 2, 2),
(8,  'HUB-2026-0003', 'DEFECTIVE', 2, 2),
(9,  'STD-2026-0001', 'IN_STOCK', 7, 7),
(10, 'STD-2026-0002', 'IN_STOCK', 7, 7),
(11, 'STD-2026-0003', 'SOLD', 7, 7),
(12, 'STD-2026-0004', 'SOLD', 7, 7),
(13, 'STD-2026-0005', 'SOLD', 7, 7),
(14, 'STD-2026-0006', 'SOLD', 7, 7),
(15, 'STD-2026-0007', 'SOLD', 7, 7),
(16, 'STD-2026-0008', 'SOLD', 7, 7);

INSERT INTO purchase_requests (id, confirm_token, created_at, note, purchase_price, quantity, reject_reason, reviewed_at, status, created_by, product_id, reviewed_by, supplier_id) VALUES
(1, NULL, '2026-06-01 09:00:00.000000', 'Urgent restock for Q2', 18.50, 50, NULL, '2026-06-01 14:00:00.000000', 'COMPLETED', 3, 1, 1, 1),
(2, NULL, '2026-06-02 10:00:00.000000', 'New model USB-C hub', 32.00, 20, NULL, '2026-06-02 15:30:00.000000', 'COMPLETED', 4, 2, 1, 1),
(3, 'tok-await-003', '2026-06-05 08:00:00.000000', 'Need more chairs for new office', 140.00, 10, NULL, NULL, 'AWAITING_APPROVAL', 3, 5, NULL, 3),
(4, NULL, '2026-06-04 11:00:00.000000', 'Standing desk pilot order', 280.00, 5, NULL, '2026-06-04 16:00:00.000000', 'WAITING_DELIVERY', 5, 6, 2, 3),
(5, NULL, '2026-06-03 09:30:00.000000', 'Bulk sticky notes', 2.20, 200, 'Budget exceeded for this quarter', '2026-06-03 17:00:00.000000', 'REJECTED', 4, 8, 1, 2),
(6, NULL, '2026-06-06 13:00:00.000000', 'Laptop stand for corporate client', 22.00, 30, NULL, '2026-06-06 17:30:00.000000', 'APPROVED', 5, 7, 2, 1),
(7, NULL, '2026-06-07 08:30:00.000000', 'Paper restock', 4.80, 100, NULL, '2026-06-07 10:00:00.000000', 'SUPPLIER_REJECTED', 3, 3, 1, 2);

INSERT INTO notifications (id, created_at, is_read, link, message, title, type, recipient_id) VALUES
(1, '2026-06-01 14:05:00.000000', 1, '/purchase-approval', 'Yêu cầu nhập 50 Wireless Mouse đã được duyệt và hoàn tất.', 'Yêu cầu nhập hàng đã duyệt', 'APPROVED', 3),
(2, '2026-06-02 15:35:00.000000', 1, '/purchase-approval', 'Yêu cầu nhập 20 USB-C Hub đã được duyệt.', 'Yêu cầu nhập hàng đã duyệt', 'APPROVED', 4),
(3, '2026-06-03 17:05:00.000000', 0, '/purchase-request', 'Yêu cầu nhập 200 Sticky Notes bị từ chối: Budget exceeded for this quarter.', 'Yêu cầu nhập hàng bị từ chối', 'REJECTED', 4),
(4, '2026-06-04 16:05:00.000000', 0, '/purchase-request', 'NCC HomeComfort đã xác nhận đơn 5 Standing Desk. Chờ giao hàng.', 'NCC đã xác nhận đơn hàng', 'SUPPLIER_ACCEPTED', 5),
(5, '2026-06-06 17:35:00.000000', 0, '/purchase-approval', 'Yêu cầu nhập 30 Laptop Stand đã được duyệt. Email đã gửi cho NCC.', 'Yêu cầu nhập hàng đã duyệt', 'APPROVED', 5),
(6, '2026-06-07 10:05:00.000000', 0, '/purchase-request', 'OfficeMart từ chối đơn nhập 100 A4 Copy Paper.', 'NCC từ chối đơn hàng', 'SUPPLIER_REJECTED', 3),
(7, '2026-06-07 18:00:00.000000', 0, '/transaction', 'Giao dịch bán 8 Laptop Stand đã hoàn tất.', 'Giao dịch hoàn tất', 'COMPLETED', 4);

INSERT INTO news (id, title, content, author, created_at) VALUES
(1, 'Hệ thống IMS chính thức vận hành', 'Hệ thống quản lý kho IMS đã được triển khai thành công. Tất cả nhân viên vui lòng cập nhật thông tin cá nhân và làm quen với quy trình nhập - xuất kho mới.', 'Nguyen Van Admin', '2026-06-01 08:00:00.000000'),
(2, 'Quy trình duyệt nhập hàng mới', 'Từ tháng 6/2026, mọi yêu cầu nhập hàng cần được Quản lý tạo và Admin phê duyệt trước khi gửi email xác nhận cho nhà cung cấp.', 'Tran Thi Ha', '2026-06-02 09:30:00.000000'),
(3, 'Kiểm kê kho định kỳ tháng 6', 'Kho hàng sẽ tiến hành kiểm kê vào ngày 15/06/2026. Các bộ phận vui lòng hoàn tất mọi giao dịch xuất kho trước ngày 14/06.', 'Le Van Manager', '2026-06-05 14:00:00.000000'),
(4, 'Chính sách bảo hành sản phẩm điện tử', 'Sản phẩm điện tử có serial number sẽ được bảo hành theo thời gian ghi trên thông số kỹ thuật. Tra cứu bảo hành tại mục Bảo hành trên hệ thống.', 'Pham Thi Lan', '2026-06-06 10:00:00.000000'),
(5, 'Tuyển dụng nhân viên kho mới', 'Bộ phận Nhân sự đang tuyển thêm 2 nhân viên kho. Ứng viên quan tâm liên hệ Quản lý trực tiếp hoặc gửi hồ sơ qua email admin@inventory.com.', 'Hoang Van Duc', '2026-06-07 08:00:00.000000');
