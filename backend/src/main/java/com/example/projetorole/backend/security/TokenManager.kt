package com.example.projetorole.backend.security

import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class TokenPayload(val userId: Int, val expiresAt: Long)

object TokenManager {
    private val tokens = ConcurrentHashMap<String, TokenPayload>()
    private val ttlMillis = Duration.ofHours(2).toMillis()

    fun createToken(userId: Int): String {
        val token = UUID.randomUUID().toString()
        tokens[token] = TokenPayload(userId, System.currentTimeMillis() + ttlMillis)
        return token
    }

    fun getUserId(token: String?): Int? {
        if (token.isNullOrBlank()) return null
        cleanupExpired()
        val payload = tokens[token] ?: return null
        return if (payload.expiresAt > System.currentTimeMillis()) payload.userId else {
            tokens.remove(token)
            null
        }
    }

    fun invalidate(token: String) {
        tokens.remove(token)
    }

    fun validate(token: String?): Boolean = getUserId(token) != null

    private fun cleanupExpired() {
        val now = System.currentTimeMillis()
        tokens.entries.removeIf { it.value.expiresAt <= now }
    }
}