package com.example.airesume.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "experiences")
data class Experience(
    @PrimaryKey val id: String, // Ensure 'id' is here
    val jobTitle: String = "",
    val company: String = "",
    val startDate: String = "", // MM/YYYY
    val endDate: String = "",   // MM/YYYY or "Present"
    val description: String = ""
)