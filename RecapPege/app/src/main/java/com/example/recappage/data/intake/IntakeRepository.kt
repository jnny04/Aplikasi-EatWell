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

        // 1. UPDATE STATE LOKAL DULUAN (OPTIMISTIC)
        // Kita langsung tambahkan entry baru ke list yang sedang tampil.
        // User akan melihat itemnya "instan" tanpa loading.
        val currentList = _intakes.value
        _intakes.value = currentList + entry

        // 2. PROSES UPLOAD KE FIREBASE (BACKGROUND)
        val intakeMap = hashMapOf(
            "id" to entry.id,
            "name" to entry.name,
            "calories" to entry.calories,
            "date" to entry.date.toString(),
            "imageUrl" to entry.imageUrl,
            "carbs" to entry.carbs,
            "protein" to entry.protein,
            "fat" to entry.fat
        )

        db.collection("users").document(uid)
            .collection("intakes")
            .document(entry.id.toString())
            .set(intakeMap)
            .addOnSuccessListener {
                // Sukses tersimpan di server.
                // Tidak perlu update UI lagi karena sudah kita lakukan di langkah 1.
            }
            .addOnFailureListener { e ->
                // 3. ROLLBACK (JIKA GAGAL)
                // Jika server menolak atau internet mati total, kita hapus lagi itemnya dari tampilan
                // agar data tetap konsisten (Jujur ke user).
                _intakes.value = _intakes.value.filter { it.id != entry.id }
                println("Gagal menyimpan ke server: ${e.message}")
            }
    }

    // Fungsi Load Mingguan
    fun fetchWeeklyIntakes() {
        val uid = auth.currentUser?.uid ?: return

        val today = LocalDate.now()
        // Hitung tanggal 6 hari yang lalu (total rentang 7 hari termasuk hari ini)
        val startOfWeekStr = today.minusDays(6).toString()

        db.collection("users").document(uid)
            .collection("intakes")
            .whereGreaterThanOrEqualTo("date", startOfWeekStr) // Filter: Tanggal >= 7 hari lalu
            .orderBy("date") // Urutkan berdasarkan tanggal
            .get()
            .addOnSuccessListener { documents ->
                val loadedList = mutableListOf<IntakeEntry>()

                for (document in documents) {
                    // Parse data Firestore ke Object IntakeEntry
                    val id = document.getLong("id") ?: 0L
                    val name = document.getString("name") ?: "Unknown"
                    val calories = document.getLong("calories")?.toInt() ?: 0
                    val dateStr = document.getString("date") ?: LocalDate.now().toString()
                    val imageUrl = document.getString("imageUrl")

                    // Ambil Macros
                    val carbs = document.getLong("carbs")?.toInt() ?: 0
                    val protein = document.getLong("protein")?.toInt() ?: 0
                    val fat = document.getLong("fat")?.toInt() ?: 0

                    val date = LocalDate.parse(dateStr)

                    loadedList.add(IntakeEntry(id, name, calories, date, imageUrl, carbs, protein, fat))
                }

                // Simpan semua data seminggu ke variable lokal
                _intakes.value = loadedList
            }
            .addOnFailureListener { e ->
                println("Error fetching weekly intakes: ${e.message}")
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

    // ... kode sebelumnya ...

    // ✅ FUNGSI BARU: MENGAMBIL DATA PER BULAN
    fun fetchMonthlyIntakes(year: Int, month: Int, callback: (List<IntakeEntry>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        // Format tanggal awal dan akhir bulan (yyyy-MM-dd)
        // Note: String format bulan harus 2 digit, misal "09" untuk September
        val monthStr = month.toString().padStart(2, '0')

        val startDate = "$year-$monthStr-01"
        // Untuk simplifikasi, kita ambil sampai akhir bulan dengan trik ambil tanggal 1 bulan depannya (exclusive)
        // atau kita ambil saja semua data yang stringnya diawali "$year-$monthStr" jika format konsisten
        val nextMonth = if (month == 12) 1 else month + 1
        val nextYear = if (month == 12) year + 1 else year
        val nextMonthStr = nextMonth.toString().padStart(2, '0')
        val endDate = "$nextYear-$nextMonthStr-01"

        db.collection("users").document(uid)
            .collection("intakes")
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThan("date", endDate)
            .orderBy("date")
            .get()
            .addOnSuccessListener { documents ->
                val loadedList = mutableListOf<IntakeEntry>()
                for (document in documents) {
                    val id = document.getLong("id") ?: 0L
                    val name = document.getString("name") ?: "Unknown"
                    val calories = document.getLong("calories")?.toInt() ?: 0
                    val dateStr = document.getString("date") ?: LocalDate.now().toString()
                    val imageUrl = document.getString("imageUrl")
                    val carbs = document.getLong("carbs")?.toInt() ?: 0
                    val protein = document.getLong("protein")?.toInt() ?: 0
                    val fat = document.getLong("fat")?.toInt() ?: 0
                    val date = LocalDate.parse(dateStr)

                    loadedList.add(IntakeEntry(id, name, calories, date, imageUrl, carbs, protein, fat))
                }
                callback(loadedList) // Kembalikan data ke ViewModel
            }
            .addOnFailureListener {
                callback(emptyList()) // Return kosong jika gagal
            }
    }
}