package com.example.recappage.ui.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.ui.theme.SourceSans3
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import com.example.recappage.R
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.navigation.Screen
import androidx.compose.material3.MaterialTheme // 👈 WAJIB IMPORT INI

@Composable
fun MainGoalScreen(navController: NavHostController, viewModel: RegistrationViewModel) {

    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val goals = listOf(
        "Lose Weight", "Maintain Weight", "Gain Weight", "Build Muscle",
        "Improve Fitness", "Eat Healthier", "Track Nutrition", "Medical / Special Diet"
    )

    Scaffold(
        topBar = {
            TopBorder(navController = navController, showProfile = false)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // 🔥 GANTI: Background halaman menggunakan tema
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "What is your main goal?",
                fontSize = 24.sp,
                // 🔥 GANTI: Warna Teks Judul Utama (sesuaikan dengan warna tema)
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontFamily = SourceSerifPro,
                modifier = Modifier
                    .padding(top = 60.dp, bottom = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp)) // 👈 jarak tambahan di sini

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                goals.forEach { goal ->
                    GoalButton(
                        label = goal,
                        isSelected = viewModel.mainGoal.value == goal,
                        onClick = { viewModel.mainGoal.value = goal }
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            ContinueButton(
                onClick = {
                    if (viewModel.mainGoal.value == null) {
                        Toast.makeText(context, "Pilih tujuan utama Anda", Toast.LENGTH_SHORT).show()
                    } else if (!isLoading) {
                        isLoading = true
                        viewModel.createAccountAndSaveProfile(
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(context, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.SetupScreen.route) {
                                    popUpTo(Screen.SignIn.route) { inclusive = true }
                                }
                            },
                            onFailure = { err ->
                                isLoading = false
                                Toast.makeText(context, "Error: $err", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            )

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}


// =================================================================
// KOMPONEN PENDUKUNG
// =================================================================

// (GoalButton TIDAK BERUBAH, sudah "bodoh" dan benar)
@Composable
fun GoalButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val imageRes = if (isSelected) R.drawable.maingoalbuttonon else R.drawable.maingoalbutton

    // 🔥 GANTI: Warna Teks tombol Goal menggunakan tema
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Text(
            text = label,
            fontSize = 16.sp,
            color = textColor, // 🔥 Terapkan warna dinamis
            fontFamily = SourceSans3,
            fontWeight = FontWeight.Medium
        )
    }
}

// (ContinueButton TIDAK BERUBAH, sudah "bodoh" dan benar)
@Composable
fun ContinueButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.continueorange),
            contentDescription = "Continue Button",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}