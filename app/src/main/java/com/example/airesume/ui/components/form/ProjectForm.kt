package com.example.airesume.ui.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.airesume.data.model.Project
import com.example.airesume.ui.components.common.AppTextField

@Composable
fun ProjectForm(
    project: Project,
    onProjectChange: (Project) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Project Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Project")
                }
            }

            AppTextField(
                value = project.name,
                onValueChange = { onProjectChange(project.copy(name = it)) },
                label = "Project Name",
                singleLine = true,
                maxLines = 1
            )
            AppTextField(
                value = project.role,
                onValueChange = { onProjectChange(project.copy(role = it)) },
                label = "Your Role",
                singleLine = true,
                maxLines = 1
            )
            AppTextField(
                value = project.technologies,
                onValueChange = { onProjectChange(project.copy(technologies = it)) },
                label = "Technologies Used (e.g., Kotlin, Firebase)",
                singleLine = false,
                minLines = 1,
                maxLines = 3
            )

            // Start Date Input (Manual only)
            AppTextField(
                value = project.startDate,
                onValueChange = { onProjectChange(project.copy(startDate = it)) },
                label = "Start Date (MM/YYYY)",
                singleLine = true,
                maxLines = 1,
                keyboardType = KeyboardType.Number, // Suggest number keyboard
                modifier = Modifier.fillMaxWidth()
            )

            // End Date Input (Manual only)
            AppTextField(
                value = project.endDate,
                onValueChange = { onProjectChange(project.copy(endDate = it)) },
                label = "End Date (MM/YYYY)",
                singleLine = true,
                maxLines = 1,
                keyboardType = KeyboardType.Number, // Suggest number keyboard
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = project.description,
                onValueChange = { onProjectChange(project.copy(description = it)) },
                label = "Project Description",
                singleLine = false,
                minLines = 3,
                maxLines = Int.MAX_VALUE
            )
        }
    }
}