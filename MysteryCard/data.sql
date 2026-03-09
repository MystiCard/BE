-- ==========================================================
-- 1. CÁC BẢNG ĐỘC LẬP (Không có khóa ngoại)
-- ==========================================================

-- BẢNG ROLES
INSERT INTO roles (role_code, role_name, description, active)
VALUES ('ADMIN', 'Quản trị viên', 'Toàn quyền hệ thống', true),
       ('USER', 'Người bán', 'Đăng bán thẻ và box', true),
       ('SHIPPER', 'Giao hàng', 'Nhận và giao đơn hàng', true),
       ('SUPPORT', 'Hỗ trợ', 'Chăm sóc khách hàng', true);

-- BẢNG PERMISION
INSERT INTO permision (permision_code, permision_name, description, active)
VALUES ('PERM_001', 'MANAGE_SYSTEM', 'Quyền quản trị', true),
       ('PERM_002', 'POST_ITEM', 'Quyền đăng bài', true),
       ('PERM_003', 'BUY_ITEM', 'Quyền mua hàng', true),
       ('PERM_004', 'UPDATE_SHIP', 'Cập nhật giao hàng', true),
       ('PERM_005', 'VIEW_REPORT', 'Xem báo cáo', true);

-- BẢNG CARD_CATEGORY
INSERT INTO card_category (category_id, category_name)
VALUES ('11111111-1111-1111-1111-111111111111', 'Pokemon'),
       ('22222222-2222-2222-2222-222222222222', 'Yu-Gi-Oh'),
       ('33333333-3333-3333-3333-333333333333', 'Dragon Ball'),
       ('44444444-4444-4444-4444-444444444444', 'One Piece'),
       ('55555555-5555-5555-5555-555555555555', 'Magic The Gathering');

-- BẢNG RATE_CONFIG (Bao phủ Rarity)
INSERT INTO rate_config (rate_config_id, card_rarity, drop_rate, variance_percent)
VALUES ('f1111111-1111-1111-1111-111111111111', 'COMMON', 0.6, 5.0),
       ('f2222222-2222-2222-2222-222222222222', 'UNCOMMON', 0.25, 2.0),
       ('f3333333-3333-3333-3333-333333333333', 'RARE', 0.1, 1.0),
       ('f4444444-4444-4444-4444-444444444444', 'SUPER_RARE', 0.04, 0.5),
       ('f5555555-5555-5555-5555-555555555555', 'ULTRA_RARE', 0.01, 0.1);

-- ==========================================================
-- 2. NHÓM NGƯỜI DÙNG & VÍ
-- ==========================================================

-- BẢNG USERS (Gender: MALE, FEMALE)
INSERT INTO users (user_id, name, email, password, phone, gender, active, address, token_version, district_id, ward_id)
VALUES    ('a0000000-0000-0000-0000-000000000006', 'Duong Van Quyet', 'seller2@test.com',
           '$2a$10$viEPZiMSXv5nnGFQkXb5du6PIP0ACDmQx35T5UrUWwF1P6Bph9yBq', '0900000006', 'FEMALE',
           false, '202 District 2, HCM', 0, '1444', '20314'),
    ('a0000000-0000-0000-0000-000000000001', 'Nguyen Van Admin', 'admin@test.com',
        '$2a$10$viEPZiMSXv5nnGFQkXb5du6PIP0ACDmQx35T5UrUWwF1P6Bph9yBq', '0900000001', 'MALE',
        true, '123 District 1, HCM', 0, '3695', '90752'),
       ('a0000000-0000-0000-0000-000000000002', 'Tran Thi Buyer', 'buyer@test.com',
        '$2a$10$viEPZiMSXv5nnGFQkXb5du6PIP0ACDmQx35T5UrUWwF1P6Bph9yBq', '0900000002', 'FEMALE',
        true, '456 District 7, HCM', 0, '1454', '21211'),
       ('a0000000-0000-0000-0000-000000000003', 'Le Van Seller', 'seller@test.com',
        '$2a$10$viEPZiMSXv5nnGFQkXb5du6PIP0ACDmQx35T5UrUWwF1P6Bph9yBq', '0900000003', 'MALE',
        true, '789 District 3, HCM', 0, '1444', '20314'),
       ('a0000000-0000-0000-0000-000000000004', 'Pham Shipper', 'shipper@test.com',
        '$2a$10$viEPZiMSXv5nnGFQkXb5du6PIP0ACDmQx35T5UrUWwF1P6Bph9yBq', '0900000004', 'MALE',
        true, '101 District 5, HCM', 0, '1446', '20501'),
       ('a0000000-0000-0000-0000-000000000005', 'Hoang Bi Khoa', 'locked@test.com',
        '$2a$10$viEPZiMSXv5nnGFQkXb5du6PIP0ACDmQx35T5UrUWwF1P6Bph9yBq', '0900000005', 'FEMALE',
        false, '202 District 2, HCM', 0, '1443', '20202');

