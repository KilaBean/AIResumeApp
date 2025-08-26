package com.example.airesume.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.airesume.ui.screens.form.FormScreen
import com.example.airesume.ui.screens.home.HomeScreen
import com.example.airesume.ui.screens.preview.PreviewScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Keep this coroutineScope for FormScreen and PreviewScreen, as they need it for snackbars
    // or AI operations which might be launched from a parent scope.
    val appCoroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            // HomeScreen does NOT take a coroutineScope parameter.
            // It uses its own internal rememberCoroutineScope if it needs to launch coroutines.
            HomeScreen(
                navController = navController
            )
        }

        composable(
            route = "form/{resumeId}",
            arguments = listOf(navArgument("resumeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val resumeId = backStackEntry.arguments?.getLong("resumeId") ?: -1L
            FormScreen(
                navController = navController,
                coroutineScope = appCoroutineScope, // Correctly passing to FormScreen
                resumeId = resumeId
            )
        }

        composable(
            route = "preview/{resumeId}",
            arguments = listOf(navArgument("resumeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val resumeId = backStackEntry.arguments?.getLong("resumeId") ?: -1L
            PreviewScreen(
                navController = navController,
                coroutineScope = appCoroutineScope, // Correctly passing to PreviewScreen
                resumeId = resumeId
            )
        }
    }
}