package com.example.airesume.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Skill(
    val id: String = "",
    val name: String = "",
    val level: String = "Beginner" // Beginner, Intermediate, Advanced
)