package org.example.backend.service

import org.example.backend.dto.OrderItemDto
import org.example.backend.exception.BusinessException
import org.example.backend.model.entity.OrderEntity
import org.example.backend.model.enums.ErrorCode
import org.example.backend.model.enums.OrderStatus
import org.example.backend.repository.jpa.OrderRepository
import org.hibernate.validator.internal.util.Contracts.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import kotlin.test.Test

class OrderServiceTest {
    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var inventoryService: InventoryService

    @Mock
    private lateinit var orderHistoryService: OrderHistoryService

    @InjectMocks
    private lateinit var orderService: OrderService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `createOrder should succeed when stock is sufficient`() {
        // 准备数据
        val userId = 1L
        val items = listOf(
            OrderItemDto(productId = 101, quantity = 2, price = BigDecimal("50.00"))
        )
        val order = OrderEntity(
            orderId = "ORDER-123456",
            userId = userId,
            totalAmount = BigDecimal("100.00"),
            status = OrderStatus.PENDING_PAYMENT
        )

        // 模拟依赖行为
        `when`(inventoryService.getAvailableStock(101)).thenReturn(10)
        `when`(orderRepository.save(any())).thenReturn(order)

        // 调用方法
        val result = orderService.createOrder(userId, items)

        // 验证结果
        assertNotNull(result)
        assertEquals("ORDER-123456", result.orderId)
        assertEquals(OrderStatus.PENDING_PAYMENT, result.status)
        verify(inventoryService, times(1)).reduceStock(101, 2)
        verify(orderRepository, times(1)).save(any())
    }

    @Test
    fun `createOrder should fail when stock is insufficient`() {
        // 准备数据
        val userId = 1L
        val items = listOf(
            OrderItemDto(productId = 101, quantity = 10, price = BigDecimal("50.00"))
        )

        // 模拟依赖行为
        `when`(inventoryService.getAvailableStock(101)).thenReturn(5)

        // 调用方法并验证异常
        val exception = assertThrows<BusinessException> {
            orderService.createOrder(userId, items)
        }

        // 验证异常信息
        assertEquals(ErrorCode.INSUFFICIENT_STOCK.code, exception.code)
        assertEquals(ErrorCode.INSUFFICIENT_STOCK.message, exception.message)
        assertEquals("商品 101 库存不足", exception.details)
    }

    @Test
    fun `cancelOrder should succeed when order is pending payment`() {
        // 准备数据
        val orderNumber = "ORDER-123456"
        val order = OrderEntity(
            orderId = orderNumber,
            userId = 1L,
            totalAmount = BigDecimal("100.00"),
            status = OrderStatus.PENDING_PAYMENT
        )

        // 模拟依赖行为
        `when`(orderRepository.findById(orderNumber)).thenReturn(order)

        // 调用方法
        orderService.cancelOrder(orderNumber)

        // 验证结果
        assertEquals(OrderStatus.CANCELLED, order.status)
        verify(inventoryService, times(1)).restoreStock(any(), any())
        verify(orderRepository, times(1)).save(order)
    }

    @Test
    fun `cancelOrder should fail when order is not found`() {
        // 准备数据
        val orderNumber = "ORDER-123456"

        // 模拟依赖行为
        `when`(orderRepository.findById(orderNumber)).thenReturn(null)

        // 调用方法并验证异常
        val exception = assertThrows<BusinessException> {
            orderService.cancelOrder(orderNumber)
        }

        // 验证异常信息
        assertEquals(ErrorCode.ORDER_NOT_FOUND.code, exception.code)
        assertEquals(ErrorCode.ORDER_NOT_FOUND.message, exception.message)
        assertEquals("订单号 ORDER-123456 不存在", exception.details)
    }
}