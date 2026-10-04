package ai.orchestraworks.todomobile.ui

import ai.orchestraworks.todomobile.AppContainer
import ai.orchestraworks.todomobile.ui.detail.TodoDetailScreen
import ai.orchestraworks.todomobile.ui.detail.TodoDetailViewModel
import ai.orchestraworks.todomobile.ui.list.TodoListScreen
import ai.orchestraworks.todomobile.ui.list.TodoListViewModel
import ai.orchestraworks.todomobile.ui.settings.SettingsScreen
import ai.orchestraworks.todomobile.ui.settings.SettingsViewModel
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun TodoNavHost(container: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "todos") {
        composable("todos") {
            TodoListScreen(
                viewModel = viewModel { TodoListViewModel(container::api) },
                onOpenTodo = { id -> navController.navigate("todos/${Uri.encode(id)}") },
                onOpenSettings = { navController.navigate("settings") },
            )
        }
        composable(
            route = "todos/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { entry ->
            val id = requireNotNull(entry.arguments?.getString("id"))
            TodoDetailScreen(
                viewModel = viewModel { TodoDetailViewModel(id, container::api) },
                onBack = { navController.popBackStack() },
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel { SettingsViewModel(container.settings) },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
