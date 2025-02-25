package org.example.backend.mq.producer

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class PaymentStatusProducer(private val rabbitTemplate:RabbitTemplate) {
    fun sendPaymentStatusUpdate(orderId:Long,status:String){
        val message=mapOf(
            "orderId" to orderId,
            "status" to status
        )
        rabbitTemplate.convertAndSend("paymentStatusUpdate", "payment.status.routingKey",message)
    }
}
