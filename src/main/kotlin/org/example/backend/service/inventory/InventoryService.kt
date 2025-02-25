package org.example.backend.service.inventory

import org.example.backend.exception.BusinessException
import org.springframework.stereotype.Service

@Service
class InventoryService {

    /**
     * 获取可用库存
     */
    fun getAvailableStock(productId: Long): Int {
        // 模拟从数据库或外部服务获取库存
        return 100 // 假设每个商品有 100 个库存
    }

    /**
     * 扣减库存
     */
    fun reduceStock(productId: Long, quantity: Int) {
        val availableStock = getAvailableStock(productId)
        if (availableStock < quantity) {
            throw BusinessException("库存不足: 商品 $productId")
        }
        // 模拟扣减库存
        println("扣减库存: 商品 $productId, 数量 $quantity")
    }

    /**
     * 恢复库存
     */
    fun restoreStock(productId: Long, quantity: Int) {
        // 模拟恢复库存
        println("恢复库存: 商品 $productId, 数量 $quantity")
    }
}