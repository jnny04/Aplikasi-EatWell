package com.example.recappage.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.components.TopBorder
import androidx.compose.material3.MaterialTheme // 👈 WAJIB IMPORT INI

@Composable
fun SetupScreen(navController: NavHostController, viewModel: RegistrationViewModel) {

    val calculatedGoal = viewModel.calculateCalorieGoal()
    val dailyCalorieGoal = String.format("%,d", calculatedGoal)

    // ✅ 2. TAMBAHAN BARU: Simpan otomatis saat layar muncul
    LaunchedEffect(Unit) {
        // Simpan angka hasil hitungan ke Firestore
        viewModel.saveCalculatedGoal(calculatedGoal)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // 🔥 GANTI: background(Color.White) → background(MaterialTheme.colorScheme.background)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Header Eatwell
        TopBorder(navController = navController, showProfile = false)

        // ✅ Icon centang hijau
        Image(
            painter = painterResource(id = R.drawable.saved),
            contentDescription = "Saved Icon",
            modifier = Modifier
                .size(180.dp)
                .padding(top = 60.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Title besar
        Text(
            text = "You're all set up!",
            fontSize = 36.sp,
            // 🔥 GANTI: color = Color(0xFF5CA135) → primary
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontFamily = SourceSerifPro,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        // ✅ Paragraph kecil
        Text(
            text = "Great job! Your account is ready, your goals are set, and now\nit's time to begin tracking your intake for a healthier you.",
            fontSize = 12.sp,
            // 🔥 GANTI: color = Color(0xFF555555) → onSurface.copy(alpha = 0.7f)
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontFamily = SourceSans3,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp  // ✅ jarak baris lebih rapat
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ✅ Kotak kalori (calorie_box)
        Box(
            modifier = Modifier
                .width(273.dp)
                .height(142.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.calorie_box),
                contentDescription = "Calorie Box",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // ✅ "Your daily net calorie goal is:"
                Text(
                    text = "Your daily net calorie goal is:",
                    fontSize = 16.sp,
                    // 🔥 GANTI: color = Color(0xFF555555) → onSurface.copy(alpha = 0.7f)
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontFamily = SourceSans3
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ Angka kalori + Calories
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dailyCalorieGoal,
                        fontSize = 28.sp,
                        // 🔥 GANTI: color = Color.Black → onSurface
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SourceSerifPro
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Calories",
                        fontSize = 16.sp,
                        // 🔥 GANTI: color = Color.Black → onSurface
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = SourceSans3
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ✅ Button "Here We Go!"
        HereWeGoButton(navController)
    }
}

// =================================================================
// KOMPONEN TOMBOL
// =================================================================

@Composable
fun HereWeGoButton(navController: NavHostController) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                navController.navigate("home_page")
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.herewego),
            contentDescription = "Here We Go Button",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}