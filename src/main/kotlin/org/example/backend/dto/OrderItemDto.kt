package org.example.backend.dto

import java.math.BigDecimal

data class OrderItemDto(
    val productId: Long,      // 商品ID
    val quantity: Int,        // 购买数量
    val price: BigDecimal     // 商品单价
)