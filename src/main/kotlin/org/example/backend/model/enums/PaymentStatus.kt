package org.example.backend.model.enums

enum class PaymentStatus {
    PENDING,  // 待支付
    TRADE_SUCCESS,  // 支付成功
    TRADE_CLOSED,   // 支付失败
    REFUNDED  // 已退款
}