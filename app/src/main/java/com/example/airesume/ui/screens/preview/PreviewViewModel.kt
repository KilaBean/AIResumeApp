package com.example.airesume.ui.screens.preview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airesume.AIResumeApp
import com.example.airesume.data.model.Resume
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreviewViewModel(
    application: Application,
    private val resumeId: Long
) : AndroidViewModel(application) {

    private val repository = (application as AIResumeApp).repository

    private val _resume = MutableStateFlow<Resume?>(null)
    val resume: StateFlow<Resume?> = _resume.asStateFlow()

    init {
        loadResume()
    }

    fun loadResume() {
        viewModelScope.launch {
            val resume = repository.getResumeById(resumeId)
            _resume.value = resume
        }
    }

    class Factory(
        private val application: Application,
        private val resumeId: Long
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PreviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PreviewViewModel(application, resumeId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}