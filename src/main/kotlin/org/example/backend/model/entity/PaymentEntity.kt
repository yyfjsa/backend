package org.example.backend.model.entity

import jakarta.persistence.*
import org.example.backend.model.enums.PaymentStatus
import java.math.BigDecimal
import java.time.LocalDateTime


@Entity
@Table(name = "payments")
class PaymentEntity(
    @Id
    @Column(name="payment_id", nullable = false)
    var paymentId:String,

    @Column(name = "order_id", nullable = false)
    var orderId: String, // 关联的订单ID

    @Column(name = "amount", nullable = false)
    var amount: BigDecimal, // 支付金额

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus = PaymentStatus.PENDING, // 支付状态，默认为待支付

    @Column(name = "payment_method", nullable = false)
    var paymentMethod: String, // 支付方式（如 ALIPAY, WECHAT_PAY）

    @Column(name = "transaction_id", nullable = true)
    var transactionId: String? = null, // 第三方支付交易号

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(), // 创建时间，默认为当前时间

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now() // 更新时间，默认为当前时间
) : BaseEntity() // 继承 BaseEntity 包含公共字段（如 id）
{
    /**
     * 更新支付状态
     */
    fun updateStatus(newStatus: PaymentStatus) {
        this.status = newStatus
        this.updatedAt = LocalDateTime.now()
    }
}