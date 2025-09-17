package com.example.airesume.ui.screens.search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airesume.AIResumeApp // Import your Application class
import com.example.airesume.data.model.Resume
import com.example.airesume.data.repository.ResumeRepository // Import the interface
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class SearchViewModel(application: Application) : ViewModel() {

    private val resumeRepository: ResumeRepository = (application as AIResumeApp).repository

    // 🔹 Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 🔹 Filters state
    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // 🔹 Chips data (static for now)
    val filters = listOf("All", "Tech", "Business", "Design")
    val suggestions = listOf("John Doe", "Senior Developer", "UI Designer")

    @OptIn(FlowPreview::class)
    val filteredResumes: StateFlow<List<Resume>> =
        combine(
            searchQuery.debounce(300L),
            selectedFilter,
            resumeRepository.getAllResumes()
        ) { query, filter, allResumes ->
            // Filtering logic
            val filtered = if (query.isBlank()) {
                emptyList() // No query yet
            } else {
                allResumes.filter { resume ->
                    // Basic text search
                    val matchesText =
                        resume.title.contains(query, ignoreCase = true) ||
                                resume.personalInfo.fullName.contains(query, ignoreCase = true) ||
                                resume.personalInfo.email.contains(query, ignoreCase = true) ||
                                resume.personalInfo.phone.contains(query, ignoreCase = true)

                    // Basic filter (by title as example)
                    val matchesFilter = when (filter) {
                        "All" -> true
                        else -> resume.title.contains(filter, ignoreCase = true)
                    }
                    matchesText && matchesFilter
                }
            }
            filtered
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun updateFilter(newFilter: String) {
        _selectedFilter.value = newFilter
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                        return SearchViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
