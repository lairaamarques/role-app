package com.example.projetorole.data.auth

import android.content.Context
import com.example.projetorole.data.auth.AuthDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object AuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var dataStore: AuthDataStore

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _authEvents = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    @Volatile
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            dataStore = AuthDataStore(context.applicationContext)
            scope.launch {
                dataStore.tokenFlow.collect { _token.value = it }
            }
            initialized = true
        }
    }

    suspend fun setToken(token: String?) {
        ensureInitialized()
        dataStore.setToken(token)
    }

    suspend fun clearToken() = setToken(null)

    val currentToken: String?
        get() = _token.value

    fun notifyUnauthorized() {
        scope.launch { _authEvents.emit(AuthEvent.Unauthorized) }
    }

    private fun ensureInitialized() {
        check(initialized) { "AuthRepository not initialized. Call init(context) first." }
    }

    sealed interface AuthEvent {
        data object Unauthorized : AuthEvent
    }
}