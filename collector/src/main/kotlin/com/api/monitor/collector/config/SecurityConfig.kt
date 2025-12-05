package com.api.monitor.collector.config

import com.api.monitor.collector.security.AuthTokenFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class SecurityConfig(private val authTokenFilter: AuthTokenFilter) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource { 
                val cors = CorsConfiguration()
                cors.allowedOriginPatterns = listOf("*")
                cors.allowedMethods = listOf("*")
                cors.allowedHeaders = listOf("*")
                cors.allowCredentials = true
                cors
            }}
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/**").permitAll()       // Login/Register is open
                    .requestMatchers("/api/collector/log").permitAll() // Tracker Client is open
                    .anyRequest().authenticated()                      // EVERYTHING ELSE IS LOCKED 🔒
            }
            .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            
        return http.build()
    }
}