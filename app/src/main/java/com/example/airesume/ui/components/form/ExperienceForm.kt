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
import com.example.airesume.data.model.Experience
import com.example.airesume.ui.components.common.AppButton
import com.example.airesume.ui.components.common.AppTextField

@Composable
fun ExperienceForm(
    experience: Experience,
    onExperienceChange: (Experience) -> Unit,
    onDelete: () -> Unit,
    onImproveDescription: () -> Unit,
    isImproving: Boolean,
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
                    text = "Experience Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Experience")
                }
            }

            AppTextField(
                value = experience.jobTitle,
                onValueChange = { onExperienceChange(experience.copy(jobTitle = it)) },
                label = "Job Title",
                singleLine = true,
                maxLines = 1
            )
            AppTextField(
                value = experience.company,
                onValueChange = { onExperienceChange(experience.copy(company = it)) },
                label = "Company",
                singleLine = true,
                maxLines = 1
            )

            // Start Date Input (Manual only)
            AppTextField(
                value = experience.startDate,
                onValueChange = { onExperienceChange(experience.copy(startDate = it)) },
                label = "Start Date (MM/YYYY)",
                singleLine = true,
                maxLines = 1,
                keyboardType = KeyboardType.Number, // Suggest number keyboard
                modifier = Modifier.fillMaxWidth()
            )

            // End Date Input (Manual only)
            AppTextField(
                value = experience.endDate,
                onValueChange = { onExperienceChange(experience.copy(endDate = it)) },
                label = "End Date (MM/YYYY)",
                singleLine = true,
                maxLines = 1,
                keyboardType = KeyboardType.Number, // Suggest number keyboard
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = experience.description,
                onValueChange = { onExperienceChange(experience.copy(description = it)) },
                label = "Job Description / Achievements",
                singleLine = false,
                minLines = 3,
                maxLines = Int.MAX_VALUE,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppButton(
                    text = if (isImproving) "Improving..." else "Improve Description with AI",
                    onClick = onImproveDescription,
                    modifier = Modifier.weight(1f),
                    isLoading = isImproving
                )
            }
            if (isImproving) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}