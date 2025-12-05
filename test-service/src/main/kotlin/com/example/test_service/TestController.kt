package com.example.test_service

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello! I am being tracked."
    }

    @GetMapping("/slow")
    fun slow(): String {
        Thread.sleep(800) // Sleep 800ms to trigger > 500ms alert
        return "Sorry I am late!"
    }
    
    @GetMapping("/error-test")
    fun error(): String {
        throw RuntimeException("Something went wrong!")
    }
}