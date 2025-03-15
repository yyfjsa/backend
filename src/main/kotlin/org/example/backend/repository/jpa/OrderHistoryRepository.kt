package org.example.backend.repository.jpa

import org.example.backend.model.entity.OrderEntity
import org.example.backend.model.entity.OrderHistoryEntity
import org.example.backend.model.enums.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface OrderHistoryRepository : JpaRepository<OrderHistoryEntity, Long> {

    // 根据订单号查询状态变更历史
    fun findByOrderNumber(orderNumber: String): List<OrderHistoryEntity>

    // 保存订单状态变更记录
    @Modifying
    @Transactional
    @Query("INSERT INTO OrderHistoryEntity (order, status,changedAt) " +
            "VALUES (:order, :status, :changedAt)")
    fun saveOrderHistory(
        @Param("order") order: OrderEntity,
        @Param("status") status: OrderStatus,
        @Param("changedAt") changedAt: LocalDateTime
    )
}

