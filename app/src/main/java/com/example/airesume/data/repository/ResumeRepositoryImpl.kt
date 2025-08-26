package com.example.airesume.data.repository

import android.util.Log
import com.example.airesume.data.local.ResumeDatabase
import com.example.airesume.data.model.Resume
import com.example.airesume.data.remote.GeminiApiService
import kotlinx.coroutines.flow.Flow

class ResumeRepositoryImpl(
    private val database: ResumeDatabase,
    private val geminiApiService: GeminiApiService
) : ResumeRepository {

    override fun getAllResumes(): Flow<List<Resume>> {
        return database.resumeDao().getAllResumes()
    }

    override suspend fun getResumeById(id: Long): Resume? {
        return database.resumeDao().getResumeById(id)
    }

    override suspend fun insertResume(resume: Resume): Long {
        return database.resumeDao().insertResume(resume)
    }

    override suspend fun updateResume(resume: Resume) {
        database.resumeDao().updateResume(resume)
    }

    override suspend fun deleteResume(resume: Resume) {
        database.resumeDao().deleteResume(resume)
    }

    override suspend fun improveExperienceDescription(description: String): String {
        Log.d("ResumeRepository", "improveExperienceDescription called with: '$description'")
        return try {
            val result = geminiApiService.improveResumeSection("work experience", description)
            Log.d("ResumeRepository", "Improved description result: '$result'")
            result
        } catch (e: Exception) {
            Log.e("ResumeRepository", "Error improving experience description", e)
            description // fallback to original
        }
    }

    override suspend fun generateSummary(
        personalInfo: String,
        experiences: String,
        skills: String
    ): String {
        Log.d("ResumeRepository", "generateSummary called")
        Log.d("ResumeRepository", "PersonalInfo: $personalInfo")
        Log.d("ResumeRepository", "Experiences: $experiences")
        Log.d("ResumeRepository", "Skills: $skills")

        return try {
            val result = geminiApiService.generateSummary(personalInfo, experiences, skills)
            Log.d("ResumeRepository", "Generated summary result: '$result'")
            result
        } catch (e: Exception) {
            Log.e("ResumeRepository", "Error generating summary", e)
            "" // return empty string on error
        }
    }
}