package org.example.backend.Response

data class ErrorResponse(
    val code: String, // 错误码
    val message: String, // 错误信息
    val details: String? = null // 错误详情
)