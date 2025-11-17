package com.example.recappage.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.recappage.data.RecipeFirestoreModel
import com.example.recappage.data.toFirestoreModel
import com.example.recappage.data.toRecipe
import com.example.recappage.model.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class FavouriteViewModel @Inject constructor() : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _favourites = mutableStateListOf<Recipe>()
    val favourites: List<Recipe> get() = _favourites

    init {
        listenFavoritesRealtime()
    }

    /** -----------------------------------------
     *  🔥 REALTIME LISTENER (FIRESTORE → UI LIVE)
     *  ----------------------------------------- */
    private fun listenFavoritesRealtime() {
        if (userId == null) return

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FavouriteVM", "Snapshot error: ${e.message}")
                    return@addSnapshotListener
                }

                _favourites.clear()

                snapshot?.documents?.forEach { doc ->
                    doc.toObject(RecipeFirestoreModel::class.java)?.let { model ->
                        _favourites.add(model.toRecipe())
                    }
                }
            }
    }

    /** CEK apakah recipe sudah favorite */
    fun isFavorite(recipe: Recipe): Boolean {
        return _favourites.any { it.id == recipe.id }
    }

    /** TOGGLE: local state dulu → Firestore sync */
    fun toggleFavorite(recipe: Recipe) {
        val nowFav = !isFavorite(recipe)

        // Update UI local → langsung berubah
        if (nowFav) _favourites.add(recipe)
        else _favourites.removeAll { it.id == recipe.id }

        Log.i("FAV", "TOGGLE recipe=${recipe.id}, nowFav=$nowFav, size=${_favourites.size}")

        if (userId == null) return

        val doc = db.collection("users")
            .document(userId)
            .collection("favorites")
            .document(recipe.id.toString())

        if (nowFav) doc.set(recipe.toFirestoreModel())
        else doc.delete()
    }
}