package com.api.monitor.tracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TrackerClientApplication

fun main(args: Array<String>) {
	runApplication<TrackerClientApplication>(*args)
}
