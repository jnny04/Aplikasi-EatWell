package com.example.recappage.data

import android.content.Context
import android.util.Log // ✅ Tambahkan ini
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.time.LocalDate

class StreakPreferences(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_streak_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        const val KEY_LAST_OPEN_DATE = "last_open_date"
        const val KEY_STREAK_COUNT = "streak_count"
    }

    fun updateStreak(): Int {
        val today = LocalDate.now() // Format Date Object
        val todayStr = today.toString() // Format String "2025-11-29"

        val lastOpenDateStr = sharedPreferences.getString(KEY_LAST_OPEN_DATE, null)
        val currentStreak = sharedPreferences.getInt(KEY_STREAK_COUNT, 0)

        Log.d("STREAK_DEBUG", "📅 Hari ini: $todayStr | Terakhir Buka: $lastOpenDateStr | Streak Lama: $currentStreak")

        val newStreak = if (lastOpenDateStr == null) {
            // Kasus 1: Baru pertama kali install
            1
        } else {
            val lastOpenDate = LocalDate.parse(lastOpenDateStr)

            when {
                // Kasus 2: Buka di hari yang SAMA (Jangan nambah, jangan reset)
                lastOpenDate == today -> currentStreak

                // Kasus 3: Buka KEMARIN (Streak Lanjut +1)
                lastOpenDate == today.minusDays(1) -> currentStreak + 1

                // Kasus 4: Lupa buka (Reset jadi 1)
                else -> 1
            }
        }

        // Simpan data baru
        sharedPreferences.edit().apply {
            putString(KEY_LAST_OPEN_DATE, todayStr)
            putInt(KEY_STREAK_COUNT, newStreak)
            apply()
        }

        Log.d("STREAK_DEBUG", "✅ Streak Diupdate Menjadi: $newStreak")
        return newStreak
    }

    fun getStreak(): Int {
        return sharedPreferences.getInt(KEY_STREAK_COUNT, 0)
    }
}