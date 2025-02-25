package org.example.backend.repository.jpa

import org.example.backend.model.entity.PaymentEntity
import org.example.backend.model.enums.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PaymentRepository : JpaRepository<PaymentEntity, Long> {

    /**
     * 根据订单ID查找支付记录
     */
    fun findByOrderId(orderId: Long): PaymentEntity?

    /**
     * 根据交易号查找支付记录
     */
    fun findByTransactionId(transactionId: String): PaymentEntity?

    /**
     * 根据支付状态查找支付记录列表
     */
    fun findByStatus(status: PaymentStatus): List<PaymentEntity>

    /**
     * 更新支付状态
     */
    @Modifying
    @Query("UPDATE PaymentEntity p SET p.status = :status, p.updatedAt = :updatedAt WHERE p.id = :paymentId")
    fun updatePaymentStatus(
        @Param("paymentId") paymentId: Long,
        @Param("status") status: PaymentStatus,
        @Param("updatedAt") updatedAt: LocalDateTime = LocalDateTime.now()
    ): Int

    /**
     * 根据订单ID更新支付状态
     */
    @Modifying
    @Query("UPDATE PaymentEntity p SET p.status = :status, p.updatedAt = :updatedAt WHERE p.orderId = :orderId")
    fun updatePaymentStatusByOrderId(
        @Param("orderId") orderId: Long,
        @Param("status") status: PaymentStatus,
        @Param("updatedAt") updatedAt: LocalDateTime = LocalDateTime.now()
    ): Int
}
