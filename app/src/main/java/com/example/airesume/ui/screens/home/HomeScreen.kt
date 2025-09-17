package com.example.airesume.ui.screens.home

import android.app.Application
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.ui.res.painterResource
import com.example.airesume.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.airesume.data.model.Resume
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(LocalContext.current.applicationContext as Application)
    )
) {
    val resumes by viewModel.resumes.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val localCoroutineScope = rememberCoroutineScope()

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    // Firebase
    val firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    var showProfileSheet by remember { mutableStateOf(false) }

    // Bottom bar items: Home, Resumes, Search
    val items = listOf("Home", "Resumes", "Search")
    val icons = listOf(Icons.Default.Home, Icons.AutoMirrored.Filled.ListAlt, Icons.Default.Search)
    val routes = listOf("home", "home", "search")

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(4.dp),
                title = {
                    Text(
                        text = "AI Resume Builder",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
                actions = {
                    // Profile icon now opens sheet
                    IconButton(onClick = { showProfileSheet = true }) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "User Profile",
                            modifier = Modifier
                                .size(36.dp) // adjust size to your liking
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (resumes.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                        if (auth.currentUser == null) {
                            navController.navigate("signup")
                        } else {
                            navController.navigate("form/-1")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Resume")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item, color = Color.Black) },
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            when (routes[index]) {
                                "home" -> navController.navigate("home") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }

                                "search" -> navController.navigate("search")
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.Black
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (resumes.isEmpty()) {
                // Center but allow scrolling
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()), // 👈 scroll in landscape
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .wrapContentHeight(),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No resumes yet?",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Start your new resume",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Let AI guide you to your dream job",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            ExtendedFloatingActionButton(
                                onClick = {
                                    if (firebaseAuth.currentUser == null) {
                                        navController.navigate("signup")
                                    } else {
                                        navController.navigate("form/-1")
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                },
                                text = { Text("Create Your First Resume") },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                elevation = FloatingActionButtonDefaults.elevation(
                                    defaultElevation = 6.dp,
                                    pressedElevation = 8.dp
                                )
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(resumes, key = { it.id }) { resume ->
                        ResumeCard(
                            resume = resume,
                            onEdit = { navController.navigate("form/${resume.id}") },
                            onDelete = {
                                localCoroutineScope.launch {
                                    viewModel.deleteResume(resume)
                                    snackbarHostState.showSnackbar("Resume deleted")
                                }
                            },
                            onPreview = { navController.navigate("preview/${resume.id}") }
                        )
                    }
                }
            }
        }
    }

    // 🔹 ModalBottomSheet for Profile
    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProfileSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "User Profile",
                    modifier = Modifier.size(64.dp) // can make it larger here if you want
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = currentUser?.displayName ?: "Guest User",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentUser?.email ?: "No email available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Edit Profile button
                OutlinedButton(
                    onClick = {
                        showProfileSheet = false
                        // Navigate to a hypothetical edit profile screen
                        navController.navigate("editProfile")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Profile")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Logout button
                Button(
                    onClick = {
                        firebaseAuth.signOut()
                        showProfileSheet = false
                        navController.navigate("signup") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Out")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeCard(
    resume: Resume,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPreview: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = resume.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resume.personalInfo.fullName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalScrollButtons(
                onEdit = onEdit,
                onDelete = onDelete,
                onPreview = onPreview
            )
        }
    }
}

@Composable
fun HorizontalScrollButtons(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPreview: () -> Unit
) {
    HorizontalScroll {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit")
            }
            OutlinedButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete")
            }
            Button(onClick = onPreview) {
                Text("Preview")
            }
        }
    }
}

@Composable
fun HorizontalScroll(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            content()
        }
    }
}
