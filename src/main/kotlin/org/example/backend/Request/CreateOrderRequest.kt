package org.example.backend.Request

import org.example.backend.dto.OrderItemDto

data class CreateOrderRequest(
    val userId: Long,
    val items: List<OrderItemDto>
)