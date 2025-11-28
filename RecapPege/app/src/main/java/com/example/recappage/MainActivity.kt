package com.example.recappage

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
// import androidx.activity.ComponentActivity <-- INI DIGANTI
import androidx.fragment.app.FragmentActivity // 👈 INI YANG BARU
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
import com.example.recappage.utils.AmbientLightPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
// 👇 UBAH ComponentActivity MENJADI FragmentActivity
class MainActivity : FragmentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null

    // Status objek dekat atau tidak
    private var isObjectNear by mutableStateOf(false)

    // Status apakah fitur proximity diaktifkan user
    private var isSensorFeatureEnabled by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Ambil sensor proximity
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        // Ambil preferensi user (ON / OFF)
        isSensorFeatureEnabled = AmbientLightPreferences.isEnabled(this)

        setContent {
            RecapPageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    Box(modifier = Modifier.fillMaxSize()) {

                        // Semua halaman aplikasi
                        AppNavigation(navController = navController)

                        // =============================
                        // EFEK DIM SAAT OBJEK MENDEKAT
                        // =============================
                        if (isSensorFeatureEnabled && isObjectNear) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Color.Black.copy(alpha = 0.45f)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Aktifkan sensor hanya jika fitur ON
        if (isSensorFeatureEnabled && proximitySensor != null) {
            sensorManager.registerListener(
                this,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]

            // Jika jarak sangat kecil → dianggap objek dekat
            isObjectNear = distance < event.sensor.maximumRange
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Tidak digunakan
    }
}