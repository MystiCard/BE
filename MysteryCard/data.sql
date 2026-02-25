-- ==========================================================
-- 1. CÁC BẢNG ĐỘC LẬP (Không có khóa ngoại)
-- ==========================================================

-- BẢNG ROLES
INSERT INTO roles (role_code, role_name, description, active)
VALUES ('ADMIN', 'Quản trị viên', 'Toàn quyền hệ thống', true),
       ('SELLER', 'Người bán', 'Đăng bán thẻ và box', true),
       ('BUYER', 'Người mua', 'Mua box và thẻ', true),
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
VALUES ('c1111111-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Pikachu VMAX', 'ULTRA_RARE',
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
VALUES ('a0000000-0000-0000-0000-000000000006', 'SELLER'),
       ('a0000000-0000-0000-0000-000000000001', 'ADMIN'),
       ('a0000000-0000-0000-0000-000000000002', 'BUYER'),
       ('a0000000-0000-0000-0000-000000000003', 'SELLER'),
       ('a0000000-0000-0000-0000-000000000004', 'SHIPPER'),
       ('a0000000-0000-0000-0000-000000000005', 'BUYER');

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
-- BẢNG WALLET_TRANSACTION (Transaction_type: DEPOSTIE, PAYMENT, REFUND, WITHDRAW...)
INSERT INTO wallet_transaction (wallet_transaction_id, wallet_receive_id, wallet_send_id, amount, transaction_type,
                                status_transaction)
VALUES ('aa111111-1111-1111-1111-111111111111', 'b2222222-2222-2222-2222-222222222222', NULL, 1000, 'DEPOSTIE',
        'SUCCESS'),
       ('aa222222-2222-2222-2222-222222222222', 'b1111111-1111-1111-1111-111111111111',
        'b2222222-2222-2222-2222-222222222222', 100, 'PAYMENT', 'SUCCESS'),
       ('aa333333-3333-3333-3333-333333333333', NULL, 'b2222222-2222-2222-2222-222222222222', 200, 'WITHDRAW',
        'PENDING'),
       ('aa444444-4444-4444-4444-444444444444', 'b2222222-2222-2222-2222-222222222222',
        'b1111111-1111-1111-1111-111111111111', 100, 'REFUND', 'SUCCESS'),
       ('aa555555-5555-5555-5555-555555555555', 'b3333333-3333-3333-3333-333333333333',
        'b1111111-1111-1111-1111-111111111111', 500, 'TRANSFER', 'SUCCESS');