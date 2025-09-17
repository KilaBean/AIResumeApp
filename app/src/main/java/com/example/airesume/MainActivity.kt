package com.example.airesume

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Required for edge-to-edge display
import androidx.compose.foundation.layout.fillMaxSize // Required for Modifier.fillMaxSize
import androidx.compose.material3.MaterialTheme // Required for MaterialTheme.colorScheme.background
import androidx.compose.material3.Surface // Required for the Surface composable
import androidx.compose.ui.Modifier // Required for Modifier
import com.example.airesume.ui.navigation.AppNavigation
import com.example.airesume.ui.theme.AIResumeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables full-screen experience
        setContent {
            AIResumeTheme {
                // A top-level Surface ensures the entire app content area
                // is painted with the theme's background color, handling edge-to-edge.
                Surface(
                    modifier = Modifier.fillMaxSize(), // Makes the surface fill the entire screen
                    color = MaterialTheme.colorScheme.background // Sets the background color to White from your theme
                ) {
                    AppNavigation() // Your main navigation graph
                }
            }
        }
    }
}