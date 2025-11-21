package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun IntakeRecapPageWeekly(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: IntakeViewModel = hiltViewModel()
) {
    val serifFont = FontFamily(Font(R.font.source_serif_pro_regular))
    val serifBold = FontFamily(Font(R.font.source_serif_pro_bold))

    // Ambil data dari ViewModel
    val intakeList by viewModel.todayIntakes.collectAsState()
    val totalCalories by viewModel.totalCaloriesToday.collectAsState()

    val targetCalories by viewModel.userGoal.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // === HEADER (POSISI TETAP) ===
        TopBorder(navController = navController)

        Text(
            text = "My Intake",
            color = Color.Black,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold),
            modifier = Modifier.align(Alignment.TopStart).offset(x = 16.dp, y = 118.dp)
        )

        // More Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.TopStart).offset(x = 360.dp, y = 154.dp).requiredWidth(36.dp).clickable { navController.navigate(Screen.MonthlyRecap.route) }
        ) {
            Image(painter = painterResource(id = R.drawable.icroundreadmore), contentDescription = null, colorFilter = ColorFilter.tint(Color(0xff5ca135)), modifier = Modifier.requiredSize(36.dp))
            Text(text = "More", color = Color(0xff5ca135), textAlign = TextAlign.Center, fontSize = 8.sp, modifier = Modifier.offset(y = (-10).dp))
        }

        // Info This Week
        Image(painter = painterResource(id = R.drawable.iconamoonhistoryfill), contentDescription = null, colorFilter = ColorFilter.tint(Color.Black), modifier = Modifier.align(Alignment.TopStart).offset(x = 16.dp, y = 164.dp).requiredSize(20.dp))
        Text(text = "This Week", color = Color.Black, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold), modifier = Modifier.align(Alignment.TopStart).offset(x = 41.dp, y = 165.dp))

        // Calories Taken Text
        Text(text = "Calories Taken", color = Color.Black, textAlign = TextAlign.Center, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold), modifier = Modifier.align(Alignment.TopStart).offset(x = 150.dp, y = 201.dp))

        // === TOTAL CALORIES ===
        // ✅ Menggunakan Row agar posisi dinamis (tidak tertumpuk)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 198.dp, y = 223.dp), // Posisi utama tetap
            verticalAlignment = Alignment.Bottom // Ratakan teks di bagian bawah
        ) {
            // Angka Kalori yang sudah dimakan (Besar)
            Text(
                text = "$totalCalories",
                color = Color(0xfffc7100),
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold)
            )

            Spacer(modifier = Modifier.width(2.dp)) // Beri jarak sedikit

            // Target Kalori (Kecil)
            Text(
                text = "/$targetCalories calories",
                color = Color(0xfffc7100),
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = serifFont),
                // Sedikit padding bawah agar sejajar manis dengan angka besar
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
            Text(text = "See details", color = Color(0xff555555), textDecoration = TextDecoration.Underline, fontSize = 10.sp)
            Image(painter = painterResource(id = R.drawable.materialsymbolsdoublearrowrounded), contentDescription = null, colorFilter = ColorFilter.tint(Color(0xff555555)), modifier = Modifier.requiredSize(12.dp))
        }

        // Divider vertikal
        Divider(color = Color.Black.copy(alpha = 0.3f), modifier = Modifier.align(Alignment.TopStart).offset(x = 397.dp, y = 361.dp).requiredWidth(120.dp).rotate(-90f))


        // === LIST MAKANAN (SCROLLABLE) ===
        // Menggunakan padding top agar tidak menutupi header
        if (intakeList.isEmpty()) {
            // Tampilan kosong (Default)
            Text(
                text = "No intake logged yet.",
                modifier = Modifier.align(Alignment.Center).offset(y = 50.dp),
                color = Color.Gray,
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
                    .padding(top = 380.dp, bottom = 100.dp) // ✅ Jarak aman dari header & bottom nav
                    .align(Alignment.TopCenter) // ✅ Dimulai dari atas ke bawah (bukan menumpuk di bawah)
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
                .background(Color(0xFFF0F0F0)) // ✅ Background abu-abu jika tidak ada gambar
        ) {
            if (entry.imageUrl != null) {
                AsyncImage(
                    model = entry.imageUrl,
                    contentDescription = entry.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                    // ❌ Placeholder default dihapus agar bersih
                )
            } else {
                // Jika manual log (tidak ada URL), biarkan kosong atau bisa kasih icon simple
                // Disini dibiarkan box abu-abu polos sesuai request "kosong"
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Nama Makanan
        Text(
            text = entry.name,
            fontFamily = serifBold,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 1
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