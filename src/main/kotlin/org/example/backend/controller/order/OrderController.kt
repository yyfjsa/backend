package org.example.backend.controller.order

import org.example.backend.Request.CreateOrderRequest
import org.example.backend.model.entity.OrderEntity
import org.example.backend.service.OrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): OrderEntity {
        return orderService.createOrder(request.userId, request.items)
    }
}
