package com.example.airesume.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "resumes")
data class Resume(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val personalInfo: PersonalInfo,
    val experiences: List<Experience>,
    val educations: List<Education>,
    val skills: List<Skill>,
    val projects: List<Project> = emptyList(),       // <--- ADDED THIS LINE
    val certifications: List<Certification> = emptyList(), // <--- ADDED THIS LINE
    val lastModified: Long = System.currentTimeMillis(),
    val templateId: String = "Classic"
)