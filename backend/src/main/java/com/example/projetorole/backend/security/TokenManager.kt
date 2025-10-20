package com.example.projetorole.backend.security

import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

enum class SubjectType { USER, ESTAB }

data class TokenPayload(
    val subjectId: Int,
    val subjectType: SubjectType,
    val expiresAt: Long
)

object TokenManager {
    private val tokens = ConcurrentHashMap<String, TokenPayload>()
    private val ttlMillis = Duration.ofHours(2).toMillis()

    fun createToken(subjectId: Int, subjectType: SubjectType): String {
        val token = UUID.randomUUID().toString()
        tokens[token] = TokenPayload(
            subjectId = subjectId,
            subjectType = subjectType,
            expiresAt = System.currentTimeMillis() + ttlMillis
        )
        return token
    }

    fun getPayload(token: String?): TokenPayload? {
        if (token.isNullOrBlank()) return null
        cleanupExpired()
        val payload = tokens[token] ?: return null
        return if (payload.expiresAt > System.currentTimeMillis()) payload else {
            tokens.remove(token); null
        }
    }

    fun invalidate(token: String) {
        tokens.remove(token)
    }

    fun validate(token: String?): Boolean = getPayload(token) != null

    private fun cleanupExpired() {
        val now = System.currentTimeMillis()
        tokens.entries.removeIf { it.value.expiresAt <= now }
    }
}