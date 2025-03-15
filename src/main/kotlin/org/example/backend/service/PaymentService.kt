package org.example.backend.service

import com.alipay.api.AlipayApiException
import com.alipay.api.AlipayClient
import com.alipay.api.DefaultAlipayClient
import com.alipay.api.internal.util.AlipaySignature
import com.alipay.api.request.AlipayTradeQueryRequest
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.example.backend.exception.BusinessException
import org.example.backend.integration.alipay.AlipayConfig
import org.example.backend.mq.producer.PaymentStatusProducer
import org.example.backend.repository.jpa.OrderRepository
import org.example.backend.repository.jpa.PaymentRepository
import org.example.backend.model.entity.PaymentEntity
import org.example.backend.model.enums.ErrorCode
import org.example.backend.model.enums.OrderStatus

@Service
class PaymentService(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentStatusProducer: PaymentStatusProducer,
    private val alipayConfig: AlipayConfig,
) {

    private val alipayClient: AlipayClient = DefaultAlipayClient(
        alipayConfig.gatewayUrl,
        alipayConfig.appId,
        alipayConfig.privateKey,
        alipayConfig.format,
        alipayConfig.charset,
        alipayConfig.alipayPublicKey,
        alipayConfig.signType
    )

    // 校验支付宝回调
    fun verifyAlipayCallback(params: Map<String, String>): Boolean {
        try {
            // 获取支付宝的公钥
            val alipayPublicKey = alipayConfig.alipayPublicKey

            // 校验支付宝回调通知签名
            val signVerified = AlipaySignature.rsaCheckV2(
                params,
                alipayPublicKey,
                alipayConfig.charset,
                alipayConfig.signType
            )

            if (!signVerified) {
                throw BusinessException(
                    ErrorCode.PAYMENT_FAILED, // 使用你定义的错误码
                    "支付宝回调签名验证失败"
                )
            }

            return signVerified
        } catch (e: Exception) {
            throw BusinessException(
                ErrorCode.INTERNAL_ERROR, // 处理未知异常
                "支付宝回调验证失败: ${e.message}"
            )
        }
    }


    // 处理支付结果
    @Transactional
    fun processPaymentResult(tradeNo: String, tradeStatus: String) {
        val order = orderRepository.findByTradeNo(tradeNo)
            ?: throw BusinessException(ErrorCode.ORDER_NOT_FOUND,"订单不存在: $tradeNo")
        when (tradeStatus) {
            "TRADE_SUCCESS" -> order.status = OrderStatus.PAID
            "TRADE_CLOSED" -> order.status = OrderStatus.CANCELLED
            else -> throw BusinessException(ErrorCode.ORDER_STATUS_INVALID,"未知的交易状态: $tradeStatus")
        }

        // 创建并保存支付记录
        val payment = PaymentEntity(
            orderId = order.orderId,
            amount = order.totalAmount,
            paymentMethod = "ALIPAY",
            transactionId = tradeNo,
            status = PaymentStatus.valueOf(tradeStatus)
        )
        paymentRepository.save(payment)

        // 发送支付状态更新消息
        paymentStatusProducer.sendPaymentStatusUpdate(order.id, tradeStatus)
    }

    // 查询支付宝支付状态
    fun queryAlipayPaymentStatus(tradeNo: String): String {
        val request = AlipayTradeQueryRequest()
        request.bizContent = "{\"out_trade_no\":\"$tradeNo\"}"

        try {
            val response = alipayClient.execute(request)
            return response.tradeStatus ?: throw BusinessException("支付宝查询失败：无交易状态")
        } catch (e: AlipayApiException) {
            throw BusinessException("支付宝查询失败: ${e.message}")
        }
    }

    // 处理支付超时
    @Transactional
    fun handleTimeoutPayment(orderId: Long) {
        val order = orderRepository.findById(orderId).orElseThrow {
            BusinessException("订单不存在: $orderId")
        }

        if (order.status == OrderStatus.PENDING_PAYMENT) {
            order.status = OrderStatus.CANCELLED
            orderRepository.save(order)

            // 发送支付状态更新
            paymentStatusProducer.sendPaymentStatusUpdate(order.id, "TRADE_CLOSED")
        }
    }
}
