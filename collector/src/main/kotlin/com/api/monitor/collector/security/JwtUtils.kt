package com.api.monitor.collector.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtils {

    // 🔥 This is a secret key. In prod, use environment variable!
    private val jwtSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
    private val jwtExpirationMs = 86400000 // 1 day

    private fun getSignInKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(jwtSecret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(authToken: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(authToken)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserFromToken(token: String): String {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build()
            .parseClaimsJws(token).body.subject
    }
}