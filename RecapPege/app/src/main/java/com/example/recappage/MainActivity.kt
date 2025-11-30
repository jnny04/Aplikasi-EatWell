package com.example.recappage

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.example.recappage.ui.navigation.AppNavigation
import com.example.recappage.ui.theme.RecapPageTheme
import dagger.hilt.android.AndroidEntryPoint

// ✅ Import Fitur Kamu (Streak)
import com.example.recappage.data.StreakPreferences

// ✅ Import Fitur Karina (Descope)
import com.descope.Descope

// 🔥 IMPORT WAJIB UNTUK SPLASH SCREEN
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@AndroidEntryPoint
class MainActivity : FragmentActivity(), SensorEventListener {

    // --- Variabel Sensor (Punya Kamu) ---
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null

    private var isObjectNear by mutableStateOf(false)
    private var isSensorFeatureEnabled by mutableStateOf(true)

    // --- Variabel Streak (Punya Kamu) ---
    private lateinit var streakPrefs: StreakPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // 🔥 1. INSTALL SPLASH SCREEN (WAJIB PALING ATAS)
        // Ini agar logo muncul saat aplikasi baru dibuka
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // 🔥 2. HILANGKAN ACTION BAR SECARA PAKSA
        // Agar tulisan "RecapPage" di bar abu-abu hilang total
        actionBar?.hide()

        // ======================================================
        // 🔥 3. INISIALISASI DESCOPE (DARI CODINGAN KARINA)
        // ======================================================
        try {
            Descope.setup(this, projectId = "P35deW4J1H5rkOUS9ZTwb0hD1pbd")
            Log.d("DESCOPE_INIT", "Descope initialized successfully")
        } catch (e: Exception) {
            Log.e("DESCOPE_INIT", "Failed to init Descope", e)
            e.printStackTrace()
        }

        // ======================================================
        // 🔥 4. INISIALISASI SENSOR & STREAK (PUNYA KAMU)
        // ======================================================
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        streakPrefs = StreakPreferences(this)

        // ======================================================
        // 🔥 5. SET CONTENT (GABUNGAN UI)
        // ======================================================
        setContent {
            RecapPageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Box Utama (Untuk menumpuk Overlay Sensor)
                    Box(modifier = Modifier.fillMaxSize()) {

                        // Navigasi Utama Aplikasi
                        AppNavigation(navController = navController)

                        // Fitur Overlay Redup (Punya Kamu)
                        if (isSensorFeatureEnabled && isObjectNear) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.45f))
                            )
                        }
                    }
                }
            }
        }
    }

    // ======================================================
    // 🔥 LIFECYCLE METHODS (TETAP SAMA)
    // ======================================================

    override fun onResume() {
        super.onResume()

        // Aktifkan sensor jika tersedia
        if (isSensorFeatureEnabled && proximitySensor != null) {
            sensorManager.registerListener(
                this,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        // Update streak otomatis saat aplikasi dibuka kembali
        val newStreak = streakPrefs.updateStreak()
        Log.d("LIFECYCLE_CHECK", "🎯 Streak ter-update: $newStreak")
    }

    override fun onPause() {
        super.onPause()
        // Matikan sensor saat aplikasi di background agar hemat baterai
        sensorManager.unregisterListener(this)
    }

    // ======================================================
    // 🔥 LOGIKA SENSOR (TETAP SAMA)
    // ======================================================

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            val maxRange = event.sensor.maximumRange

            Log.d(
                "PROXIMITY_TEST",
                "🔍 Sensor Mendeteksi: $distance cm → ${if(distance < maxRange) "DEKAT" else "JAUH"}"
            )

            // Ubah state UI (Jauh/Dekat)
            isObjectNear = distance < maxRange
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Tidak digunakan, tapi wajib di-override
    }
}