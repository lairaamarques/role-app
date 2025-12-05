package com.example.projetorole.data.auth

import android.content.Context
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

    private val _actorType = MutableStateFlow<ActorType?>(null)
    val actorType: StateFlow<ActorType?> = _actorType.asStateFlow()

    private val _authEvents = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    data class AuthProfile(val displayName: String?, val email: String?, val photoUrl: String?)

    private val _profile = MutableStateFlow<AuthProfile?>(null)
    val profile: StateFlow<AuthProfile?> = _profile.asStateFlow()

    @Volatile
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            dataStore = AuthDataStore(context.applicationContext)
            scope.launch {
                launch { dataStore.tokenFlow.collect { _token.value = it } }
                launch { dataStore.actorTypeFlow.collect { _actorType.value = it } }
                launch { dataStore.profileFlow.collect { data ->
                    _profile.value = data?.let { AuthProfile(it.displayName, it.email, it.photoUrl) }
                } }
            }
            initialized = true
        }
    }

    suspend fun setSession(token: String?, actorType: ActorType?, displayName: String?, email: String?, photoUrl: String? = null) {
        ensureInitialized()
        _profile.value = AuthProfile(displayName, email, photoUrl)
        dataStore.setSession(token, actorType, displayName, email, photoUrl)
    }

    suspend fun setToken(token: String?) {
        ensureInitialized()
        val currentProfile = _profile.value
        dataStore.setSession(token, _actorType.value, currentProfile?.displayName, currentProfile?.email, currentProfile?.photoUrl)
    }

    suspend fun clearToken() {
        ensureInitialized()
        _profile.value = null
        dataStore.setSession(null, null, null, null, null)
    }

    val currentToken: String? get() = _token.value
    val currentActorType: ActorType? get() = _actorType.value

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