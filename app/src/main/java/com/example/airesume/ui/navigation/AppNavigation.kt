package com.example.airesume.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.airesume.ui.screens.auth.LoginScreen
import com.example.airesume.ui.screens.auth.SignUpScreen
import com.example.airesume.ui.screens.form.FormScreen
import com.example.airesume.ui.screens.home.HomeScreen
import com.example.airesume.ui.screens.home.EditProfileScreen
import com.example.airesume.ui.screens.preview.PreviewScreen
import com.example.airesume.ui.screens.search.SearchScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val appCoroutineScope = rememberCoroutineScope()

    // ✅ Check if user already logged in
    val firebaseAuth = FirebaseAuth.getInstance()
    val startDestination = if (firebaseAuth.currentUser != null) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ⬇️ Login Screen
        composable("login") {
            LoginScreen(navController = navController)
        }

        // ⬇️ Sign Up Screen
        composable("signup") {
            SignUpScreen(navController = navController)
        }

        // ⬇️ Home Screen
        composable("home") {
            HomeScreen(navController = navController)
        }

        // Form Screen
        composable(
            route = "form/{resumeId}",
            arguments = listOf(navArgument("resumeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val resumeId = backStackEntry.arguments?.getLong("resumeId") ?: -1L
            FormScreen(
                navController = navController,
                coroutineScope = appCoroutineScope,
                resumeId = resumeId
            )
        }

        // Preview Screen
        composable(
            route = "preview/{resumeId}",
            arguments = listOf(navArgument("resumeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val resumeId = backStackEntry.arguments?.getLong("resumeId") ?: -1L
            PreviewScreen(
                navController = navController,
                coroutineScope = appCoroutineScope,
                resumeId = resumeId
            )
        }

        // Search Screen
        composable("search") {
            SearchScreen(navController = navController)
        }

        composable("editProfile") {
            EditProfileScreen(navController = navController)
        }
    }
}
