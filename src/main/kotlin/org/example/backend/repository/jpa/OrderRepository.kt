package org.example.backend.repository.jpa

import org.example.backend.model.entity.OrderEntity
import org.example.backend.model.enums.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<OrderEntity, String> {
    fun findByTradeNo(tradeNo: String): MutableList<OrderEntity>
    fun findByUserId(userId: Long): List<OrderEntity>

    /**
     * 根据订单状态查找订单列表
     */
    fun findByStatus(status: OrderStatus): List<OrderEntity>

    /**
     * 更新订单状态
     */
    @Transactional
    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :status, o.updatedAt = :updatedAt WHERE o.id = :orderId")
    fun updateOrderStatus(
        @Param("orderId") orderId: String,
        @Param("status") status: OrderStatus,
        @Param("updatedAt") updatedAt: LocalDateTime = LocalDateTime.now()
    ): Int

    /**
     * 查找超时未支付的订单
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.status = :status AND o.createdAt < :timeout")
    fun findTimeoutOrders(
        @Param("status") status: OrderStatus = OrderStatus.PENDING_PAYMENT,
        @Param("timeout") timeout: LocalDateTime
    ): List<OrderEntity>
}
