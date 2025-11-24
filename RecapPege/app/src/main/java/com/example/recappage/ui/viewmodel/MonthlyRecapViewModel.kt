package com.example.recappage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recappage.data.IntakeRepository
import com.example.recappage.model.IntakeEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MonthlyRecapViewModel @Inject constructor(
    private val repository: IntakeRepository
) : ViewModel() {

    // Data List Makanan Bulan Ini
    private val _monthlyIntakes = MutableStateFlow<List<IntakeEntry>>(emptyList())
    val monthlyIntakes: StateFlow<List<IntakeEntry>> = _monthlyIntakes.asStateFlow()

    // Bulan yang dipilih (String nama bulan)
    private val _selectedMonth = MutableStateFlow(LocalDate.now().month.name.lowercase().replaceFirstChar { it.uppercase() })
    val selectedMonth: StateFlow<String> = _selectedMonth.asStateFlow()

    // List Nama Bulan untuk mapping ke Angka
    private val monthsList = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    init {
        // Load data bulan saat ini ketika dibuka
        val currentMonth = LocalDate.now().monthValue
        loadDataForMonth(currentMonth)
    }

    fun selectMonth(monthName: String) {
        _selectedMonth.value = monthName
        val monthIndex = monthsList.indexOf(monthName) + 1 // Konversi nama ke angka (1-12)
        if (monthIndex > 0) {
            loadDataForMonth(monthIndex)
        }
    }

    private fun loadDataForMonth(month: Int) {
        val currentYear = LocalDate.now().year // Asumsi tahun ini
        repository.fetchMonthlyIntakes(currentYear, month) { data ->
            _monthlyIntakes.value = data
        }
    }
}