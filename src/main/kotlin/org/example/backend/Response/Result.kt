package org.example.backend.Response

data class Result<T>(
    val success: Boolean,       // 是否成功
    val errorCode: String? = null, // 错误码，如果有错误则必填
    val errorMessage: String? = null, // 错误信息
    val details: String? = null, // 错误详情（可选）
    val data: T? = null // 成功时返回的数据（泛型）
)