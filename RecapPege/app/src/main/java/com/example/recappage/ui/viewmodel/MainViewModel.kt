package com.example.recappage.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recappage.data.Repository
import com.example.recappage.data.local.RecipeEntity
import com.example.recappage.model.FoodRecipes
import com.example.recappage.model.NutritionWidgetResponse
import com.example.recappage.model.Recipe
import com.example.recappage.model.RecipeInformation
import com.example.recappage.util.ApiConfig
import com.example.recappage.util.FilterMapper
import com.example.recappage.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

// ===============================
//  DATA CLASS MACROS
// ===============================
data class MacroData(
    val calories: String,
    val protein: String,
    val carbs: String,
    val fat: String
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    // Status Network (Loading, Error, Success) untuk Feedback UI
    val recipesResponse: MutableLiveData<NetworkResult<FoodRecipes>> = MutableLiveData()

    // Detail Resep & Nutrisi
    private val _recipeDetail = MutableStateFlow<RecipeInformation?>(null)
    val recipeDetail = _recipeDetail.asStateFlow()

    private val _nutritionDetail = MutableStateFlow<NutritionWidgetResponse?>(null)
    val nutritionDetail = _nutritionDetail.asStateFlow()

    val macroData = MutableStateFlow<MacroData?>(null)

    // ============================================================
    // 🔥 PAGINATION STATE (List Utama untuk UI)
    // ============================================================
    // Gunakan list ini untuk menampilkan data di FoodLibraryPage
    private val _paginatedRecipes = mutableStateListOf<Recipe>()
    val paginatedRecipes: List<Recipe> get() = _paginatedRecipes

    private var currentOffset = 0
    var isPaginating by mutableStateOf(false)
    var endReached by mutableStateOf(false)

    // ============================================================
    // LOAD RECIPE DETAIL
    // ============================================================
    fun loadRecipeDetail(id: Int) = viewModelScope.launch {
        try {
            val info = repository.remote.getRecipeInformation(id)
            if (info.isSuccessful) _recipeDetail.value = info.body()

            val nut = repository.remote.getNutritionDetail(id)
            if (nut.isSuccessful) {
                _nutritionDetail.value = nut.body()
                macroData.value = extractMacros(nut.body())
            }
        } catch (e: Exception) {
            println("DETAIL ERROR: ${e.message}")
        }
    }

    private fun extractMacros(n: NutritionWidgetResponse?): MacroData? {
        if (n == null) return null
        fun find(name: String) = n.nutrients.firstOrNull { it.name == name }
        return MacroData(
            calories = "${find("Calories")?.amount?.toInt() ?: 0} kcal",
            protein = "${find("Protein")?.amount?.toInt() ?: 0} g",
            carbs = "${find("Carbohydrates")?.amount?.toInt() ?: 0} g",
            fat = "${find("Fat")?.amount?.toInt() ?: 0} g"
        )
    }

    // ======================================================================
    //  SAFE CALL (RANDOM / LOAD ALL / FILTER)
    // ======================================================================
    private suspend fun getRecipesSafeCall(
        queries: Map<String, String>,
        useComplexSearch: Boolean = false
    ) {
        recipesResponse.value = NetworkResult.Loading()

        if (!hasInternetConnection()) {
            loadFromCache() // Jika offline, ambil dari cache
            return
        }

        try {
            val response = if (useComplexSearch) {
                repository.remote.searchRecipes(queries)
            } else {
                repository.remote.getRandomRecipes(queries)
            }

            val networkResult = handleFoodRecipesResponse(response)

            // Jika sukses, simpan ke cache DAN update list pagination UI
            if (networkResult is NetworkResult.Success && networkResult.data != null) {
                saveToCache(networkResult.data)

                // Update List Utama UI
                _paginatedRecipes.clear()
                _paginatedRecipes.addAll(networkResult.data.recipes)
            }

            recipesResponse.value = networkResult

        } catch (e: Exception) {
            recipesResponse.value = NetworkResult.Error("Error: ${e.message}")
        }
    }

    // ======================================================================
    // LOADERS (HOME / LIBRARY AWAL)
    // ======================================================================

    fun loadAllRecipes() = viewModelScope.launch {
        // Reset pagination karena ini load awal/kategori baru
        resetPagination()

        val q = mapOf(
            "number" to "20",
            "addRecipeInformation" to "true",
            "fillIngredients" to "true",
            "apiKey" to ApiConfig.API_KEY
        )
        getRecipesSafeCall(q, useComplexSearch = false)
    }

    fun loadRecipesByTag(tag: String?) = viewModelScope.launch {
        resetPagination()
        val q = mutableMapOf(
            "number" to "30",
            "addRecipeInformation" to "true",
            "fillIngredients" to "true",
            "apiKey" to ApiConfig.API_KEY
        )
        if (!tag.isNullOrEmpty()) q["tags"] = tag

        getRecipesSafeCall(q, useComplexSearch = false)
    }

    fun loadWithFilter(filter: FilterState) = viewModelScope.launch {
        resetPagination()
        val q = FilterMapper.toQueries(filter)
        getRecipesSafeCall(q, useComplexSearch = false)
    }

    // ======================================================================
    // 🔍 SEARCH & PAGINATION LOGIC
    // ======================================================================

    fun resetPagination() {
        _paginatedRecipes.clear()
        currentOffset = 0
        endReached = false
    }

    // Dipanggil saat user pertama kali tekan Enter/Search
    fun searchRecipesByKeyword(keyword: String) = viewModelScope.launch {
        resetPagination() // Bersihkan data lama
        loadNextPage(keyword) // Load halaman pertama
    }

    // Dipanggil saat user scroll ke bawah (Load More)
    fun loadNextPage(keyword: String) = viewModelScope.launch {
        // Cegah loading dobel atau jika data sudah habis
        if (isPaginating || endReached) return@launch

        isPaginating = true

        // Kita gunakan Map Query agar kompatibel dengan RemoteDataSource yang sudah ada
        val queries = mapOf(
            "query" to keyword,
            "number" to "30",
            "offset" to currentOffset.toString(), // 👈 Offset dikirim lewat Map
            "addRecipeInformation" to "true",
            "apiKey" to ApiConfig.API_KEY
        )

        try {
            // Gunakan searchRecipes yang menerima Map (sudah ada di RemoteDataSource)
            val response = repository.remote.searchRecipes(queries)

            if (response.isSuccessful) {
                val newRecipes = response.body()?.recipes ?: emptyList()

                if (newRecipes.isNotEmpty()) {
                    _paginatedRecipes.addAll(newRecipes) // Append data baru
                    currentOffset += 30 // Naikkan offset
                } else {
                    endReached = true // Data habis
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isPaginating = false
        }
    }

    // ======================================================================
    // RESPONSE HANDLER
    // ======================================================================
    private fun handleFoodRecipesResponse(response: Response<FoodRecipes>): NetworkResult<FoodRecipes> {
        val body = response.body()
        return when {
            response.message().contains("timeout") -> NetworkResult.Error("Timeout")
            response.code() == 402 -> NetworkResult.Error("API Key Limited.")
            body?.recipes.isNullOrEmpty() -> NetworkResult.Error("Recipes not found.")
            response.isSuccessful -> NetworkResult.Success(body!!)
            else -> NetworkResult.Error(response.message())
        }
    }

    // ======================================================================
    // LOCAL CACHE (ROOM)
    // ======================================================================
    private suspend fun saveToCache(data: FoodRecipes) {
        val cached = data.recipes.map { RecipeEntity.fromRecipe(it) }
        repository.local.clearRecipes()
        repository.local.insertRecipes(cached)
    }

    private fun loadFromCache() = viewModelScope.launch {
        repository.local.readRecipes().collectLatest { cached ->
            if (cached.isNotEmpty()) {
                val recipes = cached.map { it.toRecipe() }

                // Update State Response
                recipesResponse.postValue(NetworkResult.Success(FoodRecipes(recipes)))

                // Update List UI juga agar sinkron
                _paginatedRecipes.clear()
                _paginatedRecipes.addAll(recipes)
            } else {
                recipesResponse.postValue(NetworkResult.Error("No Internet & No Cache"))
            }
        }
    }

    // ======================================================================
    // INTERNET CHECKER
    // ======================================================================
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}