-- BẢNG WALLET (Status: ACTIVE, INACTIVE, SUSPENDED)
INSERT INTO wallet (wallet_id, user_id, balance, wallet_status)
VALUES        ('b6666666-6666-6666-6666-666666666666', 'a0000000-0000-0000-0000-000000000006', 2004, 'ACTIVE')
,('b1111111-1111-1111-1111-111111111111', 'a0000000-0000-0000-0000-000000000001', 10000000, 'ACTIVE'),
       ('b2222222-2222-2222-2222-222222222222', 'a0000000-0000-0000-0000-000000000002', 500000, 'ACTIVE'),
       ('b3333333-3333-3333-3333-333333333333', 'a0000000-0000-0000-0000-000000000003', 2000000, 'ACTIVE'),
       ('b4444444-4444-4444-4444-444444444444', 'a0000000-0000-0000-0000-000000000004', 0, 'INACTIVE'),
       ('b5555555-5555-5555-5555-555555555555', 'a0000000-0000-0000-0000-000000000005', 100, 'SUSPENDED');

-- ==========================================================
-- 3. NHÓM SẢN PHẨM & BLIND BOX
-- ==========================================================

-- BẢNG CARD (Rarity: Đầy đủ các loại)
INSERT INTO card (card_id, category_id, name, rarity, base_price, min_price, max_price)
VALUES
           ('c6666666-0000-0000-0000-000000000006', '11111111-1111-1111-1111-111111111111', 'Raichu GX', 'RARE', 70, 50, 120),

           ('c7777777-0000-0000-0000-000000000007', '11111111-1111-1111-1111-111111111111', 'Bulbasaur', 'COMMON', 8, 5, 15),

           ('c8888888-0000-0000-0000-000000000008', '22222222-2222-2222-2222-222222222222', 'Dark Magician', 'ULTRA_RARE', 250, 200, 500),

           ('c9999999-0000-0000-0000-000000000009', '22222222-2222-2222-2222-222222222222', 'Red-Eyes Black Dragon', 'RARE', 180, 140, 350),

           ('550e8400-e29b-41d4-a716-446655440006','11111111-1111-1111-1111-111111111111','Raichu GX','RARE',70,50,120),

           ('550e8400-e29b-41d4-a716-446655440007','11111111-1111-1111-1111-111111111111','Bulbasaur','COMMON',8,5,15),

           ('550e8400-e29b-41d4-a716-446655440008','22222222-2222-2222-2222-222222222222','Dark Magician','ULTRA_RARE',250,200,500),

           ('550e8400-e29b-41d4-a716-446655440009','22222222-2222-2222-2222-222222222222','Red-Eyes Black Dragon','RARE',180,140,350),

           ('550e8400-e29b-41d4-a716-446655440010','33333333-3333-3333-3333-333333333333','Vegeta Super Saiyan Blue','SUPER_RARE',200,150,400),
           ('c1111111-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Pikachu VMAX', 'ULTRA_RARE',
        500, 400, 1000),
       ('c2222222-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'Charmander', 'COMMON', 10, 5,
        20),
       ('c3333333-0000-0000-0000-000000000003', '22222222-2222-2222-2222-222222222222', 'Blue-Eyes White Dragon',
        'SECRET_RARE', 300, 250, 600),
       ('c4444444-0000-0000-0000-000000000004', '33333333-3333-3333-3333-333333333333', 'Goku Ultra Instinct',
        'SUPER_RARE', 150, 100, 300),
       ('c5555555-0000-0000-0000-000000000005', '44444444-4444-4444-4444-444444444444', 'Luffy Gear 5', 'RARE', 80, 60,
        120);

