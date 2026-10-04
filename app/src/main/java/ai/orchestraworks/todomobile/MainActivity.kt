package ai.orchestraworks.todomobile

import ai.orchestraworks.todomobile.ui.TodoNavHost
import ai.orchestraworks.todomobile.ui.theme.TodoTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as TodoApplication).container
        setContent {
            TodoTheme {
                TodoNavHost(container)
            }
        }
    }
}
