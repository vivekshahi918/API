package com.api.monitor.collector.repository.primary
import com.api.monitor.collector.model.primary.ApiLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ApiLogRepository : MongoRepository<ApiLog, String> {
    // Add custom queries here if needed, e.g., findByServiceName
}