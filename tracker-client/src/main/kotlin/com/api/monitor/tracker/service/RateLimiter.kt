package com.api.monitor.tracker.service

import com.api.monitor.tracker.config.TrackerProperties
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.time.Instant

@Component
class RateLimiter(private val properties: TrackerProperties) {

    // Simple window counter: Map<SecondTimestamp, RequestCount>
    private val requestCounts = ConcurrentHashMap<Long, AtomicInteger>()

    fun isRateLimitExceeded(): Boolean {
        val currentSecond = Instant.now().epochSecond
        
        // clean up old entries (optional optimization would be a background job)
        requestCounts.keys.removeIf { it < currentSecond }

        val counter = requestCounts.computeIfAbsent(currentSecond) { AtomicInteger(0) }
        val currentCount = counter.incrementAndGet()

        return currentCount > properties.rateLimit
    }
}