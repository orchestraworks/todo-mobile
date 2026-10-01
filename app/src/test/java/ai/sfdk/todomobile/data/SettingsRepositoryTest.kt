package ai.sfdk.todomobile.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @After
    fun tearDown() {
        scope.cancel()
    }

    private fun repository() = SettingsRepository(
        dataStore = PreferenceDataStoreFactory.create(scope = scope) {
            folder.root.resolve("settings.preferences_pb")
        },
        scope = scope,
    )

    @Test
    fun `uses the emulator host address by default`() = runTest {
        val settings = repository()

        assertEquals("http://10.0.2.2:3000/api", settings.currentBaseUrl())
        assertEquals("http://10.0.2.2:3000/api", settings.baseUrl.value)
    }

    @Test
    fun `a new address takes effect right away`() = runTest {
        val settings = repository()

        settings.setBaseUrl("http://192.168.1.20:3000/api")

        assertEquals("http://192.168.1.20:3000/api", settings.currentBaseUrl())
        assertEquals("http://192.168.1.20:3000/api", settings.baseUrl.value)
    }
}
