package com.example.airesume.ui.screens.form

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.airesume.ui.components.common.AppButton
import com.example.airesume.ui.components.common.AppTextField
import com.example.airesume.ui.components.form.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    resumeId: Long,
    viewModel: FormViewModel = viewModel(
        factory = FormViewModel.Factory(
            application = LocalContext.current.applicationContext as Application,
            resumeId = resumeId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    val experiences by remember { derivedStateOf { uiState.resume.experiences } }
    val educations by remember { derivedStateOf { uiState.resume.educations } }
    val skills by remember { derivedStateOf { uiState.resume.skills } }
    val projects by remember { derivedStateOf { uiState.resume.projects } }
    val certifications by remember { derivedStateOf { uiState.resume.certifications } }

    val title by remember { derivedStateOf { uiState.resume.title } }
    val selectedTemplateId by remember { derivedStateOf { uiState.resume.templateId } }

    val personalInfo by remember { derivedStateOf { uiState.resume.personalInfo } }

    val isGeneratingSummary by remember { derivedStateOf { uiState.isGeneratingSummary } }
    val improvingExperienceId by remember { derivedStateOf { uiState.improvingExperienceId } }

    Log.d("FormScreen", "Rendering form with resumeId: $resumeId")

    LaunchedEffect(resumeId) {
        Log.d("FormScreen", "Launched effect for resumeId: $resumeId")
        if (resumeId != -1L) {
            viewModel.loadResume(resumeId)
        }
    }

    LaunchedEffect(uiState) {
        Log.d("FormScreen", "UI state changed: isGeneratingSummary=$isGeneratingSummary, improvingExperienceId=$improvingExperienceId, templateId=$selectedTemplateId")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (resumeId == -1L) "Create Resume" else "Edit Resume") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val success = viewModel.saveResume()
                                if (success) {
                                    snackbarHostState.showSnackbar("Resume saved")
                                    navController.popBackStack()
                                } else {
                                    snackbarHostState.showSnackbar("Failed to save resume", withDismissAction = true)
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item(key = "title") {
                AppTextField(
                    value = title,
                    onValueChange = {
                        viewModel.updateTitle(it)
                        Log.d("FormScreen", "Title changed to: $it")
                    },
                    label = "Resume Title",
                    singleLine = true,
                )
            }

            item(key = "templateSelection") {
                val templates = listOf("Classic", "Modern", "Minimalist")
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppTextField(
                        value = selectedTemplateId,
                        onValueChange = { /* Read-only for dropdown */ },
                        label = "Select Template",
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        templates.forEach { template ->
                            DropdownMenuItem(
                                text = { Text(template) },
                                onClick = {
                                    viewModel.updateTemplateId(template)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }


            item(key = "personalInfo") {
                PersonalInfoForm(
                    personalInfo = personalInfo,
                    onPersonalInfoChange = {
                        viewModel.updatePersonalInfo(it)
                        Log.d("FormScreen", "Personal info updated")
                    },
                    onGenerateSummary = {
                        Log.d("FormScreen", "Generate summary button clicked")
                        coroutineScope.launch {
                            viewModel.generateSummary()
                        }
                    },
                    isGeneratingSummary = isGeneratingSummary
                )
            }

            item(key = "experienceHeader") {
                SectionHeader(
                    title = "Work Experience",
                    onAddClick = {
                        viewModel.addExperience()
                        Log.d("FormScreen", "Add experience button clicked")
                    }
                )
            }

            items(
                items = experiences,
                key = { it.id } // Ensure Experience.id is accessible
            ) { experience ->
                ExperienceForm(
                    experience = experience,
                    onExperienceChange = { updated ->
                        viewModel.updateExperience(updated)
                        Log.d("FormScreen", "Experience updated: ${updated.id}")
                    },
                    onDelete = {
                        viewModel.deleteExperience(experience.id)
                        Log.d("FormScreen", "Experience deleted: ${experience.id}")
                    },
                    onImproveDescription = {
                        Log.d("FormScreen", "Improve description button clicked for: ${experience.id}")
                        coroutineScope.launch {
                            viewModel.improveExperienceDescription(experience.id)
                        }
                    },
                    isImproving = improvingExperienceId == experience.id
                )
            }

            item(key = "educationHeader") {
                SectionHeader(
                    title = "Education",
                    onAddClick = {
                        viewModel.addEducation()
                        Log.d("FormScreen", "Add education button clicked")
                    }
                )
            }

            items(
                items = educations,
                key = { it.id } // Ensure Education.id is accessible
            ) { education ->
                EducationForm(
                    education = education,
                    onEducationChange = { updated ->
                        viewModel.updateEducation(updated)
                        Log.d("FormScreen", "Education updated: ${updated.id}")
                    },
                    onDelete = {
                        viewModel.deleteEducation(education.id)
                        Log.d("FormScreen", "Education deleted: ${education.id}")
                    }
                )
            }

            item(key = "projectsHeader") {
                SectionHeader(
                    title = "Projects",
                    onAddClick = {
                        viewModel.addProject()
                        Log.d("FormScreen", "Add project button clicked")
                    }
                )
            }

            items(
                items = projects,
                key = { it.id } // Ensure Project.id is accessible
            ) { project ->
                ProjectForm(
                    project = project,
                    onProjectChange = { updated ->
                        viewModel.updateProject(updated)
                        Log.d("FormScreen", "Project updated: ${updated.id}")
                    },
                    onDelete = {
                        viewModel.deleteProject(project.id)
                        Log.d("FormScreen", "Project deleted: ${project.id}")
                    }
                )
            }

            item(key = "certificationsHeader") {
                SectionHeader(
                    title = "Certifications",
                    onAddClick = {
                        viewModel.addCertification()
                        Log.d("FormScreen", "Add certification button clicked")
                    }
                )
            }

            items(
                items = certifications,
                key = { it.id } // Ensure Certification.id is accessible
            ) { certification ->
                CertificationForm(
                    certification = certification,
                    onCertificationChange = { updated ->
                        viewModel.updateCertification(updated)
                        Log.d("FormScreen", "Certification updated: ${updated.id}")
                    },
                    onDelete = {
                        viewModel.deleteCertification(certification.id)
                        Log.d("FormScreen", "Certification deleted: ${certification.id}")
                    }
                )
            }


            item(key = "skillsHeader") {
                SectionHeader(
                    title = "Skills",
                    onAddClick = {
                        viewModel.addSkill()
                        Log.d("FormScreen", "Add skill button clicked")
                    }
                )
            }

            items(
                items = skills,
                key = { it.id } // Ensure Skill.id is accessible
            ) { skill ->
                SkillsForm(
                    skill = skill,
                    onSkillChange = { updated ->
                        viewModel.updateSkill(updated)
                        Log.d("FormScreen", "Skill updated: ${updated.id}")
                    },
                    onDelete = {
                        viewModel.deleteSkill(skill.id)
                        Log.d("FormScreen", "Skill deleted: ${skill.id}")
                    }
                )
            }

            item(key = "saveButton") {
                AppButton(
                    text = "Save Resume",
                    onClick = {
                        Log.d("FormScreen", "Save resume button clicked")
                        coroutineScope.launch {
                            val success = viewModel.saveResume()
                            if (success) {
                                snackbarHostState.showSnackbar("Resume saved")
                                navController.popBackStack()
                            } else {
                                snackbarHostState.showSnackbar("Failed to save resume", withDismissAction = true)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onAddClick) {
            Icon(Icons.Default.Add, contentDescription = "Add $title")
        }
    }
}