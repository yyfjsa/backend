package org.example.backend.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<Result<Any>> {
        // 创建一个包含错误码和消息的 Result
        val result = org.example.backend.Response.Result<Any>(
            success = false,
            errorCode = ex.errorCode,
            errorMessage = ex.message,
            details = ex.details
        )
        // 这里可以根据需要返回不同的 HTTP 状态码
        return ResponseEntity(result, HttpStatus.BAD_REQUEST)
    }

    // 可以根据需求添加更多的异常处理
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Result<Any>> {
        val result = org.example.backend.Response.Result<Any>(
            success = false,
            errorCode = "UNKNOWN_ERROR",
            errorMessage = ex.message ?: "Unknown error occurred",
            details = null
        )
        return ResponseEntity(result, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}