package com.example.recappage

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.recappage.ui.navigation.AppNavigation
import com.example.recappage.ui.theme.RecapPageTheme
import dagger.hilt.android.AndroidEntryPoint

// ✅ Tambahkan ini
import com.example.recappage.data.StreakPreferences

@AndroidEntryPoint
class MainActivity : FragmentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null

    private var isObjectNear by mutableStateOf(false)
    private var isSensorFeatureEnabled by mutableStateOf(true)

    // ✅ Tambahkan Streak Prefs di kode lama
    private lateinit var streakPrefs: StreakPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Ambil sensor proximity
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        // ✅ Inisialisasi StreakPreferences
        streakPrefs = StreakPreferences(this)

        setContent {
            RecapPageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    Box(modifier = Modifier.fillMaxSize()) {

                        AppNavigation(navController = navController)

                        // Overlay redup jika objek dekat
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

    override fun onResume() {
        super.onResume()

        // 🔥 Tetap: aktifkan sensor
        if (isSensorFeatureEnabled && proximitySensor != null) {
            sensorManager.registerListener(
                this,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        // 🔥 Tambahan: update streak otomatis setiap kali app dibuka
        val newStreak = streakPrefs.updateStreak()
        Log.d("LIFECYCLE_CHECK", "🎯 Streak ter-update: $newStreak")
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            val maxRange = event.sensor.maximumRange

            Log.d(
                "PROXIMITY_TEST",
                "🔍 Sensor Mendeteksi: $distance cm → ${if(distance < maxRange) "DEKAT" else "JAUH"}"
            )

            isObjectNear = distance < maxRange
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
