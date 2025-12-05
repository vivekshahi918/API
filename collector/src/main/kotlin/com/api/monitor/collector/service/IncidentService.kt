package com.api.monitor.collector.service

import com.api.monitor.collector.model.secondary.Incident
import com.api.monitor.collector.repository.secondary.IncidentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IncidentService(private val incidentRepository: IncidentRepository) {

    // 1. Get all Open Incidents
    fun getOpenIncidents(): List<Incident> {
        return incidentRepository.findAll().filter { it.status == "OPEN" }
    }

    // 2. Resolve an Incident (With Optimistic Locking Safety)
    @Transactional
    fun resolveIncident(id: String) {
        val incident = incidentRepository.findById(id).orElseThrow { 
            RuntimeException("Incident not found") 
        }
        
        incident.status = "RESOLVED"
        
        // Spring Data MongoDB checks the @Version field here automatically.
        // If someone else modified it meanwhile, this will throw an OptimisticLockingFailureException
        incidentRepository.save(incident)
    }
}