package com.example.test_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// 🔥 This line is CRITICAL. It loads YOUR code + the Tracker Library.
@SpringBootApplication(scanBasePackages = ["com"])
class TestServiceApplication

fun main(args: Array<String>) {
    runApplication<TestServiceApplication>(*args)
}