package com.example.recappage.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recappage.data.Repository
import com.example.recappage.data.local.RecipeEntity
import com.example.recappage.model.FoodRecipes
import com.example.recappage.model.NutritionWidgetResponse
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

    // ============================================================
    // STATE: Recipes list (RESULT FINAL DIPAKAI FoodLibraryPage)
    // ============================================================
    val recipesResponse: MutableLiveData<NetworkResult<FoodRecipes>> = MutableLiveData()


    // ============================================================
    // POPUP DETAIL STATES
    // ============================================================

    private val _recipeDetail = MutableStateFlow<RecipeInformation?>(null)
    val recipeDetail = _recipeDetail.asStateFlow()

    private val _nutritionDetail = MutableStateFlow<NutritionWidgetResponse?>(null)
    val nutritionDetail = _nutritionDetail.asStateFlow()

    val macroData = MutableStateFlow<MacroData?>(null)


    // ============================================================
    // LOAD RECIPE DETAIL (FULL INFO + NUTRITION)
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
    //  UNIVERSAL LOADER — RANDOMRECIPES & COMPLEXSEARCH (AUTO SWITCH)
    // ======================================================================
    private suspend fun getRecipesSafeCall(
        queries: Map<String, String>,
        useComplexSearch: Boolean = false
    ) {
        recipesResponse.value = NetworkResult.Loading()

        if (!hasInternetConnection()) {
            loadFromCache()
            return
        }

        try {
            val response =
                if (useComplexSearch) repository.remote.searchRecipes(queries)
                else repository.remote.getRandomRecipes(queries)

            val networkResult = handleFoodRecipesResponse(response)

            if (networkResult is NetworkResult.Success && networkResult.data != null) {
                saveToCache(networkResult.data)
            }

            recipesResponse.value = networkResult

        } catch (e: Exception) {
            recipesResponse.value = NetworkResult.Error("Error: ${e.message}")
        }
    }


    // ======================================================================
    // LOADERS FOR FOOD LIBRARY
    // ======================================================================

    fun loadAllRecipes() = viewModelScope.launch {
        val q = mapOf(
            "number" to "30",
            "addRecipeInformation" to "true",
            "fillIngredients" to "true",
            "apiKey" to ApiConfig.API_KEY
        )
        getRecipesSafeCall(q, useComplexSearch = false)
    }

    fun loadRecipesByTag(tag: String?) = viewModelScope.launch {
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
        val q = FilterMapper.toQueries(filter)
        getRecipesSafeCall(q, useComplexSearch = false)
    }


    // ======================================================================
    // SEARCH (COMPLEX SEARCH)
    // ======================================================================
    fun searchRecipesByKeyword(keyword: String) = viewModelScope.launch {

        val q = mapOf(
            "query" to keyword,
            "number" to "30",
            "addRecipeInformation" to "true",
            "apiKey" to ApiConfig.API_KEY
        )

        getRecipesSafeCall(q, useComplexSearch = true)
    }



    // ======================================================================
    // RESPONSE HANDLER
    // ======================================================================
    private fun handleFoodRecipesResponse(response: Response<FoodRecipes>): NetworkResult<FoodRecipes> {

        val body = response.body()

        return when {
            response.message().contains("timeout") ->
                NetworkResult.Error("Timeout")

            response.code() == 402 ->
                NetworkResult.Error("API Key Limited.")

            body?.recipes.isNullOrEmpty() ->
                NetworkResult.Error("Recipes not found.")

            response.isSuccessful ->
                NetworkResult.Success(body!!)

            else ->
                NetworkResult.Error(response.message())
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
                recipesResponse.postValue(
                    NetworkResult.Success(
                        FoodRecipes(cached.map { it.toRecipe() })
                    )
                )
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
