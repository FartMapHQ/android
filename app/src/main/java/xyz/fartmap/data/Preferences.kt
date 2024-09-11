package xyz.fartmap.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Preferences(private var context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val api = HTTP()
    }

    val token: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }

    suspend fun login(username: String, password: String) {
        // wait 2s
        withContext(Dispatchers.IO) {
            Thread.sleep(2000)
            context.dataStore.edit { settings ->
                settings[TOKEN_KEY] = username + password
            }
        }
    }

    suspend fun signup(username: String, password: String) {
        withContext(Dispatchers.IO) {
            val output = api.signup(username, password)
            if (!output) {
                throw Exception("Signup failed")
            }
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { settings ->
                settings.remove(TOKEN_KEY)
            }
        }
    }
}