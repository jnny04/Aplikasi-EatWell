package com.example.recappage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.recappage.ui.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

// 👇 Import Descope (Seharusnya tidak merah lagi setelah Langkah 1)
import com.descope.Descope

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 👇 INISIALISASI DESCOPE (Gunakan ID Terbaru kamu)
        try {
            // ID dari text yang kamu kirim: P35deW4J1H5rkOUS9ZTwb0hD1pbd
            Descope.setup(this, projectId = "P35deW4J1H5rkOUS9ZTwb0hD1pbd")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MaterialTheme {
                Surface {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}