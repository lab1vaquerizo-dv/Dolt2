package com.example.dolt2

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                // Solicitar permisos
                RequestPermissions()
                
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
fun RequestPermissions() {
    val context = LocalContext.current
    var showExactAlarmDialog by remember { mutableStateOf(false) }

    // Permiso de Notificaciones (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        showExactAlarmDialog = true
                    }
                }
            }
        )

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    showExactAlarmDialog = true
                }
            }
        }
    }

    if (showExactAlarmDialog) {
        AlertDialog(
            onDismissRequest = { showExactAlarmDialog = false },
            title = { Text("Permiso de alarmas exactas") },
            text = { Text("Para que los recordatorios funcionen puntualmente, Dolt necesita permiso para programar alarmas exactas.") },
            confirmButton = {
                TextButton(onClick = {
                    showExactAlarmDialog = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                }) { Text("Ir a Ajustes") }
            },
            dismissButton = {
                TextButton(onClick = { showExactAlarmDialog = false }) { Text("Ahora no") }
            }
        )
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