package ai.orchestraworks.todomobile

import ai.orchestraworks.todomobile.data.SettingsRepository
import ai.orchestraworks.todomobile.data.TodoApi
import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient

class TodoApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

class AppContainer(context: Context) {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val httpClient = OkHttpClient()
    private var cachedApi: Pair<String, TodoApi>? = null

    val settings = SettingsRepository(context.applicationContext.settingsDataStore, applicationScope)

    suspend fun api(): TodoApi {
        val baseUrl = settings.currentBaseUrl()
        synchronized(this) {
            cachedApi?.let { (url, api) -> if (url == baseUrl) return api }
            return TodoApi.create(baseUrl, httpClient).also { cachedApi = baseUrl to it }
        }
    }
}
