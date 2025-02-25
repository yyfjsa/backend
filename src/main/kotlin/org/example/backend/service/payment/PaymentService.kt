package org.example.backend.service.payment

import com.alipay.api.AlipayApiException
import com.alipay.api.AlipayClient
import com.alipay.api.DefaultAlipayClient
import com.alipay.api.request.AlipayTradeQueryRequest
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.example.backend.exception.BusinessException
import org.example.backend.integration.alipay.AlipayConfig
import org.example.backend.mq.producer.PaymentStatusProducer
import org.example.backend.repository.jpa.OrderRepository
import org.example.backend.repository.jpa.PaymentRepository

@Service
class PaymentService(private val orderRepository:OrderRepository,
                     private val paymentRepository:PaymentRepository,
                     private val paymentStatusProducer:PaymentStatusProducer,
                     private val alipayConfig:AlipayConfig) {
    private  val alipayClient:AlipayClient=DefaultAlipayClient(
        alipayConfig.gatewayUrl,
        alipayConfig.appId,
        alipayConfig.privateKey,
        alipayConfig.format,
        alipayConfig.charset,
        alipayConfig.alipayPublicKey,
        alipayConfig.signType
    )
    fun verifyAlipayCallback(params:Map<String,String>):Boolean{
        try {
            return aliapyClient.checkNotifySign(params)
        }catch (e: AlipayApiException){
            throw BusinessException("支付宝回调验证失败: ${e.message}")
        }
    }
    @Transactional
    fun processPaymentResult(tradeNo:String,tradeStatus:String){
        val order =orderRepository.findByOrderNumber(tradeNo)?:throw BusinessException("订单不存在: ${e.message}")
        when(tradeStatus){
            "TRADE_SUCCESS" -> order.status=OrderStatus.PAID
            "TRADE_CLOSED" -> order.status=OrderStatus.CANCELLED
            else-> throw BusinessException("未知的交易状态: $tradeStatus")
        }
        val payment =PaymentEntity(
            orderId = order.id,
            amount= order.totalAmount,
            paymentMethod="ALIPAY",
            transactionId = tradeNo,
            status = tradeStatus
        )
        paymentRepository.save(payment)
        paymentStatusProducer.sendPaymentStatusUpdate(order.id,tradeStatus)
    }
    fun queryAlipayPaymentStatus(tradeNo:String):String{
        val request=AlipayTradeQueryRequest()
        request.bizContent="{\"out_trade_no\":\"$tradeNo\"}"
        try {
            val response:AlipayTradeQueryRequest=alipayClient.execute(request)
            return response.tradeStatus?:throw BusinessException("支付宝查询失败：无交易状态")
        }catch (e:AlipayApiException){
            throw BusinessException("支付宝查询失败: ${e.message}")
        }
    }
    @Transactional
    fun handleTimeoutPayment(orderId:Long){
        val order =orderRepository.findById(orderId).orElseThrow{BusinessException("订单不存在:$orderId")}
        if(order.status==OrderStatus.PENDING_PAYMENT){
            order.status=OrderStatus.CANCELLED
            orderRepository.save(order)
            paymentRepository.sendPaymentStatusUpdate(order.id,"TRADE_CLOSED")
        }
    }
}