-- BẢNG BLIND_BOX (Status: DRAFT, ACTIVE, OUT_OF_STOCK, DISABLED, UPCOMING, ENDED)
INSERT INTO blind_box (blind_box_id, name, draw_price, all_box_price, blind_box_status, description)
VALUES ('d1111111-1111-1111-1111-111111111111', 'Pokemon Genesis', 50, 5000, 'ACTIVE', 'Thế hệ đầu tiên'),
       ('d2222222-2222-2222-2222-222222222222', 'Yugi Legend', 100, 10000, 'UPCOMING', 'Sắp ra mắt'),
       ('d3333333-3333-3333-3333-333333333333', 'Draft Box Test', 10, 1000, 'DRAFT', 'Bản nháp'),
       ('d4444444-4444-4444-4444-444444444444', 'Spring Anime 2024', 60, 6000, 'OUT_OF_STOCK', 'Đã hết hàng'),
       ('d5555555-5555-5555-5555-555555555555', 'Winter 2023 Collection', 40, 4000, 'ENDED', 'Đã kết thúc');

-- ==========================================================
-- 4. NHÓM ĐƠN HÀNG & THANH TOÁN
-- ==========================================================

-- BẢNG ORDERS (Status: CREATED, PAID, PROCESSING, SHIPPED, COMPLETED, CANCELLED, REFUNDED)
INSERT INTO orders (order_id, buyer_id, blind_box_id, total_amount, quantity, status, order_date)
VALUES ('e1111111-1111-1111-1111-111111111111', 'a0000000-0000-0000-0000-000000000002',
        'd1111111-1111-1111-1111-111111111111', 100, 2, 'PAID', NOW()),
       ('e2222222-2222-2222-2222-222222222222', 'a0000000-0000-0000-0000-000000000002',
        'd1111111-1111-1111-1111-111111111111', 50, 1, 'COMPLETED', NOW()),
       ('e3333333-3333-3333-3333-333333333333', 'a0000000-0000-0000-0000-000000000002',
        'd4444444-4444-4444-4444-444444444444', 120, 2, 'COMPLETED', NOW()),
       ('e4444444-4444-4444-4444-444444444444', 'a0000000-0000-0000-0000-000000000002',
        'd1111111-1111-1111-1111-111111111111', 50, 1, 'CANCELLED', NOW()),
       ('e5555555-5555-5555-5555-555555555555', 'a0000000-0000-0000-0000-000000000002',
        'd2222222-2222-2222-2222-222222222222', 100, 1, 'CREATED', NOW());

-- BẢNG PAYMENT (Status: PENDING, SUCCESS, FAILED, REFUNDED, ESCROWED, RELEASED)
INSERT INTO payment (payment_id, amount, status_payment, provider)
VALUES ('91111111-1111-1111-1111-111111111111', 100, 'SUCCESS', 'STRIPE'),
       ('92222222-2222-2222-2222-222222222222', 50, 'PENDING', 'VNPAY'),
       ('93333333-3333-3333-3333-333333333333', 120, 'RELEASED', 'MOMO'),
       ('94444444-4444-4444-4444-444444444444', 50, 'FAILED', 'BANK_TRANSFER'),
       ('95555555-5555-5555-5555-555555555555', 200, 'REFUNDED', 'STRIPE');

-- ==========================================================
-- 5. CÁC BẢNG LIÊN KẾT KHÁC
-- ==========================================================

-- BẢNG USER_ROLES
INSERT INTO user_roles (user_id, role_id)
VALUES ('a0000000-0000-0000-0000-000000000006', 'USER'),
       ('a0000000-0000-0000-0000-000000000001', 'ADMIN'),
       ('a0000000-0000-0000-0000-000000000002', 'USER'),
       ('a0000000-0000-0000-0000-000000000003', 'USER'),
       ('a0000000-0000-0000-0000-000000000004', 'SHIPPER'),
       ('a0000000-0000-0000-0000-000000000005', 'USER');

-- BẢNG LIST_SELLER (Status 0: OFF, 1: ON)
INSERT INTO list_seller (list_seller_id, card_id, seller_id, price, quantity, status)
VALUES  ('81111111-1111-1111-1111-888888888888', 'c1111111-0000-0000-0000-000000000001',
         'a0000000-0000-0000-0000-000000000006', 10000, 10, 1),
      ('81111111-1111-1111-1111-111111111111', 'c1111111-0000-0000-0000-000000000001',
        'a0000000-0000-0000-0000-000000000003', 450, 1, 1),
       ('82222222-2222-2222-2222-222222222222', 'c2222222-0000-0000-0000-000000000002',
        'a0000000-0000-0000-0000-000000000003', 12, 10, 1),
       ('83333333-3333-3333-3333-333333333333', 'c3333333-0000-0000-0000-000000000003',
        'a0000000-0000-0000-0000-000000000003', 300, 2, 1),
       ('84444444-4444-4444-4444-444444444444', 'c4444444-0000-0000-0000-000000000004',
        'a0000000-0000-0000-0000-000000000003', 160, 5, 1),
       ('85555555-5555-5555-5555-555555555555', 'c5555555-0000-0000-0000-000000000005',
        'a0000000-0000-0000-0000-000000000003', 90, 0, 0);
