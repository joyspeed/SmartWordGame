package com.smartwordgame.app.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

class SoundManager(var soundEnabled: Boolean = true) {
    private val handler = Handler(Looper.getMainLooper())
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)

    fun playCorrect() {
        val variant = CORRECT_TONE_VARIANTS.random(Random.Default)
        playSequence(tones = variant)
    }

    fun playIncorrect() {
        playSequence(
            tones = listOf(
                ToneGenerator.TONE_DTMF_7,
                ToneGenerator.TONE_DTMF_3
            ),
            durationMs = GENTLE_TONE_DURATION_MS,
            delayMs = GENTLE_TONE_DELAY_MS
        )
    }

    fun playTick() {
        if (!soundEnabled) return

        handler.removeCallbacksAndMessages(null)
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, TICK_DURATION_MS)
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        toneGenerator.release()
    }

    private fun playSequence(
        tones: List<Int>,
        durationMs: Int = DEFAULT_TONE_DURATION_MS,
        delayMs: Long = DEFAULT_TONE_DELAY_MS
    ) {
        if (!soundEnabled) return

        handler.removeCallbacksAndMessages(null)
        tones.forEachIndexed { index, tone ->
            handler.postDelayed(
                { toneGenerator.startTone(tone, durationMs) },
                index * delayMs
            )
        }
    }

    private companion object {
        const val DEFAULT_TONE_DURATION_MS = 120
        const val DEFAULT_TONE_DELAY_MS = 140L
        const val GENTLE_TONE_DURATION_MS = 150
        const val GENTLE_TONE_DELAY_MS = 180L
        const val TICK_DURATION_MS = 80

        val CORRECT_TONE_VARIANTS = listOf(
            listOf(
                ToneGenerator.TONE_DTMF_5,
                ToneGenerator.TONE_DTMF_7,
                ToneGenerator.TONE_DTMF_9
            ),
            listOf(
                ToneGenerator.TONE_DTMF_1,
                ToneGenerator.TONE_DTMF_3,
                ToneGenerator.TONE_DTMF_6
            ),
            listOf(
                ToneGenerator.TONE_DTMF_4,
                ToneGenerator.TONE_DTMF_8,
                ToneGenerator.TONE_DTMF_0
            )
        )
    }
}
