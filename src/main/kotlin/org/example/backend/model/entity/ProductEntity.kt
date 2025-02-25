package org.example.backend.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.Id
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
class ProductEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    var productId: Long,
    @Column(name = "name", nullable = false, length = 100)
    var name: String, // 商品名称

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null, // 商品描述（可为空）

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    var price: BigDecimal, // 商品单价

    @Column(name = "stock_quantity", nullable = false)
    var stockQuantity: Int = 0, // 库存数量（默认0）

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(), // 创建时间

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now() // 更新时间

) : BaseEntity() {

    /**
     * 更新库存数量
     */
    fun updateStock(delta: Int) {
        val newStock = stockQuantity + delta
        if (newStock < 0) {
            throw IllegalArgumentException("库存数量不能为负数")
        }
        stockQuantity = newStock
        updatedAt = LocalDateTime.now()
    }
}