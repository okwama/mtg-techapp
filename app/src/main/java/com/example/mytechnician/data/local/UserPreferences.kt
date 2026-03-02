package com.example.mytechnician.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mytechnician.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class UserPreferences(private val context: Context) {
    
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_KEY = stringPreferencesKey("user_data")
    }
    
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }
    
    val userData: Flow<User?> = context.dataStore.data.map { preferences ->
        preferences[USER_KEY]?.let { userJson ->
            try {
                Json.decodeFromString<User>(userJson)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    suspend fun saveAuthData(token: String? = null, user: User? = null) {
        context.dataStore.edit { preferences ->
            token?.let { preferences[TOKEN_KEY] = it }
            user?.let { preferences[USER_KEY] = Json.encodeToString(it) }
        }
    }
    
    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
