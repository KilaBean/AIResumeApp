package com.example.airesume.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "certifications")
data class Certification(
    @PrimaryKey val id: String, // Using String for UUID
    val name: String = "",
    val issuingOrganization: String = "",
    val issueDate: String = "",      // MM/YYYY
    val expirationDate: String = "", // MM/YYYY or "No Expiration"
    val credentialId: String = "",
    val credentialUrl: String = ""
)