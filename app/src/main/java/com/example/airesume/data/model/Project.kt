package com.example.airesume.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val id: String, // Using String for UUID
    val name: String = "",
    val role: String = "",
    val technologies: String = "",
    val startDate: String = "", // MM/YYYY
    val endDate: String = "",   // MM/YYYY or "Present"
    val description: String = ""
)