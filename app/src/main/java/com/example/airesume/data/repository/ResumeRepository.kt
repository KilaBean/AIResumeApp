package com.example.airesume.data.repository

import com.example.airesume.data.model.Resume
import kotlinx.coroutines.flow.Flow

interface ResumeRepository {
    fun getAllResumes(): Flow<List<Resume>>
    suspend fun getResumeById(id: Long): Resume?
    suspend fun insertResume(resume: Resume): Long
    suspend fun updateResume(resume: Resume)
    suspend fun deleteResume(resume: Resume)

    // Add AI methods to the interface
    suspend fun improveExperienceDescription(description: String): String
    suspend fun generateSummary(personalInfo: String, experiences: String, skills: String): String
}