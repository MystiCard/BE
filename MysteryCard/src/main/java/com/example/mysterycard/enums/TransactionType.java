package com.example.mysterycard.enums;

public enum TransactionType {
    TOP_UP,          // Nạp tiền vào ví
    PAYMENT,         // Thanh toán đơn hàng / blind box / dịch vụ
    REFUND,          // Hoàn tiền cho giao dịch trước đó
    WITHDRAW,        // Rút tiền từ ví về ngân hàng
    TRANSFER,        // Chuyển tiền giữa các ví trong hệ thống
    SELLER_PAYOUT,   // Trả tiền cho người bán
    ADJUSTMENT,      // Điều chỉnh số dư thủ công (admin)
    PENALTY          // Phạt / trừ tiền do vi phạm
}
