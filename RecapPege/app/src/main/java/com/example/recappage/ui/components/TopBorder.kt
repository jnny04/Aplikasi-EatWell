// File: TopBorder.kt
package com.example.recappage.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage // ✅ Pastikan import ini ada
import com.example.recappage.R
import com.example.recappage.ui.navigation.Screen

// ... (HeaderBackground biarkan saja, tidak perlu diubah) ...
@Composable
fun HeaderBackground(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.untitleddesign91),
        contentDescription = "Header background",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(100.dp)
    )
}

// ✅ PERBAIKAN DI SINI: ProfileButton menerima photoUrl
@Composable
fun ProfileButton(
    navController: NavHostController,
    photoUrl: String? = null, // Parameter baru (opsional)
    modifier: Modifier = Modifier
) {
    // Modifier umum untuk bentuk bulat dan klik
    val commonModifier = modifier
        .padding(8.dp)
        .size(52.dp)
        .clip(CircleShape)
        .clickable {
            navController.navigate(Screen.Profile.route)
        }

    if (photoUrl != null) {
        // 🔥 JIKA ADA URL: Pakai AsyncImage (Load dari Firebase)
        AsyncImage(
            model = photoUrl,
            contentDescription = "Profile",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.profile), // Loading state
            error = painterResource(id = R.drawable.profile),       // Error state
            modifier = commonModifier
        )
    } else {
        // 🔥 JIKA TIDAK ADA URL: Pakai Image Default
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "Profile",
            modifier = commonModifier
        )
    }
}

// ✅ PERBAIKAN DI SINI: TopBorder menerima photoUrl dan meneruskannya
@Composable
fun TopBorder(
    navController: NavHostController,
    showProfile: Boolean = true,
    photoUrl: String? = null, // Parameter baru (opsional)
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .requiredWidth(412.dp)
            .requiredHeight(100.dp)
    ) {
        // Kotak dasar (putih) dan bayangan
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .shadow(8.dp)
        )

        // Dekorasi (tidak dipisah)
        Surface(
            modifier = Modifier
                .padding(start = 334.dp, end = 6.dp, top = 14.dp, bottom = 14.dp)
                .shadow(4.dp)
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
                            .background(Color(0xffd9d9d9))
                            .border(BorderStroke(1.dp, Color(0xff898989)), CircleShape)
                    )
                }
            }
        }

        // Header Background
        HeaderBackground(modifier = Modifier.align(Alignment.TopCenter))

        // 🔥 PERUBAHAN ADA DI SINI 🔥
        if (showProfile) {
            ProfileButton(
                navController = navController,
                photoUrl = photoUrl,
                modifier = Modifier
                    .align(Alignment.TopEnd) // Posisi awal di pojok kanan atas
                    // 👇 TAMBAHKAN BARIS INI UNTUK MENGGESER
                    .padding(top = 20.dp, end = 20.dp)
            )
        }
    }
}