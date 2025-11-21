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
    // ✅ TAMBAHKAN INI: Ambil data goal dari repository
    val userGoal = repository.dailyGoal
    // ✅ 1. HITUNG TARGET MACRO OTOMATIS (StateFlow)
    // Rumus: Karbo 50%, Protein 20%, Lemak 30%

    val targetCarbs: StateFlow<Int> = userGoal.map { goal ->
        (goal * 0.50 / 4).toInt() // 1 gram karbo = 4 kalori
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val targetProtein: StateFlow<Int> = userGoal.map { goal ->
        (goal * 0.20 / 4).toInt() // 1 gram protein = 4 kalori
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val targetFat: StateFlow<Int> = userGoal.map { goal ->
        (goal * 0.30 / 9).toInt() // 1 gram lemak = 9 kalori
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    init {
        repository.fetchTodayIntakes()
    }

    // Filter khusus hari ini
    val todayIntakes: StateFlow<List<IntakeEntry>> =
        intakes.map { list ->
            val today = LocalDate.now()
            list.filter { it.date == today }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Hitung total kalori
    val totalCaloriesToday: StateFlow<Int> =
        todayIntakes.map { list ->
            list.sumOf { it.calories }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // ✅ 1. HITUNG TOTAL MACROS YANG SUDAH DIMAKAN
    val totalCarbsToday: StateFlow<Int> = todayIntakes.map { list ->
        list.sumOf { it.carbs }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val totalProteinToday: StateFlow<Int> = todayIntakes.map { list ->
        list.sumOf { it.protein }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val totalFatToday: StateFlow<Int> = todayIntakes.map { list ->
        list.sumOf { it.fat }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)


    // Fungsi untuk UI: Terima input, hitung kalori, kirim ke Repo
    // ✅ Update fungsi ini: Tambahkan parameter imageUrl
    fun addEntry(name: String, carbs: Int, protein: Int, fat: Int, imageUrl: String? = null) {
        val totalCals = (carbs * 4) + (protein * 4) + (fat * 9)

        val newEntry = IntakeEntry(
            id = System.currentTimeMillis(),
            name = if (name.isBlank()) "Unknown Food" else name,
            calories = totalCals,
            date = LocalDate.now(),
            imageUrl = imageUrl,
            // ✅ Masukkan data macros ke sini
            carbs = carbs,
            protein = protein,
            fat = fat
        )
        repository.addIntake(newEntry)
    }
}