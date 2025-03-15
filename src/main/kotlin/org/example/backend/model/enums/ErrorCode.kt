package org.example.backend.model.enums

enum class ErrorCode(
    val code: String,
    val message: String
) {
    INVALID_REQUEST("INVALID_REQUEST", "请求参数无效"),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "商品不存在"),
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", "库存不足"),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "订单不存在"),
    PAYMENT_FAILED("PAYMENT_FAILED", "支付失败"),
    INTERNAL_ERROR("INTERNAL_ERROR", "系统内部错误"),
    ORDER_STATUS_INVALID("ORDER_STATUS_INVALID", "订单状态无效")
}