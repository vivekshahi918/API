package com.api.monitor.collector.controller

import com.api.monitor.collector.model.secondary.User
import com.api.monitor.collector.repository.secondary.UserRepository
import com.api.monitor.collector.security.JwtUtils
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

// Simple DTOs
data class LoginRequest(val username: String, val password: String)
data class JwtResponse(val token: String, val username: String)

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:3000"])
class AuthController(
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils
) {
    private val encoder = BCryptPasswordEncoder()

    @PostMapping("/register")
    fun register(@RequestBody req: LoginRequest): ResponseEntity<String> {
        if (userRepository.findByUsername(req.username) != null) {
            return ResponseEntity.badRequest().body("Username exists")
        }
        // Save user with hashed password
        val user = User(username = req.username, password = encoder.encode(req.password))
        userRepository.save(user)
        return ResponseEntity.ok("User registered successfully")
    }

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): ResponseEntity<Any> {
        val user = userRepository.findByUsername(req.username)
            ?: return ResponseEntity.status(401).body("User not found")

        if (!encoder.matches(req.password, user.password)) {
            return ResponseEntity.status(401).body("Invalid password")
        }

        val token = jwtUtils.generateToken(user.username)
        return ResponseEntity.ok(JwtResponse(token, user.username))
    }
}