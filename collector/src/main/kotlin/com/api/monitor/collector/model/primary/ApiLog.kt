package com.api.monitor.collector.model.primary

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "api_logs")
data class ApiLog(
    @Id
    val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val method: String,
    val status: Int,
    val durationMs: Long,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val errorMessage: String? = null,
    val isRateLimitHit: Boolean = false
)