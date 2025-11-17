package com.example.recappage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recappage.model.IntakeEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class IntakeViewModel @Inject constructor(
    // nanti kalau mau Room/Repository tinggal tambahkan di sini
) : ViewModel() {
    private val _intakes = MutableStateFlow<List<IntakeEntry>>(emptyList())
    val intakes: StateFlow<List<IntakeEntry>> = _intakes.asStateFlow()

    // hanya yang tanggalnya hari ini
    val todayIntakes: StateFlow<List<IntakeEntry>> =
        intakes
            .map { list ->
                val today = LocalDate.now()
                list.filter { it.date == today }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // total kalori hari ini
    val totalCaloriesToday: StateFlow<Int> =
        todayIntakes
            .map { list -> list.sumOf { it.calories } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun addIntake(name: String, calories: Int) {
        viewModelScope.launch {
            val current = _intakes.value
            val newId = (current.maxOfOrNull { it.id } ?: 0L) + 1L

            val newEntry = IntakeEntry(
                id = newId,
                name = name,
                calories = calories
            )
            _intakes.value = current + newEntry
        }
    }
}
