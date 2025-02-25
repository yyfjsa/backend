package org.example.backend.model.enums

enum class OrderStatus {
    PENDING_PAYMENT, // 待支付
    PAID,            // 已支付
    CANCELLED,       // 已取消
    COMPLETED        // 已完成
}