package com.example.recappage.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recappage.data.FoodRepository
import com.example.recappage.model.FoodRecipes
import com.example.recappage.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    val searchResult = MutableLiveData<NetworkResult<FoodRecipes>>()
    val suggestions = MutableLiveData<List<String>>()   // Real-time suggestion list

    private var suggestionJob: Job? = null   // untuk debounce input


    // ---------------------------------------------------
    // 🔍 UTAMA — SEARCH RESULT (tampilkan setelah enter)
    // ---------------------------------------------------
    fun searchRecipes(query: String) = viewModelScope.launch {
        searchResult.value = NetworkResult.Loading()

        try {
            val response = repository.searchRecipes(query)
            searchResult.value = handleResponse(response)

        } catch (e: Exception) {
            searchResult.value = NetworkResult.Error("Search failed: ${e.message}")
        }
    }


    private fun handleResponse(response: Response<FoodRecipes>): NetworkResult<FoodRecipes> {
        return when {
            response.body() == null ->
                NetworkResult.Error("No data found")

            response.isSuccessful ->
                NetworkResult.Success(response.body()!!)

            else ->
                NetworkResult.Error(response.message())
        }
    }



    // ---------------------------------------------------
    // ✨ REAL-TIME LIVE SUGGESTION (tiap huruf berubah)
    // dengan 300ms debounce biar ga spam API
    // ---------------------------------------------------
    fun loadSuggestions(query: String) {

        // kosong → clear suggestion
        if (query.isBlank()) {
            suggestions.value = emptyList()
            return
        }

        // stop request sebelumnya
        suggestionJob?.cancel()

        suggestionJob = viewModelScope.launch {

            delay(250)   // Debounce biar smooth ketika user mengetik

            try {
                val response = repository.searchRecipes(query)

                if (response.isSuccessful) {
                    val titles = response.body()?.recipes
                        ?.map { it.title }
                        ?.distinct()
                        ?.take(15)          // max 15 hasil biar bersih
                        ?: emptyList()

                    suggestions.value = titles
                } else {
                    suggestions.value = emptyList()
                }

            } catch (_: Exception) {
                suggestions.value = emptyList()
            }
        }
    }
}
