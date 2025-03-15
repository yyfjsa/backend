package org.example.backend.exception

import org.example.backend.model.enums.ErrorCode

class BusinessException : RuntimeException {
    val code: String // 错误码
    val details: String? // 详细信息

    constructor(errorCode: ErrorCode, details: String? = null) : super(
        if (details != null) "${errorCode.message}: $details" else errorCode.message
    ) {
        this.code = errorCode.code
        this.details = details
    }

    constructor(code: String, message: String, details: String? = null) : super(
        if (details != null) "$message: $details" else message
    ) {
        this.code = code
        this.details = details
    }
}
