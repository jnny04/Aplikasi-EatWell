package com.example.recappage.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.recappage.data.StreakPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Pastikan ini menerima Context
class StreakViewModel(context: Context) : ViewModel() {

    private val streakPrefs = StreakPreferences(context)

    private val _streakDays = MutableStateFlow(0)
    val streakDays = _streakDays.asStateFlow()

    init {
        // Load awal saat VM dibuat
        refreshStreak()
    }

    // Ubah nama jadi refreshStreak agar lebih jelas
    fun refreshStreak() {
        val updated = streakPrefs.updateStreak()
        _streakDays.value = updated
    }
}