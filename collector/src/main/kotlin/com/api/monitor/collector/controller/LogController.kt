package com.api.monitor.collector.controller

import com.api.monitor.collector.dto.LogRequest
import com.api.monitor.collector.service.LogService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/collector")   
public class LogController(private val logService: LogService) {

    @PostMapping("/log")
    fun ingestLog(@RequestBody request: LogRequest): ResponseEntity<String> {
        
        println("✅ Log received from: ${request.serviceName} - ${request.endpoint}")

        logService.processLog(request)
        return ResponseEntity.ok("Log received")
    }
    @GetMapping("/logs")
    fun getLogs(): ResponseEntity<List<com.api.monitor.collector.model.primary.ApiLog>> {
        return ResponseEntity.ok(logService.getAllLogs())
    }
}