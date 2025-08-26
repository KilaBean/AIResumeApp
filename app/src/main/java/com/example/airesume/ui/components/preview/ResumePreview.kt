package com.example.airesume.ui.components.preview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.airesume.data.model.Resume
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ResumePreview(
    resume: Resume,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with name and contact info
            Column {
                Text(
                    text = resume.personalInfo.fullName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = resume.personalInfo.email,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = resume.personalInfo.phone,
                    style = MaterialTheme.typography.bodyMedium
                )

                // Only show if address is not empty
                if (resume.personalInfo.address.isNotEmpty()) {
                    Text(
                        text = resume.personalInfo.address,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Add LinkedIn profile if not empty
                if (resume.personalInfo.linkedin.isNotEmpty()) {
                    Text(
                        text = "LinkedIn: ${resume.personalInfo.linkedin}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Optional: slightly different color
                    )
                }

                // Add GitHub profile if not empty
                if (resume.personalInfo.github.isNotEmpty()) {
                    Text(
                        text = "GitHub: ${resume.personalInfo.github}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Optional: slightly different color
                    )
                }
            }

            // Professional Summary
            if (resume.personalInfo.summary.isNotEmpty()) {
                Column {
                    Text(
                        text = "Professional Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = resume.personalInfo.summary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Work Experience
            if (resume.experiences.isNotEmpty()) {
                Column {
                    Text(
                        text = "Work Experience",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    resume.experiences.forEach { experience ->
                        ExperiencePreview(experience = experience)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Education
            if (resume.educations.isNotEmpty()) {
                Column {
                    Text(
                        text = "Education",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    resume.educations.forEach { education ->
                        EducationPreview(education = education)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Skills
            if (resume.skills.isNotEmpty()) {
                Column {
                    Text(
                        text = "Skills",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SkillsPreview(skills = resume.skills)
                }
            }

            // Footer with last modified date
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Last modified: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(resume.lastModified))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(androidx.compose.ui.Alignment.End)
            )
        }
    }
}

@Composable
fun ExperiencePreview(
    experience: com.example.airesume.data.model.Experience
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = experience.jobTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${experience.startDate} - ${experience.endDate}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = experience.company,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = experience.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EducationPreview(
    education: com.example.airesume.data.model.Education
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = education.degree,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${education.startDate} - ${education.endDate}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = education.institution,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = education.field,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SkillsPreview(
    skills: List<com.example.airesume.data.model.Skill>
) {
    Column {
        skills.forEach { skill ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = skill.level,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}