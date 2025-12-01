// File: TopBorder.kt
package com.example.recappage.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.ui.navigation.Screen

// =======================
// HEADER BACKGROUND
// =======================
@Composable
fun HeaderBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(100.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Image(
            painter = painterResource(id = R.drawable.eatwell),
            contentDescription = "Header logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)  // tengah
                .offset(y = 15.dp)         // sedikit turun
                .size(270.dp)              // atur besar logo di sini
        )
    }
}

// =======================
// PROFILE BUTTON
// =======================
@Composable
fun ProfileButton(
    navController: NavHostController,
    photoUrl: String? = null,
    modifier: Modifier = Modifier
) {
    // 1. Modifier Dasar (Ukuran dan Shape)
    val baseModifier = modifier
        .padding(7.dp)
        .size(60.dp)
        .clip(CircleShape)
        .clickable {
            navController.navigate(Screen.Profile.route)
        }

    // 2. Modifier Gambar Internal (Untuk memastikan skala)
    val imageModifier = Modifier
        .fillMaxSize()
        // 🔥 TAMBAHAN PENTING: Paksa gambar menutupi area 70dp
        .clip(CircleShape) // Penting untuk AsyncImage Placeholder

    // --- KASUS A: Ada Foto Profil (AsyncImage) ---
    if (photoUrl != null) {
        AsyncImage(
            model = photoUrl,
            contentDescription = "Profile",
            contentScale = ContentScale.Crop, // 🔥 PENTING: Paksa potong agar pas di lingkaran
            placeholder = painterResource(id = R.drawable.profile),
            error = painterResource(id = R.drawable.profile),
            modifier = baseModifier
        )
    }
    // --- KASUS B: Tidak Ada Foto (Image Placeholder) ---
    else {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "Profile",
            // 🔥 TAMBAHAN PENTING: Gunakan modifier yang sama dengan AsyncImage
            contentScale = ContentScale.Crop,
            modifier = baseModifier
        )
    }
}
// =======================
// TOP BORDER
// =======================
@Composable
fun TopBorder(
    navController: NavHostController,
    showProfile: Boolean = true,
    photoUrl: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()          // supaya tidak kepotong di device lain
            .requiredHeight(100.dp)
    ) {
        // Kotak dasar: warna sama dengan card Calories
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .shadow(8.dp)
        )

        // Dekorasi kanan (struktur lama, hanya warna ikut theme)
        Surface(
            modifier = Modifier
                .padding(start = 334.dp, end = 6.dp, top = 14.dp, bottom = 14.dp)
                .shadow(4.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(modifier = Modifier.requiredSize(72.dp)) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .requiredSize(72.dp)
                        .padding(20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.vector),
                        contentDescription = "Vector",
                        modifier = Modifier.requiredSize(32.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .requiredSize(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                CircleShape
                            )
                    )
                }
            }
        }

        // Header background dengan logo EATWELL
        HeaderBackground(modifier = Modifier.align(Alignment.TopCenter))

        if (showProfile) {
            ProfileButton(
                navController = navController,
                photoUrl = photoUrl,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 15.dp, end = 2.dp)
            )
        }
    }
}
