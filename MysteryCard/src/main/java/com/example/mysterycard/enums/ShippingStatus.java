package com.example.mysterycard.enums;

public enum ShippingStatus {
    CREATED,        // Đã tạo đơn giao hàng
    PICKING_UP,     // Shipper đang tới lấy hàng
    PICKED_UP,      // Đã lấy hàng từ kho/người bán
    IN_TRANSIT,     // Đang vận chuyển
    DELIVERED,      // Giao hàng thành công
    FAILED,         // Giao hàng thất bại
    RETURNING,      // Đang hoàn hàng về
    RETURNED,       // Đã hoàn hàng
    CANCELLED       // Đơn giao hàng bị hủy
}
