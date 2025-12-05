package com.api.monitor.collector.dto

import java.time.LocalDateTime

data class LogRequest(
    val serviceName: String,
    val endpoint: String,
    val method: String,
    val status: Int,
    val durationMs: Long,
    val timestamp: LocalDateTime? = null,
    val errorMessage: String? = null,
    val rateLimitHit: Boolean = false
)