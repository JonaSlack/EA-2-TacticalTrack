package cl.duoc.tacticaltrack.data.repository

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(
    private val context: Context
) {
    private val lastUsernameKey = stringPreferencesKey("last_username")

    val lastUsername: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[lastUsernameKey].orEmpty()
    }

    suspend fun saveLastUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[lastUsernameKey] = username
        }
    }
}
