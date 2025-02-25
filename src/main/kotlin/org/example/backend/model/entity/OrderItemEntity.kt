package org.example.backend.model.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItemEntity(
    @Column(name = "product_id", nullable = false)
    var productId: Long, // 商品ID

    @Column(name = "quantity", nullable = false)
    var quantity: Int, // 商品数量

    @Column(name = "price", nullable = false)
    var price: BigDecimal, // 商品单价

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: OrderEntity
) : BaseEntity()