package com.example.data.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * Service integrating Gemini API through Firebase AI Logic.
 * Uses gemini-3.8-flash by default and attested App Check tokens.
 */
object FirebaseAiService {
    private const val TAG = "FirebaseAiService"

    private val model by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    /**
     * Unary content generation with realistic 60s timeout and cooperative cancellation.
     */
    suspend fun generateContent(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = withTimeout(60_000L) {
                model.generateContent(prompt)
            }
            val text = response.text
            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.failure(IllegalStateException("Model returned an empty response."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini generation timed out", e)
            Result.failure(IllegalStateException("AI generation timed out after 60 seconds. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI Gemini generation failed", e)
            Result.failure(e)
        }
    }

    /**
     * Brainstorm game mechanics, HTML5 canvas ideas, or tech stack suggestions.
     */
    suspend fun generateGameIdea(genre: String): Result<String> {
        val prompt = "You are a creative indie game designer. Suggest a unique browser-based HTML5/Canvas game concept in the '$genre' genre. Include: 1) Title, 2) Core Gameplay Mechanic (touch & keyboard friendly), 3) Minimalist Visual & Audio Style, 4) Recommended Tech Stack (e.g. Canvas 2D, Phaser, Web Audio API)."
        return generateContent(prompt)
    }

    /**
     * Generate artistic photo critiques or atmospheric captions for stills.
     */
    suspend fun generateStillCritique(title: String, location: String, exif: String): Result<String> {
        val prompt = "You are a photography curator. Write an evocative, poetic caption and visual analysis for a photograph titled '$title', captured at '$location' with settings '$exif'."
        return generateContent(prompt)
    }
}
