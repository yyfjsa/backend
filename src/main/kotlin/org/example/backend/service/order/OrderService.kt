package org.example.backend.service.order

import org.example.backend.dto.OrderItemDto
import org.example.backend.exception.BusinessException
import org.example.backend.model.entity.OrderEntity
import org.example.backend.model.entity.OrderItemEntity
import org.example.backend.model.enums.OrderStatus
import org.example.backend.repository.jpa.OrderRepository
import org.example.backend.service.inventory.InventoryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random


@Service
class OrderService(private val orderRepository: OrderRepository,
                   private val inventoryService: InventoryService
) {

    /**
     * 创建订单
     */
    @Transactional
    fun createOrder(userId: Long, items: List<OrderItemDto>): OrderEntity {
        // 1. 检查库存
        items.forEach { item ->
            val availableStock = inventoryService.getAvailableStock(item.productId)
            if (availableStock < item.quantity) {
                throw BusinessException("库存不足: 商品 ${item.productId}")
            }
        }
        // 2. 扣减库存
        items.forEach { item ->
            inventoryService.reduceStock(item.productId, item.quantity)
        }

        // 3. 计算总金额
        val totalAmount = items.sumOf { it.price.multiply(BigDecimal(it.quantity)) }

        // 4. 创建订单
        val order = OrderEntity(
            orderNumber = generateOrderNumber(), // 生成订单号
            userId = userId,
            totalAmount = totalAmount,
            status = OrderStatus.PENDING_PAYMENT
        )

        // 5. 添加订单项
        items.forEach { item ->
            val orderItem = OrderItemEntity(
                productId = item.productId,
                quantity = item.quantity,
                price = item.price,
                order=order
            )
            order.addItem(orderItem)
        }

        // 6. 保存订单
        return orderRepository.save(order)
    }

    /**
     * 取消订单
     */
    @Transactional
    fun cancelOrder(orderNumber: String) {
        val order = orderRepository.findById(orderNumber)
            .orElseThrow { BusinessException("订单不存在: $orderNumber") }

        if (order.status == OrderStatus.PENDING_PAYMENT) {
            // 1. 更新订单状态
            order.status = OrderStatus.CANCELLED
            orderRepository.save(order)

            // 2. 恢复库存
            order.items.forEach { item ->
                inventoryService.restoreStock(item.productId, item.quantity)
            }
        } else {
            throw BusinessException("订单状态不允许取消: ${order.status}")
        }
    }

    /**
     * 支付订单
     */
    @Transactional
    fun payOrder(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { BusinessException("订单不存在: $orderId") }

        if (order.status == OrderStatus.PENDING_PAYMENT) {
            // 1. 更新订单状态
            order.status = OrderStatus.PAID
            orderRepository.save(order)
        } else {
            throw BusinessException("订单状态不允许支付: ${order.status}")
        }
    }

    /**
     * 生成订单号
     */
    private fun generateOrderNumber(): String {
        return "ORDER-${System.currentTimeMillis()}-${(1000..9999).random()}"
    }
}