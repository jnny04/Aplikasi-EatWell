package com.example.recappage.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

val Context.dataStore by preferencesDataStore("streak_prefs")

class StreakPreferences(private val context: Context) {

    companion object {
        val LAST_OPEN_DATE = stringPreferencesKey("last_open_date")
        val STREAK_COUNT = intPreferencesKey("streak_count")
    }

    suspend fun updateStreak(): Int {
        val prefs = context.dataStore.data.first()

        val today = LocalDate.now()
        val lastOpen = prefs[LAST_OPEN_DATE]?.let { LocalDate.parse(it) }
        val currentStreak = prefs[STREAK_COUNT] ?: 0

        val newStreak = when {
            lastOpen == null -> 1 // pertama kali
            lastOpen == today -> currentStreak // sudah buka hari ini
            lastOpen == today.minusDays(1L) -> currentStreak + 1 // lanjut streak
            else -> 1 // reset streak
        }

        context.dataStore.edit { settings ->
            settings[LAST_OPEN_DATE] = today.toString()
            settings[STREAK_COUNT] = newStreak
        }

        return newStreak
    }

    fun getStreakFlow(): Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[STREAK_COUNT] ?: 0
    }
}