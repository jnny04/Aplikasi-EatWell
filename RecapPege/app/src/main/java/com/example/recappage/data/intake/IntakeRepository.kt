package com.example.recappage.data.intake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IntakeRepository {

    private val _state = MutableStateFlow(IntakeState())
    val state: StateFlow<IntakeState> = _state

    fun addIntake(name: String, calories: Int) {
        val newItem = IntakeItem(name, calories)

        val newTotal = _state.value.totalCalories + calories
        val newList = _state.value.items + newItem

        _state.value = IntakeState(
            totalCalories = newTotal,
            items = newList
        )
    }
}
