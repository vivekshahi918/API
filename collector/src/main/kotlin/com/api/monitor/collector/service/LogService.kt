package com.api.monitor.collector.service

import com.api.monitor.collector.dto.LogRequest
import com.api.monitor.collector.model.primary.ApiLog
import com.api.monitor.collector.model.secondary.Incident
import com.api.monitor.collector.repository.primary.ApiLogRepository
import com.api.monitor.collector.repository.secondary.IncidentRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class LogService(
    private val apiLogRepository: ApiLogRepository,
    private val incidentRepository: IncidentRepository
) {

    fun processLog(request: LogRequest) {
        // 1. Save Raw Log to Primary DB
        val log = ApiLog(
            serviceName = request.serviceName,
            endpoint = request.endpoint,
            method = request.method,
            status = request.status,
            durationMs = request.durationMs,
            timestamp = request.timestamp ?: LocalDateTime.now(),
            errorMessage = request.errorMessage,
            isRateLimitHit = request.rateLimitHit
        )
        apiLogRepository.save(log)

        // 2. Alerting Logic (Check for Slow or Broken APIs)
        // Rule 1: Latency > 500ms
        if (request.durationMs > 500) {
            createOrUpdateIncident(request, "SLOW")
        }

        // Rule 2: Status 5xx (Server Error)
        if (request.status >= 500) {
            createOrUpdateIncident(request, "ERROR")
        }
        
        // Rule 3: Rate Limit Hit
        if (request.rateLimitHit) {
             createOrUpdateIncident(request, "RATE_LIMIT")
        }
    }
    fun getAllLogs(): List<ApiLog> {
        return apiLogRepository.findAll()
    }

    // Helper to save to Secondary DB
    private fun createOrUpdateIncident(request: LogRequest, type: String) {
        // Check if an OPEN incident already exists for this service+endpoint
        val existingIncident = incidentRepository.findByServiceNameAndEndpointAndStatus(
            request.serviceName, 
            request.endpoint, 
            "OPEN"
        )

        if (existingIncident == null) {
            val newIncident = Incident(
                serviceName = request.serviceName,
                endpoint = request.endpoint,
                type = type,
                status = "OPEN",
                detectedAt = LocalDateTime.now()
            )
            incidentRepository.save(newIncident)
        } else {
            // Optional: Update 'lastOccurred' timestamp if you add that field later
            // For now, we just ensure it's recorded.
        }
    }
}