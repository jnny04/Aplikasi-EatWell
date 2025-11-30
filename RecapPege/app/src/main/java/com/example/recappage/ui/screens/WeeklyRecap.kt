package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme // ✅ Import MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.model.IntakeEntry
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.navigation.Screen
import com.example.recappage.ui.viewmodel.IntakeViewModel
// ✅ Tambahkan import RegistrationViewModel
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import androidx.compose.material3.HorizontalDivider

@Composable
fun IntakeRecapPageWeekly(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: IntakeViewModel = hiltViewModel(),
    // ✅ 1. Tambahkan Parameter ViewModel
    regViewModel: RegistrationViewModel = hiltViewModel()
) {
    val serifFont = FontFamily(Font(R.font.source_serif_pro_regular))
    val serifBold = FontFamily(Font(R.font.source_serif_pro_bold))

    // ✅ 2. Load Profile Data & Ambil URL
    LaunchedEffect(Unit) {
        regViewModel.loadUserProfile()
    }
    val profilePicUrl = regViewModel.profileImageUrl.value

    val intakeList by viewModel.displayedIntakes.collectAsState()
    val totalCalories by viewModel.totalCaloriesDisplayed.collectAsState()
    val targetCalories by viewModel.targetCaloriesDisplayed.collectAsState()
    val currentMode by viewModel.filterMode.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            // ✅ GANTI: Color.White -> background
            .background(MaterialTheme.colorScheme.background)
    ) {
        // === HEADER ===
        // ✅ 3. Teruskan URL ke TopBorder
        TopBorder(
            navController = navController,
            photoUrl = profilePicUrl
        )

        Text(
            text = "My Intake",
            // ✅ GANTI: Color.Black -> onBackground
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold),
            modifier = Modifier.align(Alignment.TopStart).offset(x = 16.dp, y = 118.dp)
        )

        // More Button (SUDAH DIPERBAIKI)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                // 1. Ubah Anchor ke Kanan Atas (TopEnd)
                .align(Alignment.TopEnd)

                // 2. Hapus x=360.dp, cukup atur Y nya saja biar tinggi tetap sama
                .offset(y = 154.dp)

                // 3. Beri jarak aman dari pinggir kanan layar
                .padding(end = 16.dp)

                .requiredWidth(36.dp)
                .clickable { navController.navigate(Screen.MonthlyRecap.route) }
        ) {
            Image(
                painter = painterResource(id = R.drawable.icroundreadmore),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color(0xff5ca135)),
                modifier = Modifier.requiredSize(36.dp)
            )
            Text(
                text = "More",
                color = Color(0xff5ca135),
                textAlign = TextAlign.Center,
                fontSize = 8.sp,
                modifier = Modifier.offset(y = (-10).dp)
            )
        }

        // === DROPDOWN FILTER ===
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 164.dp)
                .clickable { expanded = true }
        ) {
            Image(
                painter = painterResource(id = R.drawable.iconamoonhistoryfill),
                contentDescription = null,
                // ✅ GANTI: Color.Black -> onBackground
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.requiredSize(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = currentMode,
                // ✅ GANTI: Color.Black -> onBackground
                color = MaterialTheme.colorScheme.onBackground,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold)
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select Period",
                modifier = Modifier.size(24.dp),
                // ✅ GANTI: Color.Black -> onBackground
                tint = MaterialTheme.colorScheme.onBackground
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                // ✅ GANTI: Color.White -> surface
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(
                    text = { Text("Today", fontFamily = serifFont) },
                    onClick = {
                        viewModel.setFilterMode("Today")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("This Week", fontFamily = serifFont) },
                    onClick = {
                        viewModel.setFilterMode("This Week")
                        expanded = false
                    }
                )
            }
        }

        // Calories Taken Text
        // ✅ GANTI: Color.Black -> onBackground
        Text(text = "Calories Taken", color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold), modifier = Modifier.align(Alignment.TopStart).offset(x = 150.dp, y = 201.dp))

        // === TOTAL CALORIES DISPLAY ===
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 198.dp, y = 223.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "$totalCalories",
                color = Color(0xfffc7100),
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold)
            )

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = "/$targetCalories calories",
                color = Color(0xfffc7100),
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = serifFont),
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }

        // Button Log Manually
        Box(
            modifier = Modifier.align(Alignment.TopCenter).offset(y = 280.dp).height(40.dp).clip(RoundedCornerShape(20)).background(Color(0xff5ca135)).clickable { navController.navigate(Screen.IntakeDetail.route) }.padding(horizontal = 16.dp).shadow(0.dp, RoundedCornerShape(50))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                Image(painter = painterResource(id = R.drawable.ggadd), contentDescription = null, modifier = Modifier.size(12.dp), colorFilter = ColorFilter.tint(Color.White))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Log manually", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold)
            }
        }

        // See details
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.TopStart).offset(x = 16.dp, y = 334.dp).padding(bottom = 25.dp).clickable { navController.navigate(Screen.IntakeDetail.route) }.zIndex(1f)) {
            // ✅ GANTI: Color(0xff555555) -> onSurface (alpha 0.7)
            Text(text = "See details", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), textDecoration = TextDecoration.Underline, fontSize = 10.sp)
            // ✅ GANTI: Tint -> onSurface (alpha 0.7)
            Image(painter = painterResource(id = R.drawable.materialsymbolsdoublearrowrounded), contentDescription = null, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)), modifier = Modifier.requiredSize(12.dp))
        }

        // Divider vertikal
        HorizontalDivider(
            // ✅ GANTI: Color.Black.copy -> onBackground.copy
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 397.dp, y = 361.dp)
                .requiredWidth(120.dp)
                .rotate(-90f)
        )

        // === LIST MAKANAN ===
        if (intakeList.isEmpty()) {
            Text(
                text = if (currentMode == "Today") "No intake logged today." else "No intake logged this week.",
                modifier = Modifier.align(Alignment.Center).offset(y = 50.dp),
                // ✅ GANTI: Color.Gray -> onSurface (alpha 0.5)
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontFamily = serifFont
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 380.dp, bottom = 100.dp)
                    .align(Alignment.TopCenter)
            ) {
                items(intakeList) { entry ->
                    FoodCardItem(entry = entry, serifFont = serifFont, serifBold = serifBold)
                }
            }
        }

        Component18(modifier = Modifier.align(Alignment.BottomCenter), navController = navController)
    }
}

@Composable
fun FoodCardItem(entry: IntakeEntry, serifFont: FontFamily, serifBold: FontFamily) {
    Column(
        modifier = Modifier
            .width(165.dp)
            .wrapContentHeight()
            .background(Color.Transparent)
            .clickable { }
    ) {
        // Gambar Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                // ✅ GANTI: Color(0xFFF0F0F0) -> surface
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (entry.imageUrl != null) {
                AsyncImage(
                    // ✅ Optimize lagi
                    model = com.example.recappage.util.ImageHelper.optimizeUrl(entry.imageUrl),
                    contentDescription = entry.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 🔥 PERBAIKAN DI SINI (Nama Makanan)
        Text(
            text = entry.name,
            fontFamily = serifBold,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            // ✅ GANTI: Color.Black -> onSurface
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2, // ✅ Ubah jadi 2 baris (sebelumnya 1)
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, // ✅ Tambah titik-titik (...) jika masih kepanjangan
            lineHeight = 18.sp // ✅ Atur jarak antar baris agar rapi
        )

        // Kalori
        Text(
            text = "${entry.calories} Kcal",
            fontFamily = serifFont,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = Color(0xFFFC7100)
        )
    }
}