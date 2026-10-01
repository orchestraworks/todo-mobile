package ai.sfdk.todomobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TodoColors = lightColorScheme(
    primary = Color(0xFF2F6FED),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE6FF),
    onPrimaryContainer = Color(0xFF001A44),
    secondary = Color(0xFF545F71),
    background = Color(0xFFFAFAFC),
    surface = Color(0xFFFAFAFC),
)

@Composable
fun TodoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = TodoColors, content = content)
}
