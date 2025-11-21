package com.example.recappage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recappage.data.IntakeRepository
import com.example.recappage.model.IntakeEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class IntakeViewModel @Inject constructor(
    private val repository: IntakeRepository
) : ViewModel() {

    val intakes = repository.intakes

    // ✅ TAMBAHAN: Block Init
    // Kode di dalam sini akan dijalankan OTOMATIS saat ViewModel dibuat
    init {
        repository.fetchTodayIntakes()
    }

    val todayIntakes: StateFlow<List<IntakeEntry>> =
        intakes.map { list ->
            val today = LocalDate.now()
            list.filter { it.date == today }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val totalCaloriesToday: StateFlow<Int> =
        todayIntakes.map { list ->
            list.sumOf { it.calories }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun addIntake(name: String, calories: Int) {
        val newEntry = IntakeEntry(
            id = System.currentTimeMillis(),
            name = name,
            calories = calories
        )
        repository.addIntake(newEntry)
    }
}