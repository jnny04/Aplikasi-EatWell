package com.example.recappage.utils

import android.content.Context

object AmbientLightPreferences {
    private const val PREF_NAME = "sensor_settings"
    private const val KEY_SENSOR_ENABLED = "sensor_enabled"

    fun setEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SENSOR_ENABLED, enabled).apply()
    }

    fun isEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SENSOR_ENABLED, true) // default ON
    }
}
