package com.example.airesume.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airesume.AIResumeApp
import com.example.airesume.data.model.Resume
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn // Import stateIn
import kotlinx.coroutines.launch
import android.util.Log

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // Use applicationContext to get the AIResumeApp instance
    private val repository = (application.applicationContext as AIResumeApp).repository

    // The resumes StateFlow will automatically collect updates from the repository's Flow
    val resumes: StateFlow<List<Resume>> = repository.getAllResumes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Keep collecting for 5 seconds after no active collectors
        initialValue = emptyList() // Provide an initial value
    )

    // Removed loadResumes() function as stateIn handles continuous collection

    fun deleteResume(resume: Resume) {
        viewModelScope.launch {
            try {
                repository.deleteResume(resume)
                Log.d("HomeViewModel", "Resume deleted: ${resume.id}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting resume: ${resume.id}", e)
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}