package com.example.recappage.data

import com.example.recappage.model.IntakeEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntakeRepository @Inject constructor() {

    private val _intakes = MutableStateFlow<List<IntakeEntry>>(emptyList())
    val intakes: StateFlow<List<IntakeEntry>> = _intakes.asStateFlow()

    // ✅ 1. TAMBAHKAN VARIABLE UNTUK TARGET KALORI (Default 2000 jika belum diset)
    private val _dailyGoal = MutableStateFlow(2000)
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    init {
        // ✅ 2. PANGGIL FUNGSI LOAD DATA SAAT REPOSITORY DIBUAT
        fetchUserData()
    }

    // Fungsi Simpan
    fun addIntake(entry: IntakeEntry) {
        val uid = auth.currentUser?.uid ?: return
        val intakeMap = hashMapOf(
            "id" to entry.id,
            "name" to entry.name,
            "calories" to entry.calories,
            "date" to entry.date.toString(),
            "imageUrl" to entry.imageUrl,
            // ✅ SIMPAN MACROS KE FIREBASE
            "carbs" to entry.carbs,
            "protein" to entry.protein,
            "fat" to entry.fat
        )

        db.collection("users").document(uid)
            .collection("intakes")
            .document(entry.id.toString())
            .set(intakeMap)
            .addOnSuccessListener {
                val currentList = _intakes.value
                _intakes.value = currentList + entry
            }
    }

    // Fungsi Load
    fun fetchTodayIntakes() {
        val uid = auth.currentUser?.uid ?: return
        val todayStr = java.time.LocalDate.now().toString()

        db.collection("users").document(uid).collection("intakes")
            .whereEqualTo("date", todayStr)
            .get()
            .addOnSuccessListener { documents ->
                val loadedList = mutableListOf<IntakeEntry>()
                for (document in documents) {
                    // ... (ambil id, name, calories, dateStr, imageUrl tetap sama)
                    val id = document.getLong("id") ?: 0L
                    val name = document.getString("name") ?: "Unknown"
                    val calories = document.getLong("calories")?.toInt() ?: 0
                    val dateStr = document.getString("date") ?: todayStr
                    val imageUrl = document.getString("imageUrl")
                    val date = java.time.LocalDate.parse(dateStr)

                    // ✅ AMBIL DATA MACROS (Default 0 jika data lama belum punya)
                    val carbs = document.getLong("carbs")?.toInt() ?: 0
                    val protein = document.getLong("protein")?.toInt() ?: 0
                    val fat = document.getLong("fat")?.toInt() ?: 0

                    loadedList.add(IntakeEntry(id, name, calories, date, imageUrl, carbs, protein, fat))
                }
                _intakes.value = loadedList
            }
    }

    // ✅ 3. TAMBAHKAN FUNGSI INI UNTUK AMBIL DATA USER
    private fun fetchUserData() {
        val uid = auth.currentUser?.uid ?: return

        // Mengambil data dari dokumen user
        db.collection("users").document(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                if (snapshot != null && snapshot.exists()) {
                    // Pastikan nama field di Firestore kamu adalah "calorieTarget" atau "bmr"
                    // Sesuaikan string "calorieTarget" dengan nama field di database kamu!
                    val target = snapshot.getLong("dailyCalorieGoal")?.toInt()
                        ?: 2000 // Default jika masih kosong

                    _dailyGoal.value = target
                }
            }
    }
}