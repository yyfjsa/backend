package org.example.backend.service

import org.example.backend.exception.BusinessException
import org.example.backend.model.enums.ErrorCode
import org.example.backend.repository.jpa.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryService(private val productRepository: ProductRepository) {

    /**
     * 获取商品库存
     */
    fun getAvailableStock(productId: Long): Int {
        val product = productRepository.findById(productId)
            .orElseThrow { BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品ID: $productId") }
        return product.stockQuantity
    }

    /**
     * 扣减库存
     */
    @Transactional
    fun reduceStock(productId: Long, quantity: Int) {
        val product = productRepository.findById(productId)
            .orElseThrow { BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品ID: $productId") }

        if (product.stockQuantity < quantity) {
            throw BusinessException(ErrorCode.INSUFFICIENT_STOCK, "商品ID: $productId, 需要 $quantity, 现有 ${product.stockQuantity}")
        }

        // 更新库存
        product.updateStock(-quantity)
        productRepository.save(product)
    }

    /**
     * 恢复库存
     */
    @Transactional
    fun restoreStock(productId: Long, quantity: Int) {
        val product = productRepository.findById(productId)
            .orElseThrow { BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品ID: $productId") }

        // 恢复库存
        product.updateStock(quantity)
        productRepository.save(product)
    }

    /**
     * 增加库存
     */
    @Transactional
    fun addStock(productId: Long, quantity: Int) {
        val product = productRepository.findById(productId)
            .orElseThrow { BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品ID: $productId") }

        // 增加库存
        product.updateStock(quantity)
        productRepository.save(product)
    }
}
