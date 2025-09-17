package com.example.airesume.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser

    // Editable fields
    var displayName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    val email = currentUser?.email ?: ""

    // UI state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Profile", color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
                actions = {
                    IconButton(
                        onClick = {
                            // Save profile changes
                            currentUser?.updateProfile(
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build()
                            )?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Profile updated")
                                    }
                                    navController.popBackStack()
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Failed to update profile")
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.Black)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Update your profile information",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Email is shown but not editable here
            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Save profile changes
                    currentUser?.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()
                    )?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Profile updated")
                            }
                            navController.popBackStack()
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Failed to update profile")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Changes", color = MaterialTheme.colorScheme.onPrimary)
            }

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}
