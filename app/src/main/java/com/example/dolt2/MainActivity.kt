package com.example.dolt2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dolt2.ui.categories.CategoryScreen
import com.example.dolt2.ui.tasks.TaskDetailScreen
import com.example.dolt2.ui.tasks.TaskListScreen
import com.example.dolt2.ui.theme.DoltTheme
import dagger.hilt.android.AndroidEntryPoint

object NavRoutes {
    const val TASK_LIST = "tasks"
    const val TASK_NEW = "tasks/new"
    const val TASK_DETAIL = "tasks/{taskId}"
    const val CATEGORIES = "categories"
    fun taskDetail(id: Long) = "tasks/$id"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoltTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DoltNavGraph()
                }
            }
        }
    }
}

@Composable
fun DoltNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.TASK_LIST
    ) {
        composable(NavRoutes.TASK_LIST) {
            TaskListScreen(
                onNavigateToNewTask = { navController.navigate(NavRoutes.TASK_NEW) },
                onNavigateToTaskDetail = { id -> navController.navigate(NavRoutes.taskDetail(id)) },
                onNavigateToCategories = { navController.navigate(NavRoutes.CATEGORIES) }
            )
        }
        composable(NavRoutes.TASK_NEW) {
            TaskDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = NavRoutes.TASK_DETAIL,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        )  {
            TaskDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.CATEGORIES) {
            CategoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}