-- ==========================================================
-- 6. THÔNG BÁO CHO NGƯỜI DÙNG (buyer@test.com)
-- ==========================================================
-- 6. THÔNG BÁO CHO NGƯỜI DÙNG (buyer@test.com)
-- ==========================================================
-- noti_type: 0 (wishList), 1 (shipment), 2 (wallet)

INSERT INTO notification (notification_id, created_at, is_deleted, is_read, message, noti_type, card_id, user_id)
VALUES
-- Nhóm thông báo chưa đọc
('f1111111-1111-1111-1111-111111111111', NOW(), false, false, 'Đơn hàng e111... của bạn vừa được giao thành công!', 1, NULL, 'a0000000-0000-0000-0000-000000000002'),
('f2222222-2222-2222-2222-222222222222', NOW() - INTERVAL '1 HOUR', false, false, 'Thẻ Pikachu VMAX trong Wishlist của bạn đang giảm giá.', 0, 'c1111111-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002'),
('f3333333-3333-3333-3333-333333333333', NOW() - INTERVAL '3 HOUR', false, false, 'Tiền bán thẻ đã được cộng vào ví của bạn: +50,000 VND.', 2, NULL, 'a0000000-0000-0000-0000-000000000002'),
('f4444444-4444-4444-4444-444444444444', NOW() - INTERVAL '5 HOUR', false, false, 'Thẻ Blue-Eyes White Dragon vừa có người bán mới!', 0, 'c3333333-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000002'),
('f5555555-5555-5555-5555-555555555555', NOW() - INTERVAL '12 HOUR', false, false, 'Nạp tiền vào ví thành công: +500,000 VND.', 2, NULL, 'a0000000-0000-0000-0000-000000000002'),

-- Nhóm thông báo đã đọc
('f6666666-6666-6666-6666-666666666666', NOW() - INTERVAL '1 DAY', false, true, 'Đơn hàng e222... đang trên đường giao đến bạn.', 1, NULL, 'a0000000-0000-0000-0000-000000000002'),
('f7777777-7777-7777-7777-777777777777', NOW() - INTERVAL '2 DAY', false, true, 'Người bán đã đóng gói đơn hàng của bạn.', 1, NULL, 'a0000000-0000-0000-0000-000000000002'),
('f8888888-8888-8888-8888-888888888888', NOW() - INTERVAL '2 DAY', false, true, 'Đã trừ 100,000 VND từ ví để thanh toán đơn hàng.', 2, NULL, 'a0000000-0000-0000-0000-000000000002'),
('f9999999-9999-9999-9999-999999999999', NOW() - INTERVAL '3 DAY', false, true, 'Rất tiếc, đơn hàng e444... của bạn đã bị hủy do seller hết hàng.', 1, NULL, 'a0000000-0000-0000-0000-000000000002'),
('faaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW() - INTERVAL '4 DAY', false, true, 'Thẻ Goku Ultra Instinct trong Wishlist đã có người mua.', 0, 'c4444444-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000002'),

('fbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW() - INTERVAL '5 DAY', false, true, 'Bạn có khoản hoàn tiền 50,000 VND cho đơn hủy.', 2, NULL, 'a0000000-0000-0000-0000-000000000002'),
('fccccccc-cccc-cccc-cccc-cccccccccccc', NOW() - INTERVAL '6 DAY', false, true, 'Thẻ Luffy Gear 5 đã hết hàng từ các seller.', 0, 'c5555555-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000002'),
('fddddddd-dddd-dddd-dddd-dddddddddddd', NOW() - INTERVAL '7 DAY', false, true, 'Đơn hàng đầu tiên của bạn đã được khởi tạo.', 1, NULL, 'a0000000-0000-0000-0000-000000000002'),
('feeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', NOW() - INTERVAL '8 DAY', false, true, 'Ví của bạn đã được kích hoạt thành công.', 2, NULL, 'a0000000-0000-0000-0000-000000000002'),
('ffffffff-ffff-ffff-ffff-ffffffffffff', NOW() - INTERVAL '9 DAY', false, true, 'Bạn vừa thêm Bulbasaur vào Wishlist.', 0, 'c2222222-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002');