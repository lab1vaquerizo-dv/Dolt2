package com.example.dolt2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dolt2.ui.categories.CategoryScreen
import com.example.dolt2.ui.tasks.StatsScreen
import com.example.dolt2.ui.tasks.TaskDetailScreen
import com.example.dolt2.ui.tasks.TaskListScreen
import com.example.dolt2.ui.theme.DoltTheme
import dagger.hilt.android.AndroidEntryPoint

object NavRoutes {
    const val TASK_LIST = "tasks"
    const val TASK_NEW = "tasks/new"
    const val TASK_DETAIL = "tasks/{taskId}"
    const val CATEGORIES = "categories"

    const val STATS = "stats"


    fun taskDetail(id: Long) = "tasks/$id"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoltTheme {
                // Solicitar permiso de notificaciones en Android 13+
                RequestNotificationPermission()
                
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
fun RequestNotificationPermission() {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                // Manejar resultado si es necesario
            }
        )

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
                onNavigateToCategories = { navController.navigate(NavRoutes.CATEGORIES) },
                onNavigateToStats = { navController.navigate(NavRoutes.STATS) }
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
        composable(NavRoutes.STATS) {
            StatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}