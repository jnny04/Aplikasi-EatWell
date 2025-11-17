package com.example.recappage.ui.components

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
import androidx.compose.foundation.BorderStroke
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.ui.navigation.Screen

// ===========================================
// FUNGSI DIPISAHKAN
// ===========================================

/**
 * Komponen yang menampilkan latar belakang Header (untitleddesign91).
 */
@Composable
fun HeaderBackground(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.untitleddesign91),
        contentDescription = "Header background",
        contentScale = ContentScale.Crop,
        modifier = modifier
//            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .requiredHeight(100.dp)
    )
}

/**
 * Komponen tombol Profile yang dapat diklik.
 */
@Composable
fun ProfileButton(navController: NavHostController, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.profile),
        contentDescription = "Profile",
        modifier = modifier
//            .align(Alignment.TopEnd)
            .padding(8.dp)
            .size(80.dp)
            .clip(CircleShape)
            .clickable {
                navController.navigate(Screen.Profile.route)
            }
    )
}

// ===========================================
// TOPBORDER UTAMA (KOMBINASI)
// ===========================================

@Composable
fun TopBorder(
    navController: NavHostController,
    showProfile: Boolean = true, // ✅ Parameter kontrol
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .requiredWidth(412.dp)
            .requiredHeight(100.dp)
    ) {
        // Kotak dasar (putih) dan bayangan (tidak dipisah)
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

        // Panggil fungsi Header Background yang sudah dipisah
        HeaderBackground(modifier = Modifier.align(Alignment.TopCenter)
        )

        // Panggil fungsi Profile Button secara bersyarat
        if (showProfile) {
            ProfileButton(
                navController = navController,
                // ✅ TAMBAHKAN KEMBALI align() DI SINI (di dalam scope Box)
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}