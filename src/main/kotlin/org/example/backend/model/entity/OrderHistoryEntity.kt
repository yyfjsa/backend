package org.example.backend.model.entity

import jakarta.persistence.*
import org.example.backend.model.enums.OrderStatus
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

@Entity
@Table(name = "order_histories")
class OrderHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    var status_id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_number", nullable = false)
    var order: OrderEntity,  // 关联订单实体

    @Column(name = "status", nullable = false)
    var status: OrderStatus,  // 订单状态变动

    @Column(name = "changed_at", nullable = false)
    var changedAt: LocalDateTime = LocalDateTime.now(),  // 记录变动时间

    @Column(name = "comments")
    var comments: String? = null  // 可选的备注信息
) : BaseEntity()  // 继承BaseEntity，提供共用的字段如 createdAt 和 updatedAt