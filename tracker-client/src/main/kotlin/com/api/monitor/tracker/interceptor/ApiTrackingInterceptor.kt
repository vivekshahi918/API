package com.api.monitor.tracker.interceptor

import com.api.monitor.tracker.config.TrackerProperties
import com.api.monitor.tracker.service.RateLimiter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.client.RestTemplate
import java.lang.Exception
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Component
class ApiTrackingInterceptor(
    private val properties: TrackerProperties,
    private val rateLimiter: RateLimiter
) : HandlerInterceptor {

    private val restTemplate = RestTemplate()

    init {
        println("💣💣💣 TRACKER CLIENT INTERCEPTOR IS INITIALIZED 💣💣💣")
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        println("👀 Spy detected a request to: ${request.requestURI}")
        request.setAttribute("startTime", System.currentTimeMillis())
        
        // Check Rate Limit (Non-blocking)
        if (rateLimiter.isRateLimitExceeded()) {
            request.setAttribute("rateLimitHit", true)
        } else {
            request.setAttribute("rateLimitHit", false)
        }
        
        return true // Always allow request to proceed
    }

    override fun afterCompletion(
        request: HttpServletRequest, 
        response: HttpServletResponse, 
        handler: Any, 
        ex: Exception?
    ) {
        val startTime = request.getAttribute("startTime") as Long
        val duration = System.currentTimeMillis() - startTime
        val isRateLimitHit = request.getAttribute("rateLimitHit") as Boolean

         println("📤 Sending log for ${request.requestURI}...")

        // Prepare the payload
        val logPayload = mapOf(
            "serviceName" to properties.serviceName,
            "endpoint" to request.requestURI,
            "method" to request.method,
            "status" to response.status,
            "durationMs" to duration,
            "timestamp" to LocalDateTime.now().toString(),
            "rateLimitHit" to isRateLimitHit
        )

        // Send to Collector Asynchronously (So we don't slow down the main app)
        CompletableFuture.runAsync {
            try {
                val res = restTemplate.postForEntity(properties.collectorUrl, logPayload, String::class.java)
                println("✅ Log Sent! Response: ${res.statusCode}")
            } catch (e: Exception) {
                println("Failed to send log to collector: ${e.message}")
            }
        }
    }
}