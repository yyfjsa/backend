package org.example.backend.mq.producer

import org.example.backend.exception.BusinessException
import org.example.backend.model.enums.ErrorCode
import org.example.backend.repository.jpa.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class PaymentStatusProducer(
    private val rabbitTemplate: RabbitTemplate,
    private val orderRepository: OrderRepository
) {
    private val logger = LoggerFactory.getLogger(PaymentStatusProducer::class.java)

    fun sendPaymentStatusUpdate(orderId: String, status: String) {
        // 检查订单是否存在
        val order = orderRepository.findById(orderId).orElseThrow {
            BusinessException(ErrorCode.ORDER_NOT_FOUND,"订单ID：$orderId")
        }

        val message = mapOf(
            "orderId" to order.id,
            "status" to status
        )

        try {
            rabbitTemplate.convertAndSend("payment.exchange", "payment.status", message)
            logger.info("成功发送支付状态更新消息: $message")
        } catch (e: Exception) {
            logger.error("发送支付状态消息失败: ${e.message}", e)
            throw BusinessException(ErrorCode.INTERNAL_ERROR, "支付状态消息发送失败: ${e.message}")
        }
    }
}
