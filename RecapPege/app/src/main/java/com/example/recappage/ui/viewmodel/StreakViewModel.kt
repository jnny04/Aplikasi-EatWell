package com.example.recappage.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recappage.data.StreakPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StreakViewModel(context: Context) : ViewModel() {

    private val streakPrefs = StreakPreferences(context)

    private val _streakDays = MutableStateFlow(0)
    val streakDays = _streakDays.asStateFlow()

    init {
        updateStreak()
    }

    fun updateStreak() {
        viewModelScope.launch {
            val updated = streakPrefs.updateStreak()
            _streakDays.value = updated
        }
    }
}
