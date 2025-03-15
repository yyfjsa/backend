package org.example.backend.repository.jpa

import org.example.backend.model.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
interface ProductRepository:JpaRepository<ProductEntity,Long> {
    @Query("SELECT p.stockQuantity FROM ProductEntity p WHERE p.productId = :productId")
    fun getStockQuantity(@Param("productId") productId: Long): Int
    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.productId = :productId AND p.stockQuantity >= :quantity")
    fun reduceStock(@Param("productId") productId: Long, @Param("quantity") quantity: Int): Int
    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.stockQuantity = p.stockQuantity - :quantity,p.updatedAt=CURRENT_TIMESTAMP WHERE p.productId = :productId AND p.stockQuantity>=:quantity")
    fun restoreStock(productId: Long,quantity:Int):Int
    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.productId = :productId")
    fun addStock(@Param("productId") productId: Long, @Param("quantity") quantity: Int): Int

}