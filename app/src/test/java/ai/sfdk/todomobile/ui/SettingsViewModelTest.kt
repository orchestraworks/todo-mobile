package ai.sfdk.todomobile.ui

import ai.sfdk.todomobile.MainDispatcherRule
import ai.sfdk.todomobile.data.SettingsRepository
import ai.sfdk.todomobile.ui.settings.SettingsViewModel
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val settings by lazy {
        SettingsRepository(
            dataStore = PreferenceDataStoreFactory.create(scope = scope) {
                folder.root.resolve("settings.preferences_pb")
            },
            scope = scope,
        )
    }

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun `starts from the current address`() {
        val viewModel = SettingsViewModel(settings)

        assertEquals("http://10.0.2.2:3000/api", viewModel.state.value.currentBaseUrl)
        assertEquals("http://10.0.2.2:3000/api", viewModel.state.value.baseUrlInput)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `rejects an address without http or https`() {
        val viewModel = SettingsViewModel(settings)

        viewModel.onBaseUrlChange("192.168.1.20:3000/api")
        viewModel.save()

        assertEquals("Enter an address that starts with http:// or https://", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSaved)
        assertEquals("http://10.0.2.2:3000/api", viewModel.state.value.currentBaseUrl)
        assertEquals("http://10.0.2.2:3000/api", settings.baseUrl.value)
    }

    @Test
    fun `rejects text that is not an address`() {
        val viewModel = SettingsViewModel(settings)

        viewModel.onBaseUrlChange("my laptop")
        viewModel.save()

        assertEquals("Enter an address that starts with http:// or https://", viewModel.state.value.error)
    }

    @Test
    fun `saving trims spaces and a trailing slash`() {
        val viewModel = SettingsViewModel(settings)

        viewModel.onBaseUrlChange("  http://192.168.1.20:3000/api/ ")
        viewModel.save()

        assertTrue(viewModel.state.value.isSaved)
        assertEquals("http://192.168.1.20:3000/api", viewModel.state.value.baseUrlInput)
        assertEquals("http://192.168.1.20:3000/api", viewModel.state.value.currentBaseUrl)
        assertEquals("http://192.168.1.20:3000/api", settings.baseUrl.value)
    }

    @Test
    fun `use default puts the emulator address back`() {
        val viewModel = SettingsViewModel(settings)
        viewModel.onBaseUrlChange("http://192.168.1.20:3000/api")

        viewModel.resetToDefault()

        assertEquals("http://10.0.2.2:3000/api", viewModel.state.value.baseUrlInput)
    }
}
