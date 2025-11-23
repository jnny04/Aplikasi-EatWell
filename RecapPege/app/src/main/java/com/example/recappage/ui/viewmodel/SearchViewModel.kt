package com.example.recappage.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recappage.data.FoodRepository
import com.example.recappage.model.FoodRecipes
import com.example.recappage.model.SearchHistory // ✅ Pastikan file model ini sudah dibuat
import com.example.recappage.util.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import com.google.firebase.firestore.Query

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    val searchResult = MutableLiveData<NetworkResult<FoodRecipes>>()
    val suggestions = MutableLiveData<List<String>>()
    private var suggestionJob: Job? = null

    // ✅ Inisialisasi Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    // ✅ 1. VARIABLE UNTUK MENAMPUNG LIST HISTORY
    val searchHistory = MutableLiveData<List<String>>()

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
    // ✨ REAL-TIME LIVE SUGGESTION
    // ---------------------------------------------------
    fun loadSuggestions(query: String) {

        if (query.isBlank()) {
            suggestions.value = emptyList()
            return
        }

        suggestionJob?.cancel()

        suggestionJob = viewModelScope.launch {
            delay(250) // Debounce

            try {
                val response = repository.searchRecipes(query)

                if (response.isSuccessful) {
                    val titles = response.body()?.recipes
                        ?.map { it.title }
                        ?.distinct()
                        ?.take(15)
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

    // ---------------------------------------------------
    // ✅ FUNGSI BARU: SIMPAN HISTORY KE FIREBASE
    // ---------------------------------------------------
    fun saveSearchHistory(query: String) {
        val uid = auth.currentUser?.uid ?: return

        if (query.isBlank()) return

        // Pastikan class SearchHistory sudah dibuat di folder model
        val historyItem = SearchHistory(query = query)

        db.collection("users")
            .document(uid)
            .collection("search_history")
            .add(historyItem)
            .addOnSuccessListener {
                // Berhasil
            }
            .addOnFailureListener {
                // Gagal
            }
    }

    // ✅ 2. FUNGSI BARU: LOAD HISTORY DARI FIREBASE
    fun loadSearchHistory() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("search_history")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Urutkan dari yang terbaru
            .limit(10) // Ambil 10 terakhir saja biar rapi
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                if (snapshot != null) {
                    val historyList = snapshot.documents.mapNotNull { doc ->
                        doc.getString("query")
                    }
                    searchHistory.value = historyList
                }
            }
    }

} // ✅ Pastikan kurung tutup Class ada di paling akhir sini