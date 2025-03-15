package org.example.backend.service

import org.example.backend.dto.OrderItemDto
import org.example.backend.exception.BusinessException
import org.example.backend.model.entity.OrderEntity
import org.example.backend.model.entity.OrderItemEntity
import org.example.backend.model.enums.ErrorCode
import org.example.backend.model.enums.OrderStatus
import org.example.backend.repository.jpa.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val inventoryService: InventoryService,
    private val orderHistoryService: OrderHistoryService,
) {

    /**
     * 创建订单
     */
    @Transactional
    fun createOrder(userId: Long, items: List<OrderItemDto>, tradeNo: String): OrderEntity {
        // 1. 检查库存
        items.forEach { item ->
            val availableStock = inventoryService.getAvailableStock(item.productId)
            if (availableStock < item.quantity) {
                throw BusinessException(ErrorCode.INSUFFICIENT_STOCK, "商品 ${item.productId} 库存不足")
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
            orderId = generateOrderNumber(), // 生成订单号
            userId = userId,
            totalAmount = totalAmount,
            tradeNo = tradeNo,
            status = OrderStatus.PENDING_PAYMENT
        )

        // 5. 创建订单项并与订单关联
        items.forEach { item ->
            val orderItem = OrderItemEntity(
                productId = item.productId,
                quantity = item.quantity,
                price = item.price,
                order = order
            )
            order.addItem(orderItem)
        }

        // 6. 记录订单历史
        orderHistoryService.saveOrderStatusChange(order, OrderStatus.CreateOrder)

        // 7. 保存订单
        return orderRepository.save(order)
    }

    /**
     * 取消订单
     */
    @Transactional
    fun cancelOrder(orderNumber: String) {
        val order = orderRepository.findById(orderNumber)
            .orElseThrow { BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单号 $orderNumber 不存在") }

        if (order.status == OrderStatus.PENDING_PAYMENT) {
            // 1. 更新订单状态
            order.status = OrderStatus.CANCELLED
            orderRepository.save(order)

            // 2. 恢复库存
            order.items.forEach { item ->
                inventoryService.restoreStock(item.productId, item.quantity)
            }
        } else {
            throw BusinessException(ErrorCode.ORDER_STATUS_INVALID, "订单状态不允许取消: ${order.status}")
        }
    }

    /**
     * 支付订单
     */
    @Transactional
    fun payOrder(orderId: String) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单ID $orderId 不存在") }

        if (order.status == OrderStatus.PENDING_PAYMENT) {
            // 1. 更新订单状态
            order.status = OrderStatus.PAID
            orderRepository.save(order)
        } else {
            throw BusinessException(ErrorCode.ORDER_STATUS_INVALID, "订单状态不允许支付: ${order.status}")
        }
    }

    /**
     * 生成订单号
     */
    private fun generateOrderNumber(): String {
        return "ORDER-${System.currentTimeMillis()}-${(1000..9999).random()}"
    }
}
