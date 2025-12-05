package com.api.monitor.tracker.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "api.tracker")
class TrackerProperties {
    var collectorUrl: String = "http://localhost:8080/api/collector/log"
    var serviceName: String = "unknown-service"
    var rateLimit: Int = 100 // Default 100 req/sec
}