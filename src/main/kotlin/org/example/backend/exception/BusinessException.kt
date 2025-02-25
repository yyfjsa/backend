package org.example.backend.exception

class BusinessException (
    val errorCode: String,
    override val message: String,
    val details: String? = null
) : RuntimeException(message)