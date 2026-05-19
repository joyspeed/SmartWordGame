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
        playSequence(
            tones = variant.tones,
            durationMs = variant.durationMs,
            delayMs = variant.delayMs
        )
    }

    fun playIncorrect() {
        val variant = INCORRECT_TONE_VARIANTS.random(Random.Default)
        playSequence(
            tones = variant.tones,
            durationMs = variant.durationMs,
            delayMs = variant.delayMs
        )
    }

    fun playTick() {
        if (!soundEnabled) return
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, TICK_DURATION_MS)
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        toneGenerator.release()
    }

    private fun playSequence(
        tones: List<Int>,
        durationMs: Int,
        delayMs: Long
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

    private data class ToneVariant(
        val tones: List<Int>,
        val durationMs: Int,
        val delayMs: Long
    )

    private companion object {
        const val TICK_DURATION_MS = 60

        // Cheerful ascending melodies — longer, more rewarding
        val CORRECT_TONE_VARIANTS = listOf(
            ToneVariant(
                tones = listOf(
                    ToneGenerator.TONE_DTMF_1,
                    ToneGenerator.TONE_DTMF_3,
                    ToneGenerator.TONE_DTMF_5,
                    ToneGenerator.TONE_DTMF_9
                ),
                durationMs = 150,
                delayMs = 160L
            ),
            ToneVariant(
                tones = listOf(
                    ToneGenerator.TONE_DTMF_4,
                    ToneGenerator.TONE_DTMF_7,
                    ToneGenerator.TONE_DTMF_9,
                    ToneGenerator.TONE_DTMF_0
                ),
                durationMs = 140,
                delayMs = 150L
            ),
            ToneVariant(
                tones = listOf(
                    ToneGenerator.TONE_DTMF_2,
                    ToneGenerator.TONE_DTMF_5,
                    ToneGenerator.TONE_DTMF_8,
                    ToneGenerator.TONE_DTMF_0
                ),
                durationMs = 130,
                delayMs = 145L
            )
        )

        // Gentle descending "oops" — soft, not scary
        val INCORRECT_TONE_VARIANTS = listOf(
            ToneVariant(
                tones = listOf(
                    ToneGenerator.TONE_DTMF_8,
                    ToneGenerator.TONE_DTMF_5
                ),
                durationMs = 180,
                delayMs = 200L
            ),
            ToneVariant(
                tones = listOf(
                    ToneGenerator.TONE_DTMF_7,
                    ToneGenerator.TONE_DTMF_4
                ),
                durationMs = 170,
                delayMs = 190L
            ),
            ToneVariant(
                tones = listOf(
                    ToneGenerator.TONE_DTMF_9,
                    ToneGenerator.TONE_DTMF_6
                ),
                durationMs = 175,
                delayMs = 195L
            )
        )
    }
}
