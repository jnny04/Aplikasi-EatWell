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

    // Data mentah dari Repository (Isinya data 1 minggu)
    val intakes = repository.intakes
    val userGoal = repository.dailyGoal

    // 1. STATE PILIHAN FILTER (Untuk WeeklyRecap)
    private val _filterMode = MutableStateFlow("Today")
    val filterMode: StateFlow<String> = _filterMode.asStateFlow()

    fun setFilterMode(mode: String) {
        _filterMode.value = mode
    }

    init {
        repository.fetchWeeklyIntakes()
    }

    // 2. DATA FILTERED (Untuk WeeklyRecap)
    val displayedIntakes: StateFlow<List<IntakeEntry>> =
        combine(intakes, filterMode) { list, mode ->
            val today = LocalDate.now()
            if (mode == "Today") {
                list.filter { it.date == today }
            } else {
                list
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 3. TOTAL KALORI FILTERED (Untuk WeeklyRecap: bisa harian/mingguan tergantung dropdown)
    val totalCaloriesDisplayed: StateFlow<Int> =
        displayedIntakes.map { list ->
            list.sumOf { it.calories }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // ✅ TAMBAHAN BARU: TARGET KALORI DINAMIS
    // Jika mode "This Week", target dikali 7. Jika "Today", target harian biasa.
    val targetCaloriesDisplayed: StateFlow<Int> =
        combine(userGoal, filterMode) { goal, mode ->
            if (mode == "This Week") {
                goal * 7 // Target Mingguan
            } else {
                goal     // Target Harian
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // ✅ 4. [PERBAIKAN] TOTAL KALORI KHUSUS HARI INI (Untuk HomePage & IntakeDetail)
    // Ini wajib ada agar file lain tidak error
    val totalCaloriesToday: StateFlow<Int> = intakes.map { list ->
        val today = LocalDate.now()
        list.filter { it.date == today }.sumOf { it.calories }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // ✅ 5. [PERBAIKAN] MACROS KHUSUS HARI INI (Untuk HomePage & IntakeDetail)
    val totalCarbsToday: StateFlow<Int> = intakes.map { list ->
        val today = LocalDate.now()
        list.filter { it.date == today }.sumOf { it.carbs }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val totalProteinToday: StateFlow<Int> = intakes.map { list ->
        val today = LocalDate.now()
        list.filter { it.date == today }.sumOf { it.protein }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val totalFatToday: StateFlow<Int> = intakes.map { list ->
        val today = LocalDate.now()
        list.filter { it.date == today }.sumOf { it.fat }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)


    // Target Macros (Hitungan User)
    val targetCarbs: StateFlow<Int> = userGoal.map { (it * 0.50 / 4).toInt() }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val targetProtein: StateFlow<Int> = userGoal.map { (it * 0.20 / 4).toInt() }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val targetFat: StateFlow<Int> = userGoal.map { (it * 0.30 / 9).toInt() }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun addEntry(name: String, carbs: Int, protein: Int, fat: Int, imageUrl: String? = null) {
        val totalCals = (carbs * 4) + (protein * 4) + (fat * 9)

        val newEntry = IntakeEntry(
            id = System.currentTimeMillis(), // ID sementara
            name = if (name.isBlank()) "Unknown Food" else name,
            calories = totalCals,
            date = LocalDate.now(),
            imageUrl = imageUrl,
            carbs = carbs, protein = protein, fat = fat
        )
        repository.addIntake(newEntry)    }
}