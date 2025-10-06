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
    }

    val tokenFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.TOKEN]
    }

    suspend fun setToken(token: String?) {
        dataStore.edit { prefs ->
            if (token.isNullOrBlank()) {
                prefs.remove(Keys.TOKEN)
            } else {
                prefs[Keys.TOKEN] = token
            }
        }
    }
}