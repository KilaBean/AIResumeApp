package com.example.airesume.data.remote

import android.content.Context
import android.util.Log
import com.example.airesume.R
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiApiService(private val context: Context) {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-pro",
        apiKey = context.getString(R.string.gemini_api_key)
    )

    suspend fun improveResumeSection(section: String, content: String): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                    **Output ONLY the refined content for the ${section} section.**
                    Refine the following resume ${section} description.
                    Make it significantly more professional, impactful, and concise.
                    Utilize strong action verbs, quantify achievements where possible with metrics, and focus on the results and impact.
                    Crucially, retain all original factual information, only enhancing the language and structure.
                    Do not add or remove any specific factual details; focus purely on rephrasing and improving the presentation.
                    Do not include any introductory phrases, explanations, or conversational text before or after the improved content.

                    Original content:
                    $content
                """.trimIndent()

            Log.d("GeminiApiService", "Sending prompt for $section: $prompt")
            val response = generativeModel.generateContent(prompt)

            if (response.text.isNullOrEmpty()) {
                Log.w("GeminiApiService", "Received empty response for $section")
                return@withContext content
            }

            val responseText = response.text?.trim() // Apply trim here
            Log.d("GeminiApiService", "Received response for $section: $responseText")
            responseText
        } catch (e: Exception) {
            Log.e("GeminiApiService", "Error improving resume section: $section", e)
            content
        } as String
    }

    // Reverted to return String, simple prompt, no parsing
    suspend fun generateSummary(personalInfo: String, experiences: String, skills: String): String = withContext(Dispatchers.IO) {
        Log.d("GeminiApiService", "generateSummary called with personalInfo: $personalInfo")
        Log.d("GeminiApiService", "Experiences length: ${experiences.length}")
        Log.d("GeminiApiService", "Skills: $skills")

        try {
            val prompt = """
            **Output ONLY the 3-5 sentence professional summary.**
            Craft a highly compelling and professional resume summary (3-5 sentences) based on the provided information.
            The summary must strategically highlight key strengths, significant accomplishments, and relevant skills.
            Incorporate strong action verbs and quantify achievements with measurable results wherever possible.
            Ensure the summary is tailored to immediately convey value to a potential employer and capture attention.
            Do not include any introductory phrases, explanations, or conversational text before or after the summary.

            Personal Information:
            $personalInfo

            Work Experience:
            $experiences

            Skills:
            $skills
        """.trimIndent()

            Log.d("GeminiApiService", "Sending prompt to Gemini API")
            val response = generativeModel.generateContent(prompt)

            if (response.text.isNullOrEmpty()) {
                Log.w("GeminiApiService", "Received empty response for summary")
                return@withContext ""
            }

            val responseText = response.text?.trim() // Apply trim here
            Log.d("GeminiApiService", "Received response: $responseText")
            responseText
        } catch (e: Exception) {
            Log.e("GeminiApiService", "Error generating summary", e)
            ""
        } as String
    }
}