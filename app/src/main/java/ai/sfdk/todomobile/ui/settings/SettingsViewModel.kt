package ai.sfdk.todomobile.ui.settings

import ai.sfdk.todomobile.data.SettingsRepository
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.net.URI

data class SettingsState(
    val currentBaseUrl: String,
    val baseUrlInput: String = currentBaseUrl,
    val error: String? = null,
    val isSaved: Boolean = false,
)

class SettingsViewModel(
    private val settings: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(currentBaseUrl = settings.baseUrl.value))
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun onBaseUrlChange(text: String) {
        _state.update { it.copy(baseUrlInput = text, error = null, isSaved = false) }
    }

    fun resetToDefault() {
        onBaseUrlChange(SettingsRepository.DEFAULT_BASE_URL)
    }

    fun save() {
        val candidate = _state.value.baseUrlInput.trim().trimEnd('/')
        if (!isValidBaseUrl(candidate)) {
            _state.update { it.copy(error = "Enter an address that starts with http:// or https://") }
            return
        }
        settings.setBaseUrl(candidate)
        _state.update { it.copy(currentBaseUrl = candidate, baseUrlInput = candidate, error = null, isSaved = true) }
    }

    private fun isValidBaseUrl(text: String): Boolean {
        val uri = runCatching { URI(text) }.getOrNull() ?: return false
        return uri.scheme in setOf("http", "https") && !uri.host.isNullOrBlank()
    }
}
