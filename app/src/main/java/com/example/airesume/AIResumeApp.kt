package com.example.airesume

import android.app.Application
import com.example.airesume.data.local.ResumeDatabase
import com.example.airesume.data.remote.GeminiApiService
import com.example.airesume.data.repository.ResumeRepositoryImpl

class AIResumeApp : Application() {
    val database by lazy { ResumeDatabase.getDatabase(this) }
    val geminiApiService by lazy { GeminiApiService(this) }
    val repository by lazy { ResumeRepositoryImpl(database, geminiApiService) }
}