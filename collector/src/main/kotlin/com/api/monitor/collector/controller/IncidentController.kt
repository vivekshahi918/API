package com.api.monitor.collector.controller

import com.api.monitor.collector.model.secondary.Incident
import com.api.monitor.collector.service.IncidentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/collector/incidents")
@CrossOrigin(origins = ["http://localhost:3000"])
class IncidentController(private val incidentService: IncidentService) {

    @GetMapping
    fun getIncidents(): ResponseEntity<List<Incident>> {
        return ResponseEntity.ok(incidentService.getOpenIncidents())
    }

    @PostMapping("/{id}/resolve")
    fun resolveIncident(@PathVariable id: String): ResponseEntity<String> {
        return try {
            incidentService.resolveIncident(id)
            ResponseEntity.ok("Incident Resolved")
        } catch (e: Exception) {
            ResponseEntity.status(409).body("Conflict: Incident was updated by someone else!")
        }
    }
}