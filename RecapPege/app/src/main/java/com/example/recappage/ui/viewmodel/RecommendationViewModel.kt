package com.example.recappage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recappage.data.FoodRepository
import com.example.recappage.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _foodList = MutableStateFlow<List<Recipe>>(emptyList())
    val foodList = _foodList.asStateFlow()

    private val _randomFood = MutableStateFlow<Recipe?>(null)
    val randomFood = _randomFood.asStateFlow()

    val isLoading = MutableStateFlow(false)

    // ======================================================
    fun loadFoods() {
        viewModelScope.launch {
            try {
                val response = repository.getRandomRecipes(
                    number = 10,
                    tags = null
                )

                if (response.isSuccessful) {
                    _foodList.value = response.body()?.recipes ?: emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ======================================================
    private fun sanitizeTags(raw: String): String {
        return raw.trim()
            .lowercase()
            .replace(", ", ",")
            .replace(" ,", ",")
            .removeSuffix(",")
    }

    private fun applyHalalRules(tags: String): Pair<String, String?> {
        val includeTags = tags.split(",").map { it.trim() }.toMutableList()
        var excludeTags: String? = null

        if (includeTags.contains("halal")) {
            excludeTags =
                "pork,bacon,lard,ham,prosciutto,wine,rum,beer,whiskey,brandy"
            includeTags.remove("halal")
        }

        return Pair(includeTags.joinToString(","), excludeTags)
    }

    // ======================================================
    fun spin(dietary: String) {
        viewModelScope.launch {

            if (dietary.isBlank()) {
                _randomFood.value = null
                return@launch
            }

            isLoading.value = true

            try {
                val cleanTags = sanitizeTags(dietary)
                val (includeTags, excludeTags) = applyHalalRules(cleanTags)

                val response = repository.getRandomRecipes(
                    number = 25,
                    tags = includeTags.ifBlank { null },
                    excludeTags = excludeTags
                )

                if (response.isSuccessful) {
                    val recipes = response.body()?.recipes ?: emptyList()
                    _randomFood.value = if (recipes.isNotEmpty()) recipes.random() else null
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            isLoading.value = false
        }
    }
}
