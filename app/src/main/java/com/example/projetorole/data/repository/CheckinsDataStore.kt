package com.example.projetorole.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.checkinsDataStore by preferencesDataStore(name = "checkins_prefs")

object CheckinsDataStore {
    private val KEY = stringPreferencesKey("checkins_ids_csv")

    fun getSavedIdsFlow(context: Context): Flow<List<Int>> =
        context.checkinsDataStore.data
            .map { prefs ->
                prefs[KEY].orEmpty()
                    .split(",")
                    .mapNotNull { it.trim().takeIf { s -> s.isNotBlank() }?.toIntOrNull() }
            }

    suspend fun saveIds(context: Context, ids: List<Int>) {
        context.checkinsDataStore.edit { prefs ->
            prefs[KEY] = ids.joinToString(",")
        }
    }
}