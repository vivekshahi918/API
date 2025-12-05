package com.api.monitor.collector.repository.secondary

import com.api.monitor.collector.model.secondary.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByUsername(username: String): User?
}