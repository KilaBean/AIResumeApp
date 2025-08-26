package com.example.airesume.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "educations")
data class Education(
    @PrimaryKey val id: String, // Ensure 'id' is here
    val degree: String = "",
    val field: String = "",
    val institution: String = "",
    val startDate: String = "", // MM/YYYY
    val endDate: String = "",   // MM/YYYY or "Present"
    val description: String = ""
)