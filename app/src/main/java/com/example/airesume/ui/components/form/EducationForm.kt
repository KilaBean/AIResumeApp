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
import com.example.airesume.data.model.Education
import com.example.airesume.ui.components.common.AppTextField

@Composable
fun EducationForm(
    education: Education,
    onEducationChange: (Education) -> Unit,
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
                    text = "Education Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Education")
                }
            }

            AppTextField(
                value = education.institution,
                onValueChange = { onEducationChange(education.copy(institution = it)) },
                label = "Institution",
                singleLine = true,
                maxLines = 1
            )

            // Degree (simple text input)
            AppTextField(
                value = education.degree,
                onValueChange = { onEducationChange(education.copy(degree = it)) },
                label = "Degree",
                singleLine = true,
                maxLines = 1
            )

            // Field of Study (simple text input)
            AppTextField(
                value = education.field,
                onValueChange = { onEducationChange(education.copy(field = it)) },
                label = "Field of Study",
                singleLine = true,
                maxLines = 1
            )

            // Start Date Input (Manual only)
            AppTextField(
                value = education.startDate,
                onValueChange = { onEducationChange(education.copy(startDate = it)) },
                label = "Start Date (MM/YYYY)",
                singleLine = true,
                maxLines = 1,
                keyboardType = KeyboardType.Number, // Suggest number keyboard
                modifier = Modifier.fillMaxWidth()
            )

            // End Date Input (Manual only)
            AppTextField(
                value = education.endDate,
                onValueChange = { onEducationChange(education.copy(endDate = it)) },
                label = "End Date (MM/YYYY)",
                singleLine = true,
                maxLines = 1,
                keyboardType = KeyboardType.Number, // Suggest number keyboard
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = education.description,
                onValueChange = { onEducationChange(education.copy(description = it)) },
                label = "Description (Optional)",
                singleLine = false,
                minLines = 3,
                maxLines = Int.MAX_VALUE
            )
        }
    }
}