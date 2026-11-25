package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Lightweight procedural arcade sound effect synthesizer backed by Android's ToneGenerator.
 * Plays crisp non-blocking audio feedback cues for in-app games when sound effects are enabled.
 */
object SoundFxManager {
    private const val TAG = "SoundFxManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    var isEnabled: Boolean = true

    @Volatile
    private var toneGenerator: ToneGenerator? = null

    private fun getOrCreateGenerator(): ToneGenerator? {
        if (!isEnabled) return null
        return try {
            toneGenerator ?: synchronized(this) {
                toneGenerator ?: ToneGenerator(AudioManager.STREAM_MUSIC, 45).also {
                    toneGenerator = it
                }
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Audio ToneGenerator unavailable", t)
            null
        }
    }

    fun playShardCollect() = playTone(ToneGenerator.TONE_PROP_ACK, 70)
    fun playBrickBounce() = playTone(ToneGenerator.TONE_PROP_BEEP, 45)
    fun playBrickBreak() = playTone(ToneGenerator.TONE_PROP_BEEP2, 65)
    fun playTileMerge() = playTone(ToneGenerator.TONE_SUP_PIP, 55)
    fun playPowerUp() = playTone(ToneGenerator.TONE_PROP_PROMPT, 110)
    fun playGameOver() = playTone(ToneGenerator.TONE_SUP_ERROR, 180)
    fun playUiClick() = playTone(ToneGenerator.TONE_PROP_BEEP, 30)

    private fun playTone(toneType: Int, durationMs: Int) {
        if (!isEnabled) return
        scope.launch {
            try {
                getOrCreateGenerator()?.startTone(toneType, durationMs)
            } catch (t: Throwable) {
                Log.w(TAG, "Failed playing arcade tone", t)
            }
        }
    }
}
