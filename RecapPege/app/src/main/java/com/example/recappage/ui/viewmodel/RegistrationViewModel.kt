package com.example.recappage.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationViewModel : ViewModel() {

    val isLoading = mutableStateOf(false)
    val loadError = mutableStateOf<String?>(null)
    // Firebase instance
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // --- Registration Data ---
    val email = mutableStateOf("")
    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val confirmPassword = mutableStateOf("")

    // --- Personal Info ---
    val gender = mutableStateOf<String?>(null) // "MALE"/"FEMALE"
    val age = mutableStateOf("")
    val height = mutableStateOf("")
    val weight = mutableStateOf("")
    val birthYear = mutableStateOf("")
    val birthMonth = mutableStateOf("")
    val birthDate = mutableStateOf("")

    // --- Preferences ---
    val allergies = mutableStateOf<Set<String>>(emptySet())
    val diets = mutableStateOf<Set<String>>(emptySet())
    val likedFoods = mutableStateOf<Set<String>>(emptySet())
    val mainGoal = mutableStateOf<String?>(null)


    // ------------------------------------------------------
    // ✅ Membuat akun + menyimpan profil USER pertama kali
    // ------------------------------------------------------
    fun createAccountAndSaveProfile(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (password.value != confirmPassword.value) {
            onFailure("Password tidak sesuai.")
            return
        }

        if (email.value.isEmpty() || username.value.isEmpty()) {
            onFailure("Email dan Username tidak boleh kosong.")
            return
        }

        auth.createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onFailure(task.exception?.localizedMessage ?: "Register gagal.")
                    return@addOnCompleteListener
                }

                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                val data = buildUserProfileMap(uid)

                db.collection("users").document(uid)
                    .set(data)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        onFailure(e.localizedMessage ?: "Gagal menyimpan profil.")
                    }
            }
    }

    // ------------------------------------------------------
    // ✅ LOAD data dari Firestore -> masuk ke state ViewModel
    // ------------------------------------------------------
    fun loadUserProfile(
        onLoaded: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val uid = auth.currentUser?.uid ?: return

        isLoading.value = true
        loadError.value = null

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    email.value = snapshot.getString("email") ?: ""
                    username.value = snapshot.getString("username") ?: ""

                    gender.value = snapshot.getString("gender")
                    age.value = snapshot.getString("age") ?: ""
                    height.value = snapshot.getString("height") ?: ""
                    weight.value = snapshot.getString("weight") ?: ""

                    allergies.value = (snapshot.get("allergies") as? List<String>)?.toSet() ?: emptySet()
                    diets.value = (snapshot.get("diets") as? List<String>)?.toSet() ?: emptySet()
                    likedFoods.value = (snapshot.get("likedFoods") as? List<String>)?.toSet() ?: emptySet()

                    mainGoal.value = snapshot.getString("mainGoal")
                }

                isLoading.value = false
                onLoaded()
            }
            .addOnFailureListener { e ->
                isLoading.value = false
                loadError.value = e.localizedMessage ?: "Gagal memuat profil."
                onError(loadError.value!!)
                isLoading.value = false
            }
    }


    // ------------------------------------------------------
    // ✅ UPDATE profil dari ProfileScreen ke Firestore
    // ------------------------------------------------------
    fun updateUserProfile(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run {
            onFailure("User belum login.")
            return
        }

        val data = mapOf(
            "email" to email.value,
            "username" to username.value,
            "gender" to gender.value,
            "age" to age.value,
            "height" to height.value,
            "weight" to weight.value,
            "allergies" to allergies.value.toList(),
            "diets" to diets.value.toList(),
            "likedFoods" to likedFoods.value.toList(),
            "mainGoal" to mainGoal.value
        )

        db.collection("users").document(uid)
            .update(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onFailure(e.localizedMessage ?: "Gagal update profil.")
            }
    }

    // ------------------------------------------------------
    // ✅ Perhitungan Kalori (Mifflin-St Jeor) — TIDAK DIHILANGKAN
    // ------------------------------------------------------
    fun calculateCalorieGoal(): Int {
        val weightKg = weight.value.toDoubleOrNull() ?: 0.0
        val heightCm = height.value.toDoubleOrNull() ?: 0.0
        val ageYears = age.value.toIntOrNull() ?: 0

        val userGender = gender.value // "MALE" / "FEMALE"
        val goal = mainGoal.value

        if (weightKg == 0.0 || heightCm == 0.0 || ageYears == 0 || userGender == null) {
            return 1800 // default
        }

        // BMR (Mifflin-St Jeor)
        val bmr = if (userGender == "MALE") {
            (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) + 5
        } else {
            (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) - 161
        }

        val calorieGoal = when (goal) {
            "Lose Weight" -> bmr - 500
            "Gain Weight" -> bmr + 500
            "Build Muscle" -> bmr + 250
            else -> bmr
        }

        return calorieGoal.toInt()
    }

    // ------------------------------------------------------
    // Helper untuk membuat map data Firestore
    // ------------------------------------------------------
    private fun buildUserProfileMap(uid: String): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "email" to email.value,
            "username" to username.value,
            "gender" to gender.value,
            "age" to age.value,
            "height" to height.value,
            "weight" to weight.value,
            "birthday" to "${birthDate.value}-${birthMonth.value}-${birthYear.value}",
            "allergies" to allergies.value.toList(),
            "diets" to diets.value.toList(),
            "likedFoods" to likedFoods.value.toList(),
            "mainGoal" to mainGoal.value
        )
    }
}
