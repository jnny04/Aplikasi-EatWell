package com.example.recappage.data

import com.example.recappage.model.IntakeEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntakeRepository @Inject constructor() {

    private val _intakes = MutableStateFlow<List<IntakeEntry>>(emptyList())
    val intakes: StateFlow<List<IntakeEntry>> = _intakes.asStateFlow()

    // Inisialisasi Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ✅ FUNGSI 1: SIMPAN KE FIREBASE
    fun addIntake(entry: IntakeEntry) {
        val uid = auth.currentUser?.uid ?: return

        // 1. Siapkan data untuk dikirim (Format Map)
        // Kita ubah LocalDate jadi String ("2023-11-21") agar mudah disimpan
        val intakeMap = hashMapOf(
            "id" to entry.id,
            "name" to entry.name,
            "calories" to entry.calories,
            "date" to entry.date.toString()
        )

        // 2. Simpan ke sub-collection "intakes"
        db.collection("users").document(uid)
            .collection("intakes")
            .document(entry.id.toString()) // Gunakan ID unik sebagai nama dokumen
            .set(intakeMap)
            .addOnSuccessListener {
                // Jika sukses simpan di cloud, update juga tampilan lokal
                val currentList = _intakes.value
                _intakes.value = currentList + entry
                println("Sukses simpan ke Firestore: ${entry.name}")
            }
            .addOnFailureListener { e ->
                println("Gagal simpan ke Firestore: ${e.message}")
            }
    }

    // ✅ FUNGSI 2: AMBIL DATA DARI FIREBASE (LOAD)
    // Dipanggil saat aplikasi baru dibuka
    fun fetchTodayIntakes() {
        val uid = auth.currentUser?.uid ?: return
        val todayStr = LocalDate.now().toString() // Contoh: "2023-11-21"

        db.collection("users").document(uid)
            .collection("intakes")
            .whereEqualTo("date", todayStr) // Ambil CUMA yang tanggalnya hari ini
            .get()
            .addOnSuccessListener { documents ->
                val loadedList = mutableListOf<IntakeEntry>()

                for (document in documents) {
                    // Convert data Firestore kembali ke IntakeEntry
                    val id = document.getLong("id") ?: 0L
                    val name = document.getString("name") ?: "Unknown"
                    val calories = document.getLong("calories")?.toInt() ?: 0
                    val dateStr = document.getString("date") ?: todayStr

                    // Convert String balik ke LocalDate
                    val date = LocalDate.parse(dateStr)

                    loadedList.add(IntakeEntry(id, name, calories, date))
                }

                // Update StateFlow agar UI berubah
                _intakes.value = loadedList
                println("Sukses load ${loadedList.size} data dari Firestore")
            }
            .addOnFailureListener { e ->
                println("Gagal load data: ${e.message}")
            }
    }
}