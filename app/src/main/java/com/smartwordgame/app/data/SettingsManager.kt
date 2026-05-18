package com.smartwordgame.app.data

import android.content.Context

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("smart_word_game_settings", Context.MODE_PRIVATE)

    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var smartPracticeEnabled: Boolean
        get() = prefs.getBoolean("smart_practice_enabled", true)
        set(value) = prefs.edit().putBoolean("smart_practice_enabled", value).apply()
}
