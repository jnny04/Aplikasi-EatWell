package com.example.recappage.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.net.Uri // Pastikan ini di-import
import com.google.firebase.storage.FirebaseStorage // Import Storage
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import androidx.compose.runtime.mutableIntStateOf // ✅ Import ini

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
    // ✅ 1. TAMBAHAN STATE BARU: Untuk menampung angka goal
    var dailyCalorieGoal = mutableIntStateOf(0)


    // Instance untuk Storage
    private val storage = FirebaseStorage.getInstance()

    // State untuk menyimpan URL gambar dari Firestore
    val profileImageUrl = mutableStateOf<String?>(null)

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
    fun loadUserProfile(onLoaded: () -> Unit = {}, onError: (String) -> Unit = {}) {
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
                    // ✅ Ambil URL gambar yang tersimpan
                    profileImageUrl.value = snapshot.getString("profileImageUrl")

                    val savedGoal = snapshot.getLong("dailyCalorieGoal")?.toInt() ?: 0
                    dailyCalorieGoal.intValue = savedGoal
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

    // ✅ 3. FUNGSI BARU: Khusus untuk menyimpan hasil hitungan dari SetupScreen
    fun saveCalculatedGoal(goal: Int) {
        val uid = auth.currentUser?.uid ?: return

        val data = mapOf(
            "dailyCalorieGoal" to goal
        )

        // Gunakan set(..., SetOptions.merge()) atau update
        db.collection("users").document(uid)
            .update(data)
            .addOnSuccessListener {
                dailyCalorieGoal.intValue = goal // Update state lokal juga
            }
            .addOnFailureListener { e ->
                println("Gagal simpan goal: ${e.message}")
            }
    }


    // ------------------------------------------------------
    // ✅ UPDATE profil dari ProfileScreen ke Firestore
    // ------------------------------------------------------
    fun updateUserProfile(
        context: Context, // <--- TAMBAHAN BARU: Butuh Context untuk baca gambar
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run {
            onFailure("User belum login.")
            return
        }

        // Jika user memilih gambar baru
        if (imageUri != null) {
            val storageRef = storage.reference.child("profile_images/$uid")

            try {
                // === MULAI PROSES KOMPRESI ===

                // 1. Baca gambar dari Uri menjadi Bitmap
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)

                // 2. Siapkan wadah untuk hasil kompresi
                val baos = ByteArrayOutputStream()

                // 3. Kompres! Format JPEG, Kualitas 50% (Ini mengurangi ukuran drastis tapi tetap jelas di HP)
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)

                // 4. Ubah menjadi kumpulan byte (ByteArray)
                val data = baos.toByteArray()

                // === SELESAI KOMPRESI ===

                // 5. Upload menggunakan 'putBytes' (Bukan putFile lagi)
                storageRef.putBytes(data)
                    .addOnSuccessListener {
                        // Jika sukses upload, ambil URL-nya
                        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            val newImageUrl = downloadUri.toString()
                            saveProfileDataToFirestore(uid, newImageUrl, onSuccess, onFailure)
                        }.addOnFailureListener {
                            onFailure("Gagal mendapatkan URL download.")
                        }
                    }
                    .addOnFailureListener { e ->
                        onFailure("Upload gambar gagal: ${e.localizedMessage}")
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                onFailure("Gagal memproses gambar: ${e.localizedMessage}")
            }

        } else {
            // Jika tidak ada gambar baru, simpan data teks saja (pakai URL lama)
            saveProfileDataToFirestore(uid, profileImageUrl.value, onSuccess, onFailure)
        }
    }

    // ✅ TAMBAHKAN FUNGSI HELPER BARU INI
    // Fungsi ini akan menyimpan data ke Firestore.
    // Ini adalah isi dari `updateUserProfile` Anda yang lama, tapi dimodifikasi.
    private fun saveProfileDataToFirestore(
        uid: String,
        imageUrl: String?, // URL gambar (bisa baru atau lama)
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
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
            "mainGoal" to mainGoal.value,
            "profileImageUrl" to imageUrl // ✅ Simpan URL gambar di sini
        )

        db.collection("users").document(uid)
            .update(data)
            .addOnSuccessListener {
                // ✅ Update state lokal setelah sukses
                profileImageUrl.value = imageUrl
                onSuccess()
            }
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
