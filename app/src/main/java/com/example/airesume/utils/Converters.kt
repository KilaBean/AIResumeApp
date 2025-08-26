package com.example.airesume.utils

import androidx.room.TypeConverter
import com.example.airesume.data.model.Certification
import com.example.airesume.data.model.Education
import com.example.airesume.data.model.Experience
import com.example.airesume.data.model.PersonalInfo
import com.example.airesume.data.model.Project
import com.example.airesume.data.model.Skill
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromExperienceList(value: List<Experience>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toExperienceList(value: String): List<Experience> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromEducationList(value: List<Education>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toEducationList(value: String): List<Education> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromSkillList(value: List<Skill>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toSkillList(value: String): List<Skill> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromProjectList(value: List<Project>): String { // <--- ADDED THIS
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toProjectList(value: String): List<Project> { // <--- ADDED THIS
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromCertificationList(value: List<Certification>): String { // <--- ADDED THIS
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toCertificationList(value: String): List<Certification> { // <--- ADDED THIS
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromPersonalInfo(value: PersonalInfo): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toPersonalInfo(value: String): PersonalInfo {
        return Json.decodeFromString(value)
    }
}