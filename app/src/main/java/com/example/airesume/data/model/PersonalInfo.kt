package com.example.airesume.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonalInfo(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val linkedin: String = "",
    val github: String = "",
    val summary: String = "", // <--- THIS MUST BE STRING
    val address: String = ""
)