package ai.sfdk.todomobile.ui

import ai.sfdk.todomobile.AppContainer
import ai.sfdk.todomobile.ui.detail.TodoDetailScreen
import ai.sfdk.todomobile.ui.detail.TodoDetailViewModel
import ai.sfdk.todomobile.ui.list.TodoListScreen
import ai.sfdk.todomobile.ui.list.TodoListViewModel
import ai.sfdk.todomobile.ui.settings.SettingsScreen
import ai.sfdk.todomobile.ui.settings.SettingsViewModel
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
