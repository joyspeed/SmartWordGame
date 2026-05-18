package com.smartwordgame.app.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

class SoundManager(var soundEnabled: Boolean = true) {
    private val handler = Handler(Looper.getMainLooper())
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)

    fun playCorrect() {
        playSequence(
            tones = listOf(
                ToneGenerator.TONE_DTMF_5,
                ToneGenerator.TONE_DTMF_7,
                ToneGenerator.TONE_DTMF_9
            )
        )
    }

    fun playIncorrect() {
        playSequence(
            tones = listOf(
                ToneGenerator.TONE_DTMF_9,
                ToneGenerator.TONE_DTMF_7,
                ToneGenerator.TONE_DTMF_5
            )
        )
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        toneGenerator.release()
    }

    private fun playSequence(tones: List<Int>) {
        if (!soundEnabled) return

        handler.removeCallbacksAndMessages(null)
        tones.forEachIndexed { index, tone ->
            handler.postDelayed(
                { toneGenerator.startTone(tone, TONE_DURATION_MS) },
                index * TONE_DELAY_MS
            )
        }
    }

    private companion object {
        const val TONE_DURATION_MS = 120
        const val TONE_DELAY_MS = 140L
    }
}
