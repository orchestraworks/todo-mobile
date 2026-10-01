package ai.sfdk.todomobile.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
) {
    private val baseUrlState = MutableStateFlow(DEFAULT_BASE_URL)

    private val restored: Deferred<Unit> = scope.async {
        val stored = runCatching { dataStore.data.first()[BASE_URL_KEY] }.getOrNull()
        if (!stored.isNullOrBlank()) {
            baseUrlState.value = stored
        }
    }

    val baseUrl: StateFlow<String> = baseUrlState.asStateFlow()

    suspend fun currentBaseUrl(): String {
        restored.await()
        return baseUrlState.value
    }

    fun setBaseUrl(url: String) {
        baseUrlState.value = url
        scope.launch {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey("base_url")] = url
            }
        }
    }

    companion object {
        const val DEFAULT_BASE_URL = "http://10.0.2.2:3000/api"

        private val BASE_URL_KEY = stringPreferencesKey("api_base_url")
    }
}
