package com.example.projetorole.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth_preferences")

class AuthDataStore(context: Context) {

    private val dataStore = context.authDataStore

    private object Keys {
        val TOKEN = stringPreferencesKey("auth_token")
        val ACTOR = stringPreferencesKey("auth_actor_type")
        val DISPLAY_NAME = stringPreferencesKey("auth_display_name")
        val EMAIL = stringPreferencesKey("auth_email")
    }

    val profileFlow: Flow<AuthProfileData?> = dataStore.data.map { prefs ->
        val name = prefs[Keys.DISPLAY_NAME]
        val email = prefs[Keys.EMAIL]
        if (name.isNullOrBlank() && email.isNullOrBlank()) null else AuthProfileData(name, email)
    }

    val tokenFlow: Flow<String?> = dataStore.data.map { it[Keys.TOKEN] }
    val actorTypeFlow: Flow<ActorType?> = dataStore.data.map { prefs ->
        prefs[Keys.ACTOR]?.let { runCatching { ActorType.valueOf(it) }.getOrNull() }
    }

    suspend fun setSession(token: String?, actorType: ActorType?, displayName: String?, email: String?) {
        dataStore.edit { prefs ->
            if (token.isNullOrBlank()) prefs.remove(Keys.TOKEN) else prefs[Keys.TOKEN] = token
            if (actorType == null) prefs.remove(Keys.ACTOR) else prefs[Keys.ACTOR] = actorType.name
            if (displayName.isNullOrBlank()) prefs.remove(Keys.DISPLAY_NAME) else prefs[Keys.DISPLAY_NAME] = displayName
            if (email.isNullOrBlank()) prefs.remove(Keys.EMAIL) else prefs[Keys.EMAIL] = email
        }
    }
}

data class AuthProfileData(val displayName: String?, val email: String?)