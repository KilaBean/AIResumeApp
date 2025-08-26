package com.example.airesume.ui.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.airesume.data.model.PersonalInfo
import com.example.airesume.ui.components.common.AppButton
import com.example.airesume.ui.components.common.AppTextField

@Composable
fun PersonalInfoForm(
    personalInfo: PersonalInfo,
    onPersonalInfoChange: (PersonalInfo) -> Unit,
    onGenerateSummary: () -> Unit,
    isGeneratingSummary: Boolean,
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
            Text(
                text = "Personal Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            AppTextField(
                value = personalInfo.fullName,
                onValueChange = { onPersonalInfoChange(personalInfo.copy(fullName = it)) },
                label = "Full Name",
                singleLine = true,
                maxLines = 1
            )
            AppTextField(
                value = personalInfo.email,
                onValueChange = { onPersonalInfoChange(personalInfo.copy(email = it)) },
                label = "Email",
                singleLine = true,
                maxLines = 1
            )
            AppTextField(
                value = personalInfo.phone,
                onValueChange = { onPersonalInfoChange(personalInfo.copy(phone = it)) },
                label = "Phone",
                singleLine = true,
                maxLines = 1
            )
            AppTextField(
                value = personalInfo.linkedin,
                onValueChange = { onPersonalInfoChange(personalInfo.copy(linkedin = it)) },
                label = "LinkedIn Profile",
                singleLine = true,
                maxLines = 1
            )
            AppTextField(
                value = personalInfo.github,
                onValueChange = { onPersonalInfoChange(personalInfo.copy(github = it)) },
                label = "GitHub Profile",
                singleLine = true,
                maxLines = 1
            )
            AppTextField(
                value = personalInfo.address,
                onValueChange = { onPersonalInfoChange(personalInfo.copy(address = it)) },
                label = "Address",
                singleLine = false,
                minLines = 1,
                maxLines = 3
            )

            // Professional Summary TextField
            AppTextField(
                value = personalInfo.summary,
                onValueChange = { onPersonalInfoChange(personalInfo.copy(summary = it)) },
                label = "Professional Summary",
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
                    text = if (isGeneratingSummary) "Generating..." else "Generate Summary with AI",
                    onClick = onGenerateSummary,
                    modifier = Modifier.weight(1f),
                    isLoading = isGeneratingSummary
                )
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Generated",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (isGeneratingSummary) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}