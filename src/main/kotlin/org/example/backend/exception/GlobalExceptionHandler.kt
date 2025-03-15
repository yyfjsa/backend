package org.example.backend.exception

import org.example.backend.Response.ErrorResponse
import org.example.backend.model.enums.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.slf4j.LoggerFactory

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ErrorResponse> {
        logger.warn("业务异常: ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                code = ex.code,
                message = ex.message ?: "业务异常",
                details = ex.details
            )
        )
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("系统异常: ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                code = ErrorCode.INTERNAL_ERROR.code,
                message = ErrorCode.INTERNAL_ERROR.message,
                details = null // 不暴露内部异常信息，防止安全风险
            )
        )
    }
}
