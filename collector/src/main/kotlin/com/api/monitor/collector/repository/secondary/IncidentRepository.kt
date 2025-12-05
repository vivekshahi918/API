package com.api.monitor.collector.repository.secondary
import com.api.monitor.collector.model.secondary.Incident
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface IncidentRepository : MongoRepository<Incident, String> {
    fun findByServiceNameAndEndpointAndStatus(service: String, endpoint: String, status: String): Incident?
}