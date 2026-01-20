package com.example.mysterycard.enums;

public enum OrderStatus {
    CREATED,          // Đã tạo đơn
    PENDING_PAYMENT,  // Chờ thanh toán
    PAID,             // Đã thanh toán
    PROCESSING,       // Đang chuẩn bị hàng
    SHIPPED,          // Đã giao cho đơn vị vận chuyển
    DELIVERED,        // Đã giao thành công
    COMPLETED,        // Hoàn tất đơn hàng
    CANCELLED,        // Đã hủy
    REFUNDED          // Đã hoàn tiền
}
