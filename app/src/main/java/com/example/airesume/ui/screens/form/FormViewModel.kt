package com.example.airesume.ui.screens.form

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airesume.AIResumeApp
import com.example.airesume.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class FormViewModel(
    application: Application,
    private val resumeId: Long
) : AndroidViewModel(application) {

    private val repository by lazy {
        try {
            (application as? AIResumeApp)?.repository
                ?: run {
                    val db = com.example.airesume.data.local.ResumeDatabase.getDatabase(application)
                    val apiService = com.example.airesume.data.remote.GeminiApiService(application)
                    com.example.airesume.data.repository.ResumeRepositoryImpl(db, apiService)
                }
        } catch (e: Exception) {
            Log.e("FormViewModel", "Error initializing repository", e)
            val db = com.example.airesume.data.local.ResumeDatabase.getDatabase(application)
            val apiService = com.example.airesume.data.remote.GeminiApiService(application)
            com.example.airesume.data.repository.ResumeRepositoryImpl(db, apiService)
        }
    }

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    init {
        if (resumeId == -1L) {
            _uiState.value = FormUiState(
                resume = Resume(
                    title = "My Resume",
                    personalInfo = PersonalInfo(),
                    experiences = listOf(
                        Experience(
                            id = UUID.randomUUID().toString(),
                            jobTitle = "",
                            company = "",
                            startDate = "",
                            endDate = ""
                        )
                    ),
                    educations = listOf(
                        Education(
                            id = UUID.randomUUID().toString(),
                            degree = "",
                            field = "",
                            institution = "",
                            startDate = "",
                            endDate = ""
                        )
                    ),
                    skills = listOf(Skill(id = UUID.randomUUID().toString())),
                    projects = listOf(
                        Project(
                            id = UUID.randomUUID().toString(),
                            name = "",
                            role = "",
                            technologies = "",
                            startDate = "",
                            endDate = "",
                            description = ""
                        )
                    ),
                    certifications = listOf(
                        Certification(
                            id = UUID.randomUUID().toString(),
                            name = "",
                            issuingOrganization = "",
                            issueDate = "",
                            expirationDate = "",
                            credentialId = "",
                            credentialUrl = ""
                        )
                    ),
                    templateId = "Classic"
                )
            )
        }
    }

    fun loadResume(id: Long) {
        viewModelScope.launch {
            try {
                val resume = repository.getResumeById(id)
                resume?.let {
                    _uiState.value = FormUiState(resume = it)
                    Log.d("FormViewModel", "Loaded resume with ID: $id")
                }
            } catch (e: Exception) {
                Log.e("FormViewModel", "Error loading resume", e)
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(title = title)
        )
        Log.d("FormViewModel", "Updated title to: $title")
    }

    fun updateTemplateId(templateId: String) {
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(templateId = templateId)
        )
        Log.d("FormViewModel", "Updated template to: $templateId")
    }

    fun updatePersonalInfo(personalInfo: PersonalInfo) {
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(personalInfo = personalInfo)
        )
        Log.d("FormViewModel", "Updated personal info")
    }

    fun addExperience() {
        val newExperience = Experience(
            id = UUID.randomUUID().toString(),
            jobTitle = "",
            company = "",
            startDate = "",
            endDate = ""
        )
        val updatedExperiences = _uiState.value.resume.experiences + newExperience
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(experiences = updatedExperiences)
        )
        Log.d("FormViewModel", "Added new experience")
    }

    fun updateExperience(experience: Experience) {
        val updatedExperiences = _uiState.value.resume.experiences.map {
            if (it.id == experience.id) experience else it
        }
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(experiences = updatedExperiences)
        )
        Log.d("FormViewModel", "Updated experience with ID: ${experience.id}")
    }

    fun deleteExperience(id: String) {
        val updatedExperiences = _uiState.value.resume.experiences.filter { it.id != id }
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(experiences = updatedExperiences)
        )
        Log.d("FormViewModel", "Deleted experience with ID: $id")
    }

    fun addEducation() {
        val newEducation = Education(
            id = UUID.randomUUID().toString(),
            degree = "",
            field = "",
            institution = "",
            startDate = "",
            endDate = ""
        )
        val updatedEducations = _uiState.value.resume.educations + newEducation
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(educations = updatedEducations)
        )
        Log.d("FormViewModel", "Added new education")
    }

    fun updateEducation(education: Education) {
        val updatedEducations = _uiState.value.resume.educations.map {
            if (it.id == education.id) education else it
        }
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(educations = updatedEducations)
        )
        Log.d("FormViewModel", "Updated education with ID: ${education.id}")
    }

    fun deleteEducation(id: String) {
        val updatedEducations = _uiState.value.resume.educations.filter { it.id != id }
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(educations = updatedEducations)
        )
        Log.d("FormViewModel", "Deleted education with ID: $id")
    }

    fun addSkill() {
        val newSkill = Skill(id = UUID.randomUUID().toString())
        val updatedSkills = _uiState.value.resume.skills + newSkill
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(skills = updatedSkills)
        )
        Log.d("FormViewModel", "Added new skill")
    }

    fun updateSkill(skill: Skill) {
        val updatedSkills = _uiState.value.resume.skills.map {
            if (it.id == skill.id) skill else it
        }
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(skills = updatedSkills)
        )
        Log.d("FormViewModel", "Updated skill with ID: ${skill.id}")
    }

    fun deleteSkill(id: String) {
        val updatedSkills = _uiState.value.resume.skills.filter { it.id != id }
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(skills = updatedSkills)
        )
        Log.d("FormViewModel", "Deleted skill with ID: $id")
    }

    // --- Project Management Functions ---
    fun addProject() {
        val newProject = Project(id = UUID.randomUUID().toString())
        val updatedProjects = _uiState.value.resume.projects + newProject
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(projects = updatedProjects)
        )
        Log.d("FormViewModel", "Added new project")
    }

    fun updateProject(project: Project) {
        val updatedProjects = _uiState.value.resume.projects.map {
            if (it.id == project.id) project else it
        }
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(projects = updatedProjects)
        )
        Log.d("FormViewModel", "Updated project with ID: ${project.id}")
    }

    fun deleteProject(id: String) {
        val updatedProjects = _uiState.value.resume.projects.filter { it.id != id }
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(projects = updatedProjects)
        )
        Log.d("FormViewModel", "Deleted project with ID: $id")
    }

    // --- Certification Management Functions ---
    fun addCertification() {
        val newCertification = Certification(id = UUID.randomUUID().toString())
        val updatedCertifications = _uiState.value.resume.certifications + newCertification
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(certifications = updatedCertifications)
        )
        Log.d("FormViewModel", "Added new certification")
    }

    fun updateCertification(certification: Certification) {
        val updatedCertifications = _uiState.value.resume.certifications.map {
            if (it.id == certification.id) certification else it
        }
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(certifications = updatedCertifications)
        )
        Log.d("FormViewModel", "Updated certification with ID: ${certification.id}")
    }

    fun deleteCertification(id: String) {
        val updatedCertifications = _uiState.value.resume.certifications.filter { it.id != id }
        _uiState.value = _uiState.value.copy(
            resume = _uiState.value.resume.copy(certifications = updatedCertifications)
        )
        Log.d("FormViewModel", "Deleted certification with ID: $id")
    }


    suspend fun saveResume(): Boolean { // Changed to suspend and return Boolean
        return try {
            val resumeToSave = _uiState.value.resume.copy(lastModified = System.currentTimeMillis())
            if (resumeId == -1L) {
                val newResumeId = repository.insertResume(resumeToSave)
                Log.d("FormViewModel", "Inserted new resume with ID: $newResumeId")
                // Optionally update the UI state with the new ID if the form stays open
                // _uiState.value = _uiState.value.copy(resume = resumeToSave.copy(id = newResumeId))
            } else {
                repository.updateResume(resumeToSave)
                Log.d("FormViewModel", "Updated existing resume with ID: ${resumeToSave.id}")
            }
            true // Indicate success
        } catch (e: Exception) {
            Log.e("FormViewModel", "Error saving resume", e)
            false // Indicate failure
        }
    }

    fun generateSummary() {
        viewModelScope.launch {
            Log.d("FormViewModel", "Starting summary generation")

            val personalInfo = _uiState.value.resume.personalInfo
            val experiences = _uiState.value.resume.experiences
            val skills = _uiState.value.resume.skills

            if (experiences.isEmpty() || skills.isEmpty()) {
                Log.w("FormViewModel", "Not enough data to generate summary. Experiences: ${experiences.size}, Skills: ${skills.size}")
                _uiState.value = _uiState.value.copy(isGeneratingSummary = false)
                return@launch
            }

            _uiState.value = _uiState.value.copy(isGeneratingSummary = true)

            try {
                val experiencesText = experiences.joinToString("\n") {
                    "${it.jobTitle} at ${it.company}: ${it.description}"
                }
                val skillsText = skills.joinToString(", ") { it.name }

                Log.d("FormViewModel", "Calling repository.generateSummary()")
                val summary = repository.generateSummary(
                    personalInfo = "${personalInfo.fullName}, ${personalInfo.email}",
                    experiences = experiencesText,
                    skills = skillsText
                )

                Log.d("FormViewModel", "Generated summary: $summary")

                if (summary.isNotEmpty()) {
                    val updatedPersonalInfo = personalInfo.copy(summary = summary)
                    _uiState.value = _uiState.value.copy(
                        resume = _uiState.value.resume.copy(personalInfo = updatedPersonalInfo),
                        isGeneratingSummary = false
                    )
                    Log.d("FormViewModel", "UI state updated with new summary")
                } else {
                    Log.w("FormViewModel", "Received empty summary from API")
                    _uiState.value = _uiState.value.copy(isGeneratingSummary = false)
                }
            } catch (e: Exception) {
                Log.e("FormViewModel", "Error generating summary", e)
                _uiState.value = _uiState.value.copy(isGeneratingSummary = false)
            }
        }
    }

    fun improveExperienceDescription(experienceId: String) {
        viewModelScope.launch {
            Log.d("FormViewModel", "Starting to improve experience description for ID: $experienceId")

            _uiState.value = _uiState.value.copy(improvingExperienceId = experienceId)

            try {
                val experience = _uiState.value.resume.experiences.find { it.id == experienceId }
                experience?.let {
                    Log.d("FormViewModel", "Original description: ${it.description}")

                    val improvedDescription = repository.improveExperienceDescription(it.description)
                    Log.d("FormViewModel", "Improved description: $improvedDescription")

                    val updatedExperience = it.copy(description = improvedDescription)
                    updateExperience(updatedExperience)
                }
            } catch (e: Exception) {
                Log.e("FormViewModel", "Error improving experience description", e)
            } finally {
                _uiState.value = _uiState.value.copy(improvingExperienceId = null)
            }
        }
    }

    class Factory(
        private val application: Application,
        private val resumeId: Long
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FormViewModel(application, resumeId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class FormUiState(
    val resume: Resume = Resume(
        title = "",
        personalInfo = PersonalInfo(),
        experiences = emptyList(),
        educations = emptyList(),
        skills = emptyList(),
        projects = emptyList(),
        certifications = emptyList(),
        templateId = "Classic"
    ),
    val isGeneratingSummary: Boolean = false,
    val improvingExperienceId: String? = null
)