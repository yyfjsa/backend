package org.example.backend.service

import org.example.backend.model.entity.OrderEntity
import org.example.backend.model.entity.OrderHistoryEntity
import org.example.backend.model.enums.OrderStatus
import org.example.backend.repository.jpa.OrderHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderHistoryService(private val orderHistoryRepository: OrderHistoryRepository) {

    /**
     * 保存订单状态变更记录
     */
    fun saveOrderStatusChange(order: OrderEntity, status: OrderStatus) {
        // 调用 repository 的 saveOrderHistory 方法来保存变更记录
        orderHistoryRepository.saveOrderHistory(
            order = order,
            status = status,
            changedAt = LocalDateTime.now()
        )
    }

    /**
     * 查询某个订单的所有状态变更历史
     */
    fun findHistoryByOrder(orderNumber: String): List<OrderHistoryEntity> {
        return orderHistoryRepository.findByOrderNumber(orderNumber)
    }
}

