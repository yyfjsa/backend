package org.example.backend.model.entity

import com.alipay.api.domain.OrderItem
import jakarta.persistence.*
import org.example.backend.model.enums.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name="orders")
class OrderEntity (
    @Id
    @Column(name="order_number", nullable = false, unique = true)
    var orderNumber: String,
    @Column(name="user_id", nullable = false)
    var userId:Long,
    @Column(name="total_amount", nullable = false)
    var totalAmount: BigDecimal,
    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    var status: OrderStatus =OrderStatus.PENDING_PAYMENT,
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    var items: MutableList<OrderItemEntity> = mutableListOf()
): BaseEntity(){
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
    fun addItem(item: OrderItemEntity) {
        items.add(item)    // 添加到订单项列表
    }
}