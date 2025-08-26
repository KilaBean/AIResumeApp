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
import com.example.airesume.data.model.Certification
import com.example.airesume.ui.components.common.AppTextField

@Composable
fun CertificationForm(
    certification: Certification,
    onCertificationChange: (Certification) -> Unit,
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
                    text = "Certification Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Certification")
                }
            }

            AppTextField(
                value = certification.name,
                onValueChange = { onCertificationChange(certification.copy(name = it)) },
                label = "Certification Name",
                singleLine = true,
                maxLines = 1
            )
            AppTextField(
                value = certification.issuingOrganization,
                onValueChange = { onCertificationChange(certification.copy(issuingOrganization = it)) },
                label = "Issuing Organization",
                singleLine = true,
                maxLines = 1
            )

            // Issue Date Input (Manual only)
            AppTextField(
                value = certification.issueDate,
                onValueChange = { onCertificationChange(certification.copy(issueDate = it)) },
                label = "Issue Date (MM/YYYY)",
                singleLine = true,
                maxLines = 1,
                keyboardType = KeyboardType.Number, // Suggest number keyboard
                modifier = Modifier.fillMaxWidth()
            )

            // Expiration Date Input (Manual only)
            AppTextField(
                value = certification.expirationDate,
                onValueChange = { onCertificationChange(certification.copy(expirationDate = it)) },
                label = "Expiration Date (MM/YYYY)",
                singleLine = true,
                maxLines = 1,
                keyboardType = KeyboardType.Number, // Suggest number keyboard
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = certification.credentialId,
                onValueChange = { onCertificationChange(certification.copy(credentialId = it)) },
                label = "Credential ID (Optional)",
                singleLine = true,
                maxLines = 1
            )
            AppTextField(
                value = certification.credentialUrl,
                onValueChange = { onCertificationChange(certification.copy(credentialUrl = it)) },
                label = "Credential URL (Optional)",
                singleLine = true,
                maxLines = 1
            )
        }
    }